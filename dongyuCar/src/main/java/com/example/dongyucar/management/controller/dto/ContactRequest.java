package com.example.dongyucar.management.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {

    private String customerName;

    private String customerPhone;

    private String desiredModel;

}
