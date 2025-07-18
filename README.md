# Product Management Dashboard

A full-stack web app for managing products, built with ReactJS (frontend) and Spring Boot (backend).

## Tech Stack

- Frontend: ReactJS, TailwindCSS, Axios, React Router
- Backend: Spring Boot, Spring Security, JPA
- Database: PostgreSQL
- Auth: JWT

## Features

- User login/register
- Product CRUD (create, read, update, delete)
- Dashboard with charts and stats
- Search, filter, pagination

## Features

#### User → ReactJS UI → Axios → Spring Boot REST API → PostgreSQL  
- Auth: JWT token in headers
- API: RESTful, secured with Spring Security
- DB: Product & User schema, pagination, query filters
- Charts: Product stats visualized via frontend

## Project Structure

<pre><code>project-root/ 
├── README.md 
├── backend/ 
├── docs/ 
└── frontend/ </code></pre>

## Getting Started

```bash
# Clone project
git clone https://github.com/dlnthanh262/product-management-dashboard.git
cd product-management-dashboard

# Setup frontend
cd frontend
npm install
npm run dev

# Setup backend
cd ../backend
./mvnw spring-boot:run
