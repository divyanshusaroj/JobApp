package com.application.jobms.job;

import com.application.jobms.job.dto.JobDTO;

import java.util.List;


public interface JobService {
    List<JobDTO> findAll();
    void createJob(Job job);
    JobDTO findById(Long id);
    boolean deleteById(Long id);
    boolean updateById(Long id, Job updatedJob);
}
