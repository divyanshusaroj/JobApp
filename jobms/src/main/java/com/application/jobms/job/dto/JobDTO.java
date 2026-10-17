package com.application.jobms.job.dto;

import com.application.jobms.job.external.Company;
import com.application.jobms.job.external.Review;
import lombok.Data;

import java.util.List;

@Data
public class JobDTO {
    private String title;
    private String description;
    private Long minSalary;
    private Long maxSalary;
    private String location;
    private Company company;
    private List<Review> reviews;
}
