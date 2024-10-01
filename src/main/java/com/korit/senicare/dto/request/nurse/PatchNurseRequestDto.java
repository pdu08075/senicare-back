package com.korit.senicare.dto.request.nurse;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchNurseRequestDto {
    @NotBlank
    private String name;
}
