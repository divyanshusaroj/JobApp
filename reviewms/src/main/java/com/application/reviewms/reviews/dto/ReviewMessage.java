package com.application.reviewms.reviews.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReviewMessage {
    private Long id;
    private String description;
    private double star;
    private Long companyId;

}
