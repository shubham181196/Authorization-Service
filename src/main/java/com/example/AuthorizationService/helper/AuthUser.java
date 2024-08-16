package com.example.AuthorizationService.helper;

import com.example.CentralRepository.models.Role;
import com.example.CentralRepository.models.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthUser extends Users implements UserDetails{

    private String username;
    private String password;
    private Set<Role> roles;
//    @Autowired
//    private  userService userservice;
//    @Autowired
//    private roleService roleservice;
    public AuthUser(Users user1){
        this.username=user1.getEmailId();
        this.password=user1.getPassword();
        this.roles=user1.getRoles();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        roles.forEach(role -> {
            System.out.println(role.getRoletype().toString());
        });


        return roles.stream().map(role1->new SimpleGrantedAuthority("ROLE_"+role1.getRoletype().toString()))
                    .collect(Collectors.toSet());

    }


    @Override
    public String getUsername() {
        return this.username;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
