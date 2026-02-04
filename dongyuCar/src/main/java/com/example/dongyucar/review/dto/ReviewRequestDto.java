package com.example.dongyucar.review.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewRequestDto {
    private String title;
    private String content; // ReviewContent
    private List<MultipartFile> images;
}
