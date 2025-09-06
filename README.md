# Product Management Dashboard

A full-stack web app to manage products — built with **ReactJS** for the frontend and **Spring Boot** for the backend.

---

## 🌐 Tech Stack

- **Frontend**: ReactJS, TailwindCSS, Axios, React Router  
- **Backend**: Spring Boot, Spring Security, JPA  
- **Database**: PostgreSQL  
- **Authentication**: JWT 

---

## 🎯 Features

✅ User authentication (JWT)  
✅ Product CRUD (Create, Read, Update, Delete)  
✅ Interactive dashboard with charts and stats  
✅ Search, filter, and pagination support 

---

## 🧩 System Architecture

```mermaid
flowchart LR
  User[User]
  UI[ReactJS UI]
  Axios[Axios]
  API[Spring Boot REST API]
  DB[(PostgreSQL)]

  User --> UI
  UI --> Axios
  Axios --> API
  API --> DB
```

---

## 📁 Project Structure

<pre><code>product-management-dashboard/ 
├── README.md 
├── backend/ # Spring Boot API
├── frontend/ # React app 
└── docs/ # Detailed backend/frontend documentation</code></pre>

---

## ⚙️ Setup Instructions

### 🛠️ Prerequisites

Make sure the following tools are installed on your machine:

- **Java 21**
- **Maven**
- **Node.js 20**
- **PostgreSQL** (running locally or via Docker)

#### PostgreSQL default setup:
- **Database name**: `product_dashboard_db`
- **Username**: `postgres`
- **Password**: `123456`

### 🚧 Installation Steps

```bash
# Clone project
git clone https://github.com/dlnthanh262/product-management-dashboard.git
cd product-management-dashboard

# Setup frontend
cd frontend
npm install
npm run dev

# Setup backend
cd backend
mvn spring-boot:run
```

---

## 📌 Current Progress

| Feature               | Status         |
| --------------------- | -------------- |
| 📦 Product CRUD         | ✅ Done    |
| 🔐 Auth (JWT)       |  ✅ Done   |
| 📊 Dashboard & Charts |  ✅ Done   |
| ☁️ Deployment         | ✅ Done |

---

