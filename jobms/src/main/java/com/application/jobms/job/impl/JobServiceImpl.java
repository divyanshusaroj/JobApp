package com.application.jobms.job.impl;

import com.application.jobms.job.Job;
import com.application.jobms.job.JobRepository;
import com.application.jobms.job.JobService;
import com.application.jobms.job.clients.CompanyClient;
import com.application.jobms.job.clients.ReviewClient;
import com.application.jobms.job.dto.JobDTO;
import com.application.jobms.job.external.Company;
import com.application.jobms.job.external.Review;
import com.application.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;

    @Autowired
    private RestTemplate restTemplate;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    int attempt = 0;

    public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient, ReviewClient reviewClient){

        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }


    //@CircuitBreaker(name="companyBreaker", fallbackMethod = "companyBreakerFallback")
    @RateLimiter(name="companyBreaker", fallbackMethod = "companyBreakerFallback")
    public List<JobDTO> findAll(){
        System.out.println("Attempt" + ++attempt);
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<JobDTO> companyBreakerFallback(Exception e){
        System.out.println("Rate limit exceeded: " + e.getClass().getSimpleName());
        return Collections.emptyList();

    }

    public JobDTO convertToDTO(Job job){
        Company company = companyClient.getCompany(job.getCompanyId());
        List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

        return JobMapper.mapToJobWithCompanyAndReview(job, company, reviews);
    }

    public void createJob(Job job){
        jobRepository.save(job);
    }

    public JobDTO findById(Long id){
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDTO(job);
    }

    public boolean deleteById(Long id){
          try{
              jobRepository.deleteById(id);
              return true;
          }catch (Exception e){
              return false;
        }
    }

    public boolean updateById(Long id, Job updateJob) {

            Optional<Job> jobOptional = jobRepository.findById(id);

            if(jobOptional.isPresent()){
                Job job = jobOptional.get();
                job.setTitle(updateJob.getTitle());
                job.setDescription(updateJob.getDescription());
                job.setLocation(updateJob.getLocation());
                job.setMinSalary(updateJob.getMinSalary());
                job.setMaxSalary(updateJob.getMaxSalary());
                jobRepository.save(job);
                return true;
            }
        return false;
    }
}
