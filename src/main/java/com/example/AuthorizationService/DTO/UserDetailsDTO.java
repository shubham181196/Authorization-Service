package com.example.AuthorizationService.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class UserDetailsDTO {
    UUID userId;
    String email;
}
