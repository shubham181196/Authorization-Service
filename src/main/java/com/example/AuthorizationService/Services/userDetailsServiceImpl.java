package com.example.AuthorizationService.Services;

import com.example.AuthorizationService.Repositories.userRepo;
import com.example.AuthorizationService.helper.AuthUser;
import com.example.CentralRepository.models.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class userDetailsServiceImpl implements UserDetailsService {

    private userRepo userrepo;

    public userDetailsServiceImpl(userRepo userrepo){
        this.userrepo=userrepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user=userrepo.findUserByEmailId(username);
        if(user.isPresent()){
            return new AuthUser(user.get());
        }else{
            throw new UsernameNotFoundException("User not found");
        }
       
    }
}
