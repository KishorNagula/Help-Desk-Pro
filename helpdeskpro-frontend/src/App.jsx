
import { useEffect, useState } from "react";
import "./App.css";
import Login from "./Login";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(Boolean(localStorage.getItem("token")));
  const [userRole, setUserRole] = useState(localStorage.getItem("role") || "");
  const [showUserForm, setShowUserForm] = useState(false);
  const [newUsername, setNewUsername] = useState("");
  const [newEmail, setNewEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [newRole, setNewRole] = useState("USER");
  const [userSuccess, setUserSuccess] = useState("");
  const [tickets, setTickets] = useState([]);
  const [users, setUsers] = useState([]);
  const [selectedAgents, setSelectedAgents] = useState({});
  const [assignmentMessage, setAssignmentMessage] = useState("");
  const [userError, setUserError] = useState("");
  const [searchText, setSearchText] = useState("");
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState("MEDIUM");
  const [status, setStatus] = useState("OPEN");
  const [editingId, setEditingId] = useState(null);


  function loadTickets() {
    setLoading(true);

    const token = localStorage.getItem("token");

    fetch("http://localhost:8080/api/tickets", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to load tickets");
        }

        return response.json();
      })
      .then((data) => {
        setTickets(data);
        setError("");
      })
      .catch(() => {
        setError("Unable to load tickets. Check the backend.");
      })
      .finally(() => {
        setLoading(false);
      });
  }



  useEffect(() => {
    if (isLoggedIn) {
      loadTickets();

      if (userRole === "ADMIN") {
        loadUsers();
      }
    }
  }, [isLoggedIn, userRole]);


  function createTicket(event) {
    event.preventDefault();

    const ticketData = {
      title,
      description,
      priority,
      status
    };

    const isEditing = editingId !== null;

    const url = isEditing
      ? `http://localhost:8080/api/tickets/${editingId}`
      : "http://localhost:8080/api/tickets";

    fetch(url, {
      method: isEditing ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify(ticketData)
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to save ticket");
        }

        return response.json();
      })
      .then(() => {
        setTitle("");
        setDescription("");
        setPriority("MEDIUM");
        setStatus("OPEN");
        setEditingId(null);
        setShowForm(false);
        loadTickets();
      })
      .catch(() => {
        setError("Unable to save ticket. Please try again.");
      });
  }


  const openCount = tickets.filter(
    (ticket) => ticket.status === "OPEN"
  ).length;

  const inProgressCount = tickets.filter(
    (ticket) => ticket.status === "IN_PROGRESS"
  ).length;

  const resolvedCount = tickets.filter(
    (ticket) => ticket.status === "RESOLVED"
  ).length;

  const filteredTickets = tickets.filter((ticket) => {
    const matchesSearch = ticket.title
      .toLowerCase()
      .includes(searchText.toLowerCase());

    const matchesStatus =
      statusFilter === "ALL" || ticket.status === statusFilter;

    return matchesSearch && matchesStatus;
  });


  function editTicket(ticket) {
    setEditingId(ticket.id);
    setTitle(ticket.title);
    setDescription(ticket.description);
    setPriority(ticket.priority);
    setStatus(ticket.status);
    setShowForm(true);
  }

  function deleteTicket(id) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this ticket?"
    );

    if (!confirmed) {
      return;
    }

    fetch(`http://localhost:8080/api/tickets/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to delete ticket");
        }

        loadTickets();
      })
      .catch(() => {
        setError("Unable to delete ticket. Please try again.");
      });
  }

  function openCreateForm() {
    setEditingId(null);
    setTitle("");
    setDescription("");
    setPriority("MEDIUM");
    setStatus("OPEN");
    setShowForm(true);
  }

  if (!isLoggedIn) {
    return (
      <Login
        onLoginSuccess={(data) => {
          setUserRole(data.role);
          setIsLoggedIn(true);
        }}
      />
    );
  }


  function loadUsers() {
    const token = localStorage.getItem("token");

    setUserError("");

    fetch("http://localhost:8080/api/admin/users", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error("Unable to load users. Please log in again if needed.");
        }

        return response.json();
      })
      .then((data) => {
        setUsers(data);
      })
      .catch((error) => {
        setUserError(error.message);
      });
  }

  function assignTicket(ticketId) {
    const agentId = selectedAgents[ticketId];

    if (!agentId) {
      setAssignmentMessage("Please select a Support Agent.");
      return;
    }

    fetch(`http://localhost:8080/api/admin/tickets/${ticketId}/assign`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify({
        agentId: Number(agentId),
      }),
    })
      .then(async (response) => {
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Unable to assign ticket.");
        }

        return data;
      })
      .then(() => {
        setAssignmentMessage(`Ticket ${ticketId} assigned successfully!`);
        loadTickets();
      })
      .catch((error) => {
        setAssignmentMessage(error.message);
      });
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");

    setIsLoggedIn(false);
    setUserRole("");
    setTickets([]);
    setUsers([]);
    setAssignmentMessage("");
  }

  function updateUserRole(userId, newRole) {
    const token = localStorage.getItem("token");

    fetch(`http://localhost:8080/api/admin/users/${userId}/role`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        role: newRole,
      }),
    })
      .then(async (response) => {
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Unable to update role.");
        }

        return data;
      })
      .then(() => {
        setUserSuccess("User role updated successfully!");
        loadUsers();
      })
      .catch((error) => {
        setUserError(error.message);
      });
  }

  function deleteUser(userId) {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this user?"
    );

    if (!confirmDelete) {
      return;
    }

    const token = localStorage.getItem("token");

    fetch(`http://localhost:8080/api/admin/users/${userId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (response) => {
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Unable to delete user.");
        }

        return data;
      })
      .then((data) => {
        setUserSuccess(data.message);
        loadUsers();
      })
      .catch((error) => {
        setUserError(error.message);
      });
  }

  function createUser(event) {
    event.preventDefault();

    setUserError("");
    setUserSuccess("");

    const token = localStorage.getItem("token");

    fetch("http://localhost:8080/api/admin/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        username: newUsername,
        email: newEmail,
        password: newPassword,
        role: newRole,
      }),
    })
      .then(async (response) => {
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Unable to create user.");
        }

        return data;
      })
      .then((data) => {
        setUserSuccess(
          `User ${data.username} created successfully!`
        );

        setNewUsername("");
        setNewEmail("");
        setNewPassword("");
        setNewRole("USER");

        setShowUserForm(false);

        loadUsers();
      })
      .catch((error) => {
        setUserError(error.message);
      });
  }



  return (
    <div className="app">
      <aside className="sidebar">
        <h2 className="logo">HelpDesk Pro</h2>
        <p className="menu-title">WORKSPACE</p>
        <div className="menu-item active">Dashboard</div>
        <div className="menu-item">All Tickets</div>
        <div className="menu-item">My Tickets</div>
        <div className="menu-item">Settings</div>
        <div className="sidebar-footer">IT Support System</div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <h1>Dashboard</h1>
            <p>Welcome to your IT support workspace.</p>
            <p>Role: {userRole}</p>
          </div>

          <div className="topbar-actions">
            <button
              className="primary-button"
              onClick={openCreateForm}
            >
              + Create Ticket
            </button>

            <button
              className="logout-button"
              onClick={logout}
            >
              Logout
            </button>
          </div>
        </header>

        {showForm && (
          <section className="ticket-section">
            <h2>{editingId !== null ? "Update Ticket" : "Create a New Ticket"}</h2>

            <form onSubmit={createTicket}>
              <label>Title</label>
              <input
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                required
              />

              <label>Description</label>
              <textarea
                value={description}
                onChange={(event) =>
                  setDescription(event.target.value)
                }
                required
              />

              <label>Priority</label>
              <select
                value={priority}
                onChange={(event) => setPriority(event.target.value)}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>

              <label>Status</label>
              <select
                value={status}
                onChange={(event) => setStatus(event.target.value)}
              >
                <option value="OPEN">Open</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="RESOLVED">Resolved</option>
              </select>

              <button className="primary-button" type="submit">
                {editingId !== null ? "Update Ticket" : "Save Ticket"}
              </button>

              <button
                type="button"
                onClick={() => {
                  setShowForm(false);
                  setEditingId(null);
                  setTitle("");
                  setDescription("");
                  setPriority("MEDIUM");
                  setStatus("OPEN");
                }}
              >
                Cancel
              </button>
            </form>
          </section>
        )}

        <section className="stats">
          <div className="stat-card">
            <p>Total Tickets</p>
            <h2>{tickets.length}</h2>
          </div>
          <div className="stat-card">
            <p>Open Tickets</p>
            <h2>{openCount}</h2>
          </div>
          <div className="stat-card">
            <p>In Progress</p>
            <h2>{inProgressCount}</h2>
          </div>
          <div className="stat-card">
            <p>Resolved</p>
            <h2>{resolvedCount}</h2>
          </div>
        </section>

        <section className="ticket-section">
          <h2>Recent Tickets</h2>
          {userRole === "ADMIN" && assignmentMessage && (
            <p>{assignmentMessage}</p>
          )}
          <p>Track and manage IT support requests.</p>

          <div className="ticket-filters">
            <input
              type="text"
              placeholder="Search tickets by title..."
              value={searchText}
              onChange={(event) => setSearchText(event.target.value)}
            />

            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
            >
              <option value="ALL">All Statuses</option>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
            </select>
          </div>

          {error && <p className="error-message">{error}</p>}

          {loading ? (
            <p className="message">Loading tickets...</p>
          ) : !error && filteredTickets.length === 0 ? (
            <div className="empty-state">
              <h3>No tickets found</h3>
              <p>
                {searchText || statusFilter !== "ALL"
                  ? "Try changing your search or status filter."
                  : "Create your first ticket to get started."}
              </p>
            </div>
          ) : (
            filteredTickets.map((ticket) => (
              // Keep your existing ticket card JSX here.
              <div className="ticket-details" key={ticket.id}>
                <h3>{ticket.title}</h3>

                <p>{ticket.description}</p>

                <div className="ticket-badges">
                  <span
                    className={`badge priority-${ticket.priority.toLowerCase()}`}
                  >
                    {ticket.priority} Priority
                  </span>

                  <span
                    className={`badge status-${ticket.status.toLowerCase().replace("_", "-")}`}
                  >
                    {ticket.status.replace("_", " ")}
                  </span>
                </div>

                <p>Ticket ID: {ticket.id}</p>
                <p>
                  Created By: {ticket.createdBy
                    ? ticket.createdBy.username
                    : "Unknown"}
                </p>
                <p>
                  Assigned To: {ticket.assignedTo
                    ? ticket.assignedTo.username
                    : "Not Assigned"}
                </p>

                {(userRole === "ADMIN" || userRole === "SUPPORT_AGENT") && (
                  <div className="ticket-actions">
                    <button
                      className="primary-button"
                      onClick={() => editTicket(ticket)}
                    >
                      Update
                    </button>

                    <button onClick={() => deleteTicket(ticket.id)}>
                      Delete
                    </button>
                  </div>
                )}

                {userRole === "ADMIN" && (
                  <div className="ticket-assignment">
                    <label>Assign to Support Agent</label>

                    <select
                      value={selectedAgents[ticket.id] || ticket.assignedTo?.id || ""}
                      onChange={(event) =>
                        setSelectedAgents({
                          ...selectedAgents,
                          [ticket.id]: event.target.value,
                        })
                      }
                    >
                      <option value="">Select Support Agent</option>

                      {users
                        .filter((user) => user.role === "SUPPORT_AGENT")
                        .map((agent) => (
                          <option key={agent.id} value={agent.id}>
                            {agent.username}
                          </option>
                        ))}
                    </select>

                    <button onClick={() => assignTicket(ticket.id)}>
                      Assign
                    </button>
                  </div>
                )}
              </div>
            ))
          )}

        </section>

        {userRole === "ADMIN" && (
          <section className="ticket-section">
            <h2>User Management</h2>
            <p>View all registered HelpDesk Pro users.</p>

            <button
              className="primary-button"
              onClick={() => {
                setShowUserForm(!showUserForm);
                setUserError("");
                setUserSuccess("");
              }}
            >
              {showUserForm ? "Cancel" : "+ Add User"}
            </button>

            {userSuccess && (
              <p className="message">{userSuccess}</p>
            )}

            {showUserForm && (
              <form onSubmit={createUser}>
                <label>Username</label>
                <input
                  type="text"
                  value={newUsername}
                  onChange={(event) =>
                    setNewUsername(event.target.value)
                  }
                  required
                />

                <label>Email</label>
                <input
                  type="email"
                  value={newEmail}
                  onChange={(event) =>
                    setNewEmail(event.target.value)
                  }
                  required
                />

                <label>Password</label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(event) =>
                    setNewPassword(event.target.value)
                  }
                  required
                  minLength={8}
                />

                <label>Role</label>
                <select
                  value={newRole}
                  onChange={(event) =>
                    setNewRole(event.target.value)
                  }
                >
                  <option value="USER">USER</option>
                  <option value="SUPPORT_AGENT">
                    SUPPORT_AGENT
                  </option>
                  <option value="ADMIN">ADMIN</option>
                </select>

                <button
                  className="primary-button"
                  type="submit"
                >
                  Create User
                </button>
              </form>
            )}


            <button
              className="primary-button"
              onClick={loadUsers}
            >
              Load Users
            </button>

            {userError && (
              <p className="error-message">{userError}</p>
            )}

            {users.length > 0 ? (
              <div className="users-list">
                {users.map((user) => (
                  <div className="ticket-details" key={user.id}>
                    <h3>{user.username}</h3>
                    <p>Email: {user.email}</p>
                    <p>Role: {user.role}</p>
                    <button
                      className="delete-button"
                      onClick={() => deleteUser(user.id)}
                    >
                      Delete User
                    </button>
                    <select
                      value={user.role}
                      onChange={(event) => updateUserRole(user.id, event.target.value)}
                    >
                      <option value="USER">USER</option>
                      <option value="SUPPORT_AGENT">SUPPORT_AGENT</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </div>
                ))}
              </div>
            ) : (
              !userError && (
                <p className="message">
                  Click "Load Users" to view registered users.
                </p>
              )
            )}
          </section>
        )}

      </main>
    </div>
  );
}

export default App;
