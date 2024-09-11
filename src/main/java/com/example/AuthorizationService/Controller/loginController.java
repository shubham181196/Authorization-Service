package com.example.AuthorizationService.Controller;

import com.example.AuthorizationService.DTO.UserDetailsDTO;
import com.example.AuthorizationService.DTO.loginRequestDTO;

import com.example.AuthorizationService.DTO.saveRoleDTO;
import com.example.AuthorizationService.DTO.signinRequestDTO;
import com.example.AuthorizationService.Services.JwtService;
import com.example.AuthorizationService.Services.roleService;
import com.example.AuthorizationService.Services.userDetailsServiceImpl;
import com.example.AuthorizationService.Services.userService;
import com.example.CentralRepository.models.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import com.example.AuthorizationService.Services.JavaSmtpGmailSenderService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@Slf4j
@RestController
public class loginController {
    @Value("${cookie.expiry}")
    private int cookieExpiry;
    private final userService userservice;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final roleService roleservice;
    private final userDetailsServiceImpl userdetailsService;
    private final JavaSmtpGmailSenderService javaSmtpGmailSenderService;
    public loginController(userService userservice, AuthenticationManager authenticationManager, JwtService jwtService, roleService roleservice, userDetailsServiceImpl userdetailsService, JavaSmtpGmailSenderService javaSmtpGmailSenderService, JavaSmtpGmailSenderService javaSmtpGmailSenderService1){
        this.userservice=userservice;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleservice = roleservice;
        this.userdetailsService = userdetailsService;
        this.javaSmtpGmailSenderService = javaSmtpGmailSenderService1;
    }
    @GetMapping("/auth/currentuser")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request){
        String jwtToken="";

        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("Jwttoken")){
                    jwtToken=cookie.getValue();
                }
            }
        }else{
            System.out.println("req failed");
        }

        log.info("Inside current user controller :"+jwtToken);

        if(jwtToken.equals("")) return ResponseEntity.badRequest().body("Token invalid");
        else{
            Boolean isExpired=jwtService.isTokenExpired(jwtToken);
            if(isExpired) return ResponseEntity.badRequest().body("Token expired");
            String email=jwtService.extractEmail(jwtToken);
            Users user=userservice.findUserByEmail(email);
            UserDetailsDTO userDetailsDTO=UserDetailsDTO.builder().userId(user.getId()).email(email).build();
            return ResponseEntity.ok(userDetailsDTO);
        }
    }
    @PostMapping("/auth/signin")
    public ResponseEntity<?> signin(@RequestBody signinRequestDTO signinRequestDTO, HttpServletResponse response){
        log.info("Inside signin controller");
            try {
                UserDetails authUser= userdetailsService.loadUserByUsername(signinRequestDTO.getEmail());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(signinRequestDTO.getEmail(), signinRequestDTO.getPass(), authUser.getAuthorities()));
                if(authentication.isAuthenticated()) {
                    String token = jwtService.createToken(signinRequestDTO.getEmail());
                    ResponseCookie cookie = ResponseCookie.from("Jwttoken", token)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(cookieExpiry)
                            .build();
                    Users user=userservice.findUserByEmail(authUser.getUsername());
                    UserDetailsDTO userDetailsDTO=UserDetailsDTO.builder().userId(user.getId()).email(authUser.getUsername()).build();
                    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(userDetailsDTO);
                }
                // Successful authentication logic
            } catch (BadCredentialsException e) {
                System.out.println("Bad credentials");
            } catch (AuthenticationException e) {
                System.out.println("Auth invalid");
            }
        
            return new ResponseEntity<>("invalid user credentials", HttpStatus.UNAUTHORIZED);


    }
    
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup( @RequestBody loginRequestDTO loginRequestDTO){
        javaSmtpGmailSenderService.sendEmail(loginRequestDTO.getEmail(),"Email verification","OTP: 1234");
        System.out.println("hi s");
        return ResponseEntity.ok(userservice.saveUser(loginRequestDTO));

    }
    @GetMapping("/auth/validate")
    public ResponseEntity<?> validate(HttpServletRequest request){
        String token=request.getHeader("Jwttoken");
        System.out.println("in controller"+ token);

        if(token!=null) {
            String email = jwtService.extractEmail(token);

            UserDetails userDetails = userdetailsService.loadUserByUsername(email);
            Boolean verdict = jwtService.validateToken(token, userDetails.getUsername());
            if (verdict) {
                return ResponseEntity.ok("Authnetication success");
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized usesr");
            }
        }
            return ResponseEntity.badRequest().body("Invalid credentials");



    }

    @PostMapping("/resource/entry")
    public ResponseEntity<?> resource(@RequestBody signinRequestDTO signinRequestDTO ){
        return ResponseEntity.ok("resource access");

    }

    @PostMapping("/auth/addRole")
    public ResponseEntity<?> saveRole(@RequestBody saveRoleDTO saveRoledto){

        roleservice.saveRoleByRoleType(saveRoledto);
        return ResponseEntity.ok("role saved");
    }


}
