package com.helpdesk.helpdeskpro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.helpdesk.helpdeskpro.entity.User;

@Entity
public class Ticket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String description;
	private String priority;
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "created_by_id")
	private User createdBy;

	@ManyToOne
	@JoinColumn(name = "assigned_to_id")
	private User assignedTo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public User getCreatedBy() {
	    return createdBy;
	}

	public void setCreatedBy(User createdBy) {
	    this.createdBy = createdBy;
	}

	public User getAssignedTo() {
	    return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
	    this.assignedTo = assignedTo;
	}
	
}
