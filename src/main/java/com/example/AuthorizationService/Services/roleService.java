package com.example.AuthorizationService.Services;

import com.example.AuthorizationService.DTO.saveRoleDTO;
import com.example.AuthorizationService.Repositories.roleRepo;

import com.example.CentralRepository.models.Role;
import com.example.CentralRepository.models.RoleType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class roleService {
    private final roleRepo rolerepo;
    public roleService(roleRepo rolerepo){
        this.rolerepo = rolerepo;
    }
    public Role findByRoleType(RoleType roleType){
        Optional<Role> role=rolerepo.findByRoletype(roleType);
        return role.orElse(null);
    }
    public void saveRoleByRoleType(saveRoleDTO saveRoledto){
        RoleType roleType=RoleType.valueOf(saveRoledto.getRoleType());

        Role role=Role.builder().roletype(roleType).build();
        rolerepo.save(role);
    }
}
