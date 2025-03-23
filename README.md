# E-Commerce Application with React, Spring Boot, and Kafka

This project demonstrates a simple e-commerce application with:

- React frontend for browsing products and placing orders
- Spring Boot backend with REST APIs
- Apache Kafka for event-driven architecture
- Real-time order processing workflow

## Features

- Product catalog browsing
- Shopping cart management
- Order placement and processing
- Real-time inventory updates
- Payment processing
- Order notifications
- Analytics and reporting

## Project Structure

```
├── frontend/           # React frontend application
└── backend/            # Spring Boot backend services
    ├── order-service/  # Handles order creation and management
    ├── inventory-service/ # Manages product inventory
    ├── payment-service/   # Processes payments
    └── notification-service/ # Sends order confirmations
```

## Getting Started

### Prerequisites

- Node.js and npm
- Java 17+
- Maven
- Docker (for running Kafka)

### Running Kafka

```bash
# Start Kafka using Docker Compose
docker-compose up -d
```

### Running the Backend

```bash
# Navigate to backend directory
cd backend

# Build all services
mvn clean install

# Run each service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl inventory-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl notification-service
```

### Running the Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

## Architecture

This application uses an event-driven architecture with Apache Kafka:

1. Customer places an order through the frontend
2. Order Service receives the order and publishes an event to Kafka
3. Multiple services consume the order event:
   - Inventory Service updates product stock levels
   - Payment Service processes the payment
   - Notification Service sends order confirmation
   - Analytics Service updates dashboards and reports

## Testing

Unit and integration tests are available for all services:

```bash
# Run tests
mvn test
```