import React, { createContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export const AuthContext = createContext();

export const AuthProvider = ({ children, authConfig }) => {
  const [token, setToken] = useState(localStorage.getItem("token") || null);
  const [tokenData, setTokenData] = useState(
    JSON.parse(localStorage.getItem("user")) || null
  );
  const [error, setError] = useState(null); // ADD THIS LINE
  const navigate = useNavigate();

  // Login → redirect to Keycloak
  const logIn = () => {
    console.log('🚀 Login button clicked'); // ADD THIS LINE
    const loginUrl = `${authConfig.authorizationEndpoint}?client_id=${
      authConfig.clientId
    }&redirect_uri=${encodeURIComponent(
      authConfig.redirectUri
    )}&response_type=code&scope=${encodeURIComponent(authConfig.scope)}`;
    
    console.log('🔗 Login URL:', loginUrl); // ADD THIS LINE
    setError(null); // Clear any previous errors
    window.location.href = loginUrl;
  };

  // Logout → clear everything
  const logOut = () => {
    setToken(null);
    setTokenData(null);
    setError(null); // ADD THIS LINE
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("userId");
    navigate("/"); // back to home
  };

  // Decode JWT helper
  const decodeJwt = (token) => {
    try {
      return JSON.parse(atob(token.split(".")[1]));
    } catch (e) {
      return null;
    }
  };

  // Handle redirect back from Keycloak with ?code=...
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");

    if (code && !token) {
      // Exchange code for token
      fetch(authConfig.tokenEndpoint, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
          grant_type: "authorization_code",
          code,
          redirect_uri: authConfig.redirectUri,
          client_id: authConfig.clientId,
          // ⚠️ If your client requires PKCE or client_secret, add here
        }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.access_token) {
            // Save token
            localStorage.setItem("token", data.access_token);
            setToken(data.access_token);

            // Decode user info
            const payload = decodeJwt(data.access_token);
            localStorage.setItem("user", JSON.stringify(payload));
            setTokenData(payload);

            // Clean URL and redirect to dashboard
            window.history.replaceState({}, document.title, "/dashboard");
          } else {
            console.error("Token exchange failed:", data);
            setError(`Login failed: ${data.error_description || data.error || 'Unknown error'}`);
          }
        })
        .catch((err) => {
          console.error("Token exchange failed:", err);
          setError(`Login failed: ${err.message}`);
        });
    }
  }, [token, authConfig]);

  return (
    <AuthContext.Provider
      value={{
        token,
        tokenData,
        logIn,
        logOut,
        error,        // ADD THIS LINE
        setError,     // ADD THIS LINE
        isAuthenticated: !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};