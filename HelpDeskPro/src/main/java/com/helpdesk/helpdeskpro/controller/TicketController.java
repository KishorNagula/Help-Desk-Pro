
package com.helpdesk.helpdeskpro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.helpdesk.helpdeskpro.entity.Ticket;
import com.helpdesk.helpdeskpro.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class TicketController {

	@Autowired
    TicketService ticketService;
	

	@PostMapping
	public Ticket createTicket(
	        @RequestBody Ticket ticket,
	        org.springframework.security.core.Authentication authentication) {

	    return ticketService.CreateTicket(ticket, authentication.getName());
	}

    @GetMapping
    public List<Ticket> getAllTickets(
            org.springframework.security.core.Authentication authentication) {

        return ticketService.getTicketsForUser(authentication.getName());
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

            ticketService.deleteTicket(id, authentication.getName());
    }
    

    @PutMapping("/{id}")
    public Ticket updateTicket(
            @PathVariable Long id,
            @RequestBody Ticket ticket,
            org.springframework.security.core.Authentication authentication) {

        return ticketService.updateTicket(
                id, ticket, authentication.getName());
    }

}
