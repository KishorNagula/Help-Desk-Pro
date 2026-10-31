
package com.helpdesk.helpdeskpro.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            
            System.out.println("JWT username: " + username);

            if (username != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                User user = userRepository.findByUsername(username)
                        .orElse(null);
                
                boolean tokenValid = user != null
                        && jwtService.isTokenValid(token, username);

                System.out.println("JWT user found: " + (user != null));
                System.out.println("JWT token valid: " + tokenValid);

                if (tokenValid) {

                    String roleName = "ROLE_" + user.getRole().name();
                    
                    System.out.println("Granted authority: " + roleName);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    null,
                                    List.of(new SimpleGrantedAuthority(roleName))
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (io.jsonwebtoken.JwtException
                | IllegalArgumentException exception) {

            SecurityContextHolder.clearContext();
            System.out.println("JWT error: " + exception.getClass().getSimpleName());
        }

        filterChain.doFilter(request, response);
        
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authentication: " +
                SecurityContextHolder.getContext().getAuthentication());
    }
}
