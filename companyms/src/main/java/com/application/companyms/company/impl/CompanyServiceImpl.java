package com.application.companyms.company.impl;

import com.application.companyms.company.Company;
import com.application.companyms.company.CompanyRepository;
import com.application.companyms.company.CompanyService;
import com.application.companyms.company.clients.ReviewClient;
import com.application.companyms.company.dto.ReviewMessage;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;
    private ReviewClient reviewClient;

    public CompanyServiceImpl(CompanyRepository companyRepository, ReviewClient reviewClient){
           this.companyRepository = companyRepository;
           this.reviewClient = reviewClient;
    }

    public void createCompany(Company company){
          companyRepository.save(company);
    }

    public List<Company> findAllCompanies(){
        return companyRepository.findAll();
    }

    public Company findCompanyById(Long id){
        return companyRepository.findById(id).orElse(null);
    }

    public boolean deleteCompany(Long id){
        Optional<Company> companyOptional = companyRepository.findById(id);
        if(companyOptional.isPresent()){
            Company company = companyOptional.get();
            companyRepository.delete(company);
            return true;
        }else{
            return false;
        }
    }

    public boolean updateCompany(Long id, Company updatedCompany){
        Optional<Company> companyOptional = companyRepository.findById(id);
        if(companyOptional.isPresent()){
            Company company = companyOptional.get();
            company.setName(updatedCompany.getName());
            company.setDescription(updatedCompany.getDescription());
            companyRepository.save(company);
            return true;
        }
        return false;
    }


    public void updateCompanyRating(ReviewMessage reviewMessage) {
        Company company = companyRepository.findById(reviewMessage.getCompanyId()).orElseThrow(() ->
                new NotFoundException("Company Not Found" + reviewMessage.getCompanyId()));
        double averageRating = reviewClient.getAverageRatingForCompany(reviewMessage.getCompanyId());
        company.setRating(averageRating);
        companyRepository.save(company);
    }


}
