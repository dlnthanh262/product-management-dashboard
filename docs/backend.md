# Backend Overview

This Spring Boot backend follows a basic MVC (Model-View-Controller) structure to organize REST APIs.

Tech Stack: 
- Java 21
- Spring Boot 3.2.4  
- Maven (build tool)

## How to Run

```bash
cd backend/
mvn spring-boot:run
```

## Project Structure & Layers

 - Controller: Handle HTTP requests (like GET, POST).
 - Model: Define the structure of data objects (DTOs or entities).
 - Service: Business logic layer.
 
## API Endpoints

### 1. GET /api/products
 - Description: Returns a list of all products.
 - Current Status: Mock data only (DB integration coming next).
 - Sample Response:
    
```bash
[
  {
    "id": 1,
    "name": "Laptop",
    "quantity": 20,
    "price": 1200000
  },
  {
    "id": 2,
    "name": "Mouse",
    "quantity": 10,
    "price": 10000
  },
]
```

### 2. GET /
 - Description: Returns a simple welcome message.
 - Purpose: Sanity check to ensure the server is running.

