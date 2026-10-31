# HelpDesk Pro – IT Help Desk & Ticket Management System

HelpDesk Pro is a full-stack IT Help Desk and Ticket Management System built to simplify IT support operations. It enables users to raise support tickets, support agents to manage assigned tickets, and administrators to manage users, tickets, and assignments.

## 🚀 Features

### Authentication & Authorization
- User registration and login.
- JWT-based authentication.
- Role-Based Access Control (RBAC).
- Three user roles: ADMIN, SUPPORT_AGENT, and USER.

### Ticket Management
- Create, view, update, and delete support tickets.
- Search tickets and filter by status.
- Track ticket priority and status.
- View ticket creator and assigned support agent.

### Admin Management
- View and manage users.
- Create new users.
- Update user roles.
- Reset user passwords.
- Assign tickets to support agents.
- Manage all support tickets.

### Role-Based Permissions

| Role | Permissions |
|---|---|
| ADMIN | Manage users, view all tickets, assign tickets |
| SUPPORT_AGENT | View assigned tickets and update ticket progress |
| USER | Create and view their own tickets |

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React.js, JavaScript, HTML, CSS |
| Backend | Java, Spring Boot |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Build Tools | Maven, npm |
| Development

## 📸 Application Screenshots

### Login Page
![Login Page](screenshots/login.png)

### User Dashboard
![User Dashboard](screenshots/user-dashboard.png)

### Admin Dashboard
![Admin Dashboard](screenshots/admin-dashboard.png)

### Support Agent Dashboard
![Support Agent Dashboard](screenshots/support-agent-dashboard.png)
