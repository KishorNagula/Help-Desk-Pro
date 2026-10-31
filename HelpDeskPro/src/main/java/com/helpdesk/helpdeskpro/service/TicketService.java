package com.helpdesk.helpdeskpro.service;

import java.util.List;
import com.helpdesk.helpdeskpro.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.helpdesk.helpdeskpro.entity.Ticket;
import com.helpdesk.helpdeskpro.repository.TicketRepository;
import com.helpdesk.helpdeskpro.entity.User;
import com.helpdesk.helpdeskpro.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;

@Service
public class TicketService {

	@Autowired
	TicketRepository trp;
	
	@Autowired
	UserRepository userRepository;

	
	public Ticket CreateTicket(Ticket ticket, String username) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "User not found"));

	    ticket.setCreatedBy(user);

	    // A newly created ticket is not assigned automatically.
	    ticket.setAssignedTo(null);

	    return trp.save(ticket);
	}
	
	public List<Ticket> GetAllTickets(){
		return trp.findAll();
	}
	
	public Ticket getTicketById(Long id) {
		return trp.getById(id);
	}
	
	public void deleteTicket(Long id, String username) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "User not found"));

	    Ticket ticket = trp.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "Ticket not found"));

	    if (user.getRole() == Role.USER) {
	        throw new AccessDeniedException(
	                "Users cannot delete tickets.");
	    }

	    if (user.getRole() == Role.SUPPORT_AGENT) {

	        if (ticket.getAssignedTo() == null ||
	                !ticket.getAssignedTo().getId().equals(user.getId())) {

	            throw new AccessDeniedException(
	                    "You can only delete tickets assigned to you.");
	        }
	    }

	    trp.delete(ticket);
	}
	
	public Ticket updateTicket(Long id, Ticket updatedTicket, String username) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "User not found"));

	    Ticket existingTicket = trp.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "Ticket not found"));

	    if (user.getRole() == Role.USER) {
	        throw new AccessDeniedException(
	                "Users cannot update tickets.");
	    }

	    if (user.getRole() == Role.SUPPORT_AGENT) {

	        if (existingTicket.getAssignedTo() == null ||
	                !existingTicket.getAssignedTo().getId().equals(user.getId())) {

	            throw new AccessDeniedException(
	                    "You can only update tickets assigned to you.");
	        }
	    }

	    existingTicket.setTitle(updatedTicket.getTitle());
	    existingTicket.setDescription(updatedTicket.getDescription());
	    existingTicket.setPriority(updatedTicket.getPriority());
	    existingTicket.setStatus(updatedTicket.getStatus());

	    return trp.save(existingTicket);
	}
	 
	 public List<Ticket> getTicketsForUser(String username) {

		    User user = userRepository.findByUsername(username)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.NOT_FOUND, "User not found"));

		    if (user.getRole() == Role.ADMIN) {
		        return trp.findAll();
		    }

		    if (user.getRole() == Role.SUPPORT_AGENT) {
		        return trp.findByAssignedToUsername(username);
		    }

		    return trp.findByCreatedByUsername(username);
		}
	 
	 
	 public Ticket assignTicket(Long ticketId, Long agentId) {

		    System.out.println("STEP 1: Finding ticket");

		    Ticket ticket = trp.findById(ticketId)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.NOT_FOUND, "Ticket not found"));

		    System.out.println("STEP 2: Ticket found");

		    User agent = userRepository.findById(agentId)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.NOT_FOUND, "Agent not found"));

		    System.out.println("STEP 3: Agent found: " + agent.getUsername());
		    System.out.println("STEP 4: Agent role: " + agent.getRole());

		    if (agent.getRole() != Role.SUPPORT_AGENT) {
		        throw new IllegalArgumentException(
		                "Selected user is not a Support Agent.");
		    }

		    System.out.println("STEP 5: Assigning agent");

		    ticket.setAssignedTo(agent);

		    Ticket savedTicket = trp.save(ticket);

		    System.out.println("STEP 6: Ticket saved successfully");

		    return savedTicket;
		}
}
