package com.project.Elevate.userService.dto;


import lombok.Data;

@Data
public class LoginRequestDto {
    private String name, email, password;
}
