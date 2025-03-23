# E-Commerce Application with React, Spring Boot, and Kafka

This project demonstrates a simple e-commerce application with event-driven architecture using:

- React frontend for browsing products and placing orders
- Spring Boot microservices with REST APIs
- Apache Kafka for event-driven communication
- Real-time order processing workflow

## Architecture Overview

![Architecture](docs/architecture.png)

The system consists of the following components:

1. **React Frontend:** A responsive web application for users to browse products, add to cart, and place orders.

2. **Order Service:** Handles order creation and management. When an order is placed, it publishes an order event to Kafka.

3. **Inventory Service:** Manages product inventory. It consumes order events to update product stock quantities.

4. **Payment Service:** Processes payments for orders. It consumes order events and handles payment processing.

5. **Notification Service:** Sends email notifications at different stages of the order process. It consumes events from other services.

6. **Apache Kafka:** Acts as the event bus for communication between microservices, promoting loose coupling.

## Project Structure

```
├── frontend/           # React frontend application
└── backend/            # Spring Boot backend services
    ├── common/         # Shared code, events, and models
    ├── order-service/  # Handles order creation and management
    ├── inventory-service/ # Manages product inventory
    ├── payment-service/   # Processes payments
    └── notification-service/ # Sends order confirmations
```

## Event Flow

The application implements the following event-driven workflow:

1. **Order Placement:**
   - User places an order through the frontend
   - Order Service creates the order and publishes an ORDER_CREATED event

2. **Parallel Processing:**
   - Inventory Service: Reserves products, updates stock
   - Payment Service: Processes payment
   - Notification Service: Sends order confirmation email

3. **Payment Status:**
   - Payment Service publishes either PAYMENT_COMPLETED or PAYMENT_FAILED event

4. **Order Completion:**
   - Order Service updates order status based on inventory and payment events
   - Notification Service sends appropriate emails based on status changes

## Technologies Used

- **Frontend:** React, Tailwind CSS, Axios
- **Backend:** Spring Boot, Spring Data JPA, Spring Kafka
- **Database:** PostgreSQL
- **Messaging:** Apache Kafka
- **Testing:** JUnit 5, Mockito, TestContainers

## Running the Application

### Prerequisites
- Node.js and npm
- Java 17+
- Maven
- Docker (for running Kafka and PostgreSQL)

### Start Infrastructure
```bash
# Start Kafka and PostgreSQL
docker-compose up -d
```

### Start Backend Services
```bash
# Build and start all services
cd backend
mvn clean install
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl inventory-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl notification-service
```

### Start Frontend
```bash
cd frontend
npm install
npm start
```

## API Documentation

Each microservice exposes REST APIs:

- **Order Service:** `http://localhost:8081/api/orders`
- **Inventory Service:** `http://localhost:8082/api/products`
- **Payment Service:** `http://localhost:8083/api/payments`

## Testing

Each service includes unit and integration tests:

```bash
cd backend
mvn test
```