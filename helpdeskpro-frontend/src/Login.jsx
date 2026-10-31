/* eslint-disable react/prop-types */

import { useState } from "react";

function Login({ onLoginSuccess }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    function handleLogin(event) {
        event.preventDefault();
        setError("");
        setLoading(true);

        fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        })
            .then(async (response) => {
                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || "Login failed. Check your credentials.");
                }

                if (!data.token) {
                    throw new Error("Login succeeded, but no token was returned.");
                }

                localStorage.setItem("token", data.token);
                localStorage.setItem("role", data.role);
                console.log("Login role:", data.role);
                onLoginSuccess(data);
            })
            .catch((err) => {
                setError(err.message || "Unable to connect to the backend.");
            })
            .finally(() => {
                setLoading(false);
            });
    }

    return (
        <div style={{ maxWidth: "400px", margin: "80px auto", padding: "24px" }}>
            <h2>HelpDesk Pro Login</h2>

            <form onSubmit={handleLogin}>
                <div style={{ marginBottom: "16px" }}>
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        required
                        style={{ display: "block", width: "100%", padding: "10px", boxSizing: "border-box" }}
                    />
                </div>

                <div style={{ marginBottom: "16px" }}>
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        required
                        style={{ display: "block", width: "100%", padding: "10px", boxSizing: "border-box" }}
                    />
                </div>

                {error && (
                    <p style={{ color: "red" }}>{error}</p>
                )}

                <button type="submit" disabled={loading}>
                    {loading ? "Logging in..." : "Login"}
                </button>
            </form>
        </div>
    );
}

export default Login;
