package com.helpdesk.helpdeskpro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdeskpro.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket,Long>{
	
	List<Ticket> findByCreatedByUsername(String username);

    List<Ticket> findByAssignedToUsername(String username);

}
