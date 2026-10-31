
package com.helpdesk.helpdeskpro.dto;

import com.helpdesk.helpdeskpro.entity.Role;
import com.helpdesk.helpdeskpro.entity.User;

public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;

    public RegisterResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
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
}
