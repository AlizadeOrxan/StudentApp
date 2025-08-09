package com.example.studentapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {

    private Long id;
    @NotBlank(message = "Must be entered")
    private String firstName;
    @NotBlank(message = "must be enetered")
    private  String lastName;
    int age ;
    private  String email;


}
