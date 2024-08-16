package com.example.AuthorizationService.DTO;


import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class signinRequestDTO {
    private String email;
    private String pass;
}
