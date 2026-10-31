
package com.helpdesk.helpdeskpro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.entity.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
    Optional<User> findByUsername(String username);

    long countByRole(Role role);
	
}
