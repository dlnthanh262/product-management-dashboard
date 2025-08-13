import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/productApi";
import * as jwtDecode from "jwt-decode";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const token = await login(username, password);
      const decoded = jwtDecode.jwtDecode(token);

      if (decoded.role !== "ROLE_ADMIN") {
        localStorage.removeItem("token");
        setError(
          "Permission denied. Please log in with an Admin account to access the Dashboard."
        );
        setLoading(false);
      } else {
        localStorage.setItem("token", token);
        setTimeout(() => {
          navigate("/dashboard");
        }, 500);
      }
    } catch (err) {
      setError("Invalid username or password");
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <form
        onSubmit={handleLogin}
        className="bg-white p-6 rounded shadow-md w-80"
        aria-label="Login form"
      >
        <h2 className="text-2xl font-bold mb-4">Product Dashboard</h2>

        <p className="text-xs text-gray-500 mb-1" aria-live="polite">
          Login account: <span className="font-mono">admin1</span>,{" "}
          <span className="font-mono">123456</span>
        </p>
        <p className="text-xs text-gray-500 mb-4" aria-live="polite">
          Test permission: <span className="font-mono">user1</span>,{" "}
          <span className="font-mono">123456</span>
        </p>

        {error && (
          <p
            className="text-red-500 text-sm mb-2"
            role="alert"
            aria-live="assertive"
          >
            {error}
          </p>
        )}

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => {
            setUsername(e.target.value);
            if (error) setError("");
          }}
          className="w-full p-2 mb-3 border rounded"
          required
          autoFocus
          aria-label="Username"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
            if (error) setError("");
          }}
          className="w-full p-2 mb-4 border rounded"
          required
          aria-label="Password"
        />

        <button
          type="submit"
          disabled={loading}
          className={`w-full p-2 rounded text-white ${
            loading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-blue-500 hover:bg-blue-600"
          }`}
        >
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
}
