package com.example.dongyucar.management.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateRequest {

    private String notes;

    @JsonProperty("isContacted") // JSON 키를 isContacted로 유지
    private Boolean contacted;
}
