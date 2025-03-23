# ECommerce Application with React, Spring Boot, and Kafka

This is an e-commerce application built with a microservices architecture using React for the frontend, Spring Boot for the backend, and Apache Kafka for event-driven communication between services.

## Features

- Microservices architecture with service isolation
- Event-driven communication using Apache Kafka
- React frontend for product listings, shopping cart, and checkout
- RESTful APIs for product, order, and payment management
- Swagger/OpenAPI documentation for all APIs
- Containerization using Docker
- Database persistence with PostgreSQL

## Architecture

The application consists of the following components:

- **Frontend**: React application
- **Backend Services**:
  - **Inventory Service**: Manages products and stock levels
  - **Order Service**: Handles order creation and processing
  - **Payment Service**: Processes payments
  - **Notification Service**: Sends notifications (e.g., order confirmations)
- **Kafka**: For asynchronous communication between services
- **PostgreSQL**: For data persistence

## Setup and Running

### Prerequisites

- Java 17+
- Maven
- Docker and Docker Compose
- Node.js and npm

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ArvindAkula/ECommerce-react-spring-kafka.git
   cd ECommerce-react-spring-kafka
   ```

2. **Start the infrastructure (Kafka, PostgreSQL) using Docker Compose**:
   ```bash
   docker-compose up -d
   ```

3. **Build and run the backend services**:
   ```bash
   cd backend
   mvn clean install
   
   # Start each service in a separate terminal
   java -jar inventory-service/target/inventory-service-1.0-SNAPSHOT.jar
   java -jar order-service/target/order-service-1.0-SNAPSHOT.jar
   java -jar payment-service/target/payment-service-1.0-SNAPSHOT.jar
   java -jar notification-service/target/notification-service-1.0-SNAPSHOT.jar
   ```

4. **Run the frontend**:
   ```bash
   cd frontend
   npm install
   npm start
   ```

5. **Access the application**:
   - Frontend: http://localhost:3000
   - Swagger UI (API Documentation): 
     - Inventory Service: http://localhost:8082/swagger-ui.html
     - Order Service: http://localhost:8081/swagger-ui.html
     - Payment Service: http://localhost:8083/swagger-ui.html
   - Kafka UI: http://localhost:8080
   - PgAdmin: http://localhost:5050 (login with admin@example.com / admin)

## API Documentation

Each service provides its own Swagger/OpenAPI documentation:

- Inventory Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8081/swagger-ui.html
- Payment Service: http://localhost:8083/swagger-ui.html

## Data Initialization

The application includes data initializers that automatically populate each service's database with sample data when the application starts. This includes:

- Sample products in the inventory service
- Sample orders in the order service
- Sample payments in the payment service

## Error Handling

Global exception handling has been implemented to provide consistent error responses across all services:

- Validation errors return field-specific error messages
- Not found errors return appropriate 404 responses
- Unexpected errors are logged and return a user-friendly message

## CORS Configuration

CORS is configured to allow the frontend to communicate with the backend services. The default configuration allows requests from `http://localhost:3000`.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the [MIT License](LICENSE).
