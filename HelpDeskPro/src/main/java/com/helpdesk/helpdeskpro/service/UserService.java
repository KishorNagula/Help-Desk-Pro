
package com.helpdesk.helpdeskpro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.helpdesk.helpdeskpro.entity.Role;
import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.repository.UserRepository;

@Service
public class UserService {

	@Autowired
    UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;

    public User registerUser(
            String username,
            String email,
            String rawPassword) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // Hash the password before storing it.
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Public registration cannot create an admin.
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
    

    public User loginUser(String username, String rawPassword) {

    		User user = userRepository.findByUsername(username)
    				.orElseThrow(() ->
    				new IllegalArgumentException("Invalid username or password"));

    		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
    			throw new IllegalArgumentException("Invalid username or password");
    		}

    return user;
    }
	

	public User registerUserByAdmin(
        String username,
        String email,
        String rawPassword,
        Role role) {

    if (userRepository.existsByUsername(username)) {
        throw new IllegalArgumentException(
            "Username already exists");
    }

    if (userRepository.existsByEmail(email)) {
        throw new IllegalArgumentException(
            "Email already exists");
    }

    User user = new User();

    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(role);

    return userRepository.save(user);
	}

	public User resetUserPassword(String username, String newPassword) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new IllegalArgumentException(
	                    "User not found"));

	    if (newPassword == null || newPassword.isBlank()) {
	        throw new IllegalArgumentException(
	                "Password cannot be empty");
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));

	    return userRepository.save(user);
	}
   
}
