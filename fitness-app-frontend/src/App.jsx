import React from "react";

function App() {
  console.log('🔄 App component rendered');

  const handleTestClick = (e) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('🧪 Test button clicked');
    alert('TEST WORKS!');
  };

  const handleKeycloakLogin = (e) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('🚀 Keycloak login clicked');
    
    setTimeout(() => {
      window.location.href = 'http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth?client_id=oauth2-pkce-client&redirect_uri=http://localhost:5173&response_type=code&scope=openid%20profile%20email%20offline_access';
    }, 100);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Welcome to Fitness App</h1>
      
      {/* Test with event prevention */}
      <button 
        type="button"
        style={{
          backgroundColor: '#ff0000',
          color: 'white',
          padding: '10px 20px',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '16px',
          marginBottom: '10px'
        }}
        onMouseDown={handleTestClick}
      >
        TEST BUTTON (Click me first)
      </button>
      
      <br />
      
      {/* Keycloak login with event prevention */}
      <button 
        type="button"
        style={{
          backgroundColor: '#28a745',
          color: 'white',
          padding: '10px 20px',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '16px'
        }}
        onMouseDown={handleKeycloakLogin}
      >
        LOGIN TO KEYCLOAK
      </button>

      <br /><br />

      {/* Alternative: Direct link */}
      <a 
        href="http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth?client_id=oauth2-pkce-client&redirect_uri=http://localhost:5173&response_type=code&scope=openid%20profile%20email%20offline_access"
        style={{
          display: 'inline-block',
          backgroundColor: '#007bff',
          color: 'white',
          padding: '10px 20px',
          textDecoration: 'none',
          borderRadius: '4px',
          fontSize: '16px'
        }}
      >
        LOGIN WITH LINK (Alternative)
      </a>
    </div>
  );
}

export default App;