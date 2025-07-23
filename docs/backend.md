# 🛠️ Backend Overview

This is the **Spring Boot** backend for the Product Management Dashboard. It follows the **MVC (Model-View-Controller)** architecture and exposes REST APIs for frontend consumption.

---

## ⚙️ Tech Stack

- ☕ Java 21  
- 🌱 Spring Boot 3.2.4  
- 🔧 Maven (build tool)

---

## 🚀 How to Run

Make sure `PostgreSQL` **is running** with the correct DB credentials before starting the backend.
- **Database name**: product_dashboard_db
- **Username**: postgres
- **Password**: 123456

```bash
cd backend/
mvn spring-boot:run
```

---

## 📁 Project Structure

| Layer          | Responsibility                            |
| -------------- | ----------------------------------------- |
| **Controller** | Handle HTTP requests (GET, POST, etc.)    |
| **Service**    | Business logic layer                      |
| **Model**      | Entity classes and DTOs representing data |
| **Repository** | Data access layer using Spring Data JPA   |

---
 
## 📡 API Endpoints

| Method | Endpoint        | Description                    | Status       |
| ------ | --------------- | ------------------------------ | ------------ |
| GET    | `/api/products` | Get list of all products       | ✅ Integrated |
| GET    | `/api/brands`   | Get list of all brands         | ✅ Integrated |
| GET    | `/`             | Welcome message (health check) | ✅ Worked    |

---

## 📦 Sample Responses

### 🔹 GET `/api/products`
Returns all products.
    
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

### 🔹 GET `/api/brands`
Returns all brands.
    
```bash
[
  {
    "id": 1,
    "name": "Dell",
    "country": "USA",
    "founded_year": 1984,
    "website": "https://www.dell.com",
    "description": "Dell is a global leader in personal computing and enterprise solutions, known for reliable laptops and desktops."
  },
  {
    "id": 2,
    "name": "Logitech",
    "country": "Switzerland",
    "founded_year": 1981,
    "website": "https://www.logitech.com",
    "description": "Logitech is renowned for its computer peripherals, including mice, keyboards, and webcams."
  }
]
```

### 🔹 GET `/`
Returns a simple welcome message to confirm server status.
    
```bash
Welcome to the Product Management Dashboard!
```

---

