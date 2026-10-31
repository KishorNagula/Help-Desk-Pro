
package com.helpdesk.helpdeskpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.helpdesk.helpdeskpro.dto.LoginRequest;
import com.helpdesk.helpdeskpro.dto.LoginResponse;
import com.helpdesk.helpdeskpro.dto.RegisterRequest;
import com.helpdesk.helpdeskpro.dto.RegisterResponse;
import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.security.JwtService;
import com.helpdesk.helpdeskpro.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    @Autowired
    UserService userService;
    
    @Autowired
    JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        RegisterResponse response = new RegisterResponse(user);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
    

    @PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
        @RequestBody LoginRequest request) {

    		User user = userService.loginUser(
            request.getUsername(),
            request.getPassword()
    		);
    		
    		String token = jwtService.generateToken(user.getUsername());

    		LoginResponse response = new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            token
    				);

    return ResponseEntity.ok(response);
    }

}
