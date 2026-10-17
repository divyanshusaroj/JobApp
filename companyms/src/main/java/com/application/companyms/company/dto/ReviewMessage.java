package com.application.companyms.company.dto;

import lombok.Data;

@Data
public class ReviewMessage {
    private Long id;
    private String description;
    private double star;
    private Long companyId;

}
