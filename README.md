# Fitness Microservices Application

A **full-stack Fitness Tracking application** built using **Spring Boot Microservices**, **React**, and modern cloud-native technologies. This project leverages multiple microservices to provide a scalable, secure, and AI-enhanced fitness platform.

---

## 🏗 Project Architecture

The project is structured using **microservices architecture**, each responsible for a specific functionality:

- **Activity Service**: Manages user fitness activities (workouts, running, cycling, etc.).
- **AI Service**: Provides AI-powered recommendations using **Groq AI** and integrates with activity data.
- **User Service**: Handles user management and validation with **Keycloak** authentication.
- **Gateway**: Acts as an API gateway with security filters and routing.
- **Config Server**: Centralized configuration for all microservices.
- **Eureka Server**: Service discovery for dynamic microservice communication.
- **Frontend**: Built with **React** and **Vite**, providing an interactive user interface.

---

## ⚡ Key Features

- **Microservices-based architecture** for scalability and maintainability.
- **AI Integration**:
  - Activity recommendations powered by **Groq AI**.
  - Automated insights and suggestions for user workouts.
- **Security & Authentication**:
  - **Keycloak** for OAuth2-based authentication and user management.
  - Role-based access control for **USER** and **ADMIN**.
- **Messaging & Event Handling**:
  - **RabbitMQ** for asynchronous messaging between services.
- **Reactive Programming**:
  - Uses **Spring WebFlux** and **WebClient** for non-blocking I/O and fast responses.
- **Configuration Management**:
  - **Spring Cloud Config Server** for centralized properties.
- **Service Discovery & Load Balancing**:
  - **Eureka Server** ensures services can find each other dynamically.
- **Frontend**:
  - Responsive **React** interface with authentication, activity tracking, and AI recommendations.

---

## 🛠 Technology Stack

| Layer               | Technology/Framework                  |
|--------------------|--------------------------------------|
| Backend            | Spring Boot, Spring WebFlux           |
| Messaging          | RabbitMQ                              |
| Security           | Keycloak, OAuth2                      |
| AI / Recommendation| Groq AI                               |
| Microservices      | Spring Cloud, Eureka                  |
| Config Management  | Spring Cloud Config Server            |
| Frontend           | React, Vite, Tailwind CSS             |
| Database           | MongoDB (for activity & AI data)     |
| Testing            | JUnit, Mockito                        |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- Node.js & npm
- Docker (for RabbitMQ or Keycloak if needed)



