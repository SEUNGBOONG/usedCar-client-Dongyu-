package com.example.dongyucar.review.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class ReviewRequestDto {
    private String title;
    private String content; // 프론트에서 formData.append("content", ...)로 보내야 함
    private List<MultipartFile> images;
}
