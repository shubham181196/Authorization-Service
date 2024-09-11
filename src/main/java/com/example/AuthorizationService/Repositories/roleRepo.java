package com.example.AuthorizationService.Repositories;

import com.example.CentralRepository.models.Role;
import com.example.CentralRepository.models.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface roleRepo extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoletype(RoleType roletype);

}
