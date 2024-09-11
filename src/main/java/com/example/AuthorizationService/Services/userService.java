package com.example.AuthorizationService.Services;

import com.example.AuthorizationService.DTO.loginRequestDTO;
import com.example.AuthorizationService.Repositories.userRepo;
import com.example.CentralRepository.models.Role;
import com.example.CentralRepository.models.RoleType;
import com.example.CentralRepository.models.Users;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class userService {
    private userRepo userrepo;
    private PasswordEncoder passwordEncoder;
    private final roleService roleService;

    public userService(userRepo userrepo, PasswordEncoder passwordEncoder, roleService roleService){
        this.userrepo=userrepo;
        this.passwordEncoder=passwordEncoder;
        this.roleService = roleService;
    }
    public Users saveUser(loginRequestDTO loginrequestDTO){
        Role role1= roleService.findByRoleType(RoleType.USER);
        Set<Role> role=new HashSet<>();
        if(role1==null){
            roleService.saveRole(RoleType.USER);
            role1= roleService.findByRoleType(RoleType.USER);
        }
        role.add(role1);
        Users user=Users.builder()
                .userName(loginrequestDTO.getEmail())
                .emailId(loginrequestDTO.getEmail())
                .password(passwordEncoder.encode(loginrequestDTO.getPass()))
                .phoneNumber(loginrequestDTO.getPhone())
                .roles(role)
                .build();
//        Role role2=roleService.findByRoleType(RoleType.ADMIN);
//        Set<Role> rolesetting=new HashSet<>();
//        rolesetting.add(role2);
//        user.setRoles(rolesetting);
        userrepo.save(user);
        return user;
    }

    public Users findUserByEmail(String Email){
       Optional<Users> user1= userrepo.findUserByEmailId(Email);
        return user1.orElse(null);
    }


}
