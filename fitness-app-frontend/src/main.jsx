import React from 'react'
import ReactDOM from 'react-dom/client'
import { Provider } from 'react-redux'
import { BrowserRouter as Router } from 'react-router-dom'
import { store } from './store/store'
import { AuthProvider } from "./context/AuthContext"
import { authConfig } from './authConfig' // Missing import added
import App from './App'

// As of React 18
const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
  <Provider store={store}>
    <Router>
      <AuthProvider authConfig={authConfig} loadingComponent={<div>Loading...</div>}>
        <App />
      </AuthProvider>
    </Router>
  </Provider>
)
