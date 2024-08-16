package com.example.AuthorizationService.Repositories;

import com.example.CentralRepository.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface userRepo extends JpaRepository<Users, UUID> {
    Optional<Users> findUserByEmailId(String emailId);

}
