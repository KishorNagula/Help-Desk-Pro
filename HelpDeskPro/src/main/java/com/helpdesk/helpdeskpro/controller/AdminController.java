
package com.helpdesk.helpdeskpro.controller;

import java.util.List;
import java.util.Map;

import com.helpdesk.helpdeskpro.dto.AssignTicketRequest;
import com.helpdesk.helpdeskpro.entity.Ticket;
import com.helpdesk.helpdeskpro.service.TicketService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.helpdeskpro.dto.AdminCreateUserRequest;
import com.helpdesk.helpdeskpro.dto.AdminUserResponse;
import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.repository.UserRepository;
import com.helpdesk.helpdeskpro.service.UserService;

import org.springframework.web.bind.annotation.PathVariable;
import com.helpdesk.helpdeskpro.entity.Role;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TicketService ticketService;

    public AdminController(
            UserRepository userRepository,
            UserService userService,
            TicketService ticketService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(AdminUserResponse::new)
                .toList();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestBody AdminCreateUserRequest request) {

        if (request.getUsername() == null ||
            request.getUsername().isBlank() ||
            request.getEmail() == null ||
            request.getEmail().isBlank() ||
            request.getPassword() == null ||
            request.getPassword().isBlank() ||
            request.getRole() == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "All fields are required."));
        }

        try {
            User user = userService.registerUserByAdmin(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new AdminUserResponse(user));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            exception.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        System.out.println("ADMIN DASHBOARD CONTROLLER REACHED");
        return "Welcome, Admin! Access granted.";
    }
    
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        try {
            Role newRole = Role.valueOf(request.get("role"));
            user.setRole(newRole);
            userRepository.save(user);

            return ResponseEntity.ok(new AdminUserResponse(user));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid role"));
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        String currentUsername = SecurityContextHolder
                .getContext().getAuthentication().getName();

        if (user.getUsername().equals(currentUsername)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "You cannot delete your own account."));
        }

        if (user.getRole() == Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cannot delete the last Admin."));
        }

        userRepository.delete(user);

        return ResponseEntity.ok(
                Map.of("message", "User deleted successfully")
        );
    }
    
    @PutMapping("/tickets/{ticketId}/assign")
    public ResponseEntity<?> assignTicket(
            @PathVariable Long ticketId,
            @RequestBody AssignTicketRequest request) {
    	
    		System.out.println("ASSIGN TICKET CONTROLLER REACHED");

        if (request.getAgentId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Agent ID is required."));
        }

        try {
            Ticket ticket = ticketService.assignTicket(
                    ticketId, request.getAgentId());

            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", exception.getMessage()));
        }
    }
    
    @PutMapping("/users/{username}/reset-password")
    public ResponseEntity<?> resetUserPassword(
            @PathVariable String username,
            @RequestBody Map<String, String> request) {

        String newPassword = request.get("newPassword");

        try {
            User user = userService.resetUserPassword(
                    username, newPassword);

            return ResponseEntity.ok(
                    Map.of("message",
                            "Password reset successfully for " + user.getUsername()));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", exception.getMessage()));
        }
    }
}
