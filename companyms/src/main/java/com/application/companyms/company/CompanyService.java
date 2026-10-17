package com.application.companyms.company;

import com.application.companyms.company.dto.ReviewMessage;

import java.util.List;


public interface CompanyService {
     void createCompany(Company company);
     List<Company> findAllCompanies();
     Company findCompanyById(Long id);
     boolean deleteCompany(Long id);
     boolean updateCompany(Long id, Company company);
     void updateCompanyRating(ReviewMessage reviewMessage);


}
