
package com.helpdesk.helpdeskpro.dto;

import com.helpdesk.helpdeskpro.entity.Role;

public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private String token;

    public LoginResponse(Long id, String username,
                         String email, Role role, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
    
    public String getToken() {
        return token;
    }
}
