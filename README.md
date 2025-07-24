# 💳 SBR E-Banking System (Full Stack)

This is a **full-stack digital banking application** built with **Spring Boot (Java)** and **Angular**. It simulates a real-world online banking system with features like customer and account management, secure authentication, financial operations, user access control, and insightful dashboards.

> 🧠 **For educational purposes only. Please do not clone or reuse without permission.**  
> 🔐 **Live demo** available at: [saadbr.me/Digita-lBanking](https://saadbr.me/Digital-Banking)  
> 👤 Test Credentials: `admin / admin123@`

---

## 🌿 Branching Strategy (Important)

This repository contains multiple branches depending on the tech stack:

| Branch Name         | Description                                       |
|---------------------|---------------------------------------------------|
| `main`              | Full Stack (Spring Boot + Angular) with **MySQL** |
| `backend-mongoDB`   | Backend only with **MongoDB**                     |
| `frontend-mongoDB`  | Angular frontend adapted to MongoDB APIs          |
| `backend`           | Legacy backend version (MySQL)                    |
| `frontend`          | Legacy frontend version                           |

> 🧭 Use the appropriate branch depending on your stack (SQL vs NoSQL).

---

## 🚀 Features

### 🔐 Authentication & Authorization
- Login with JWT-based authentication
- Role-based access control (`USER`, `ADMIN`)
- Protected routes via Angular Guards
- Admin-only sections like logs and user management

### 👥 User & Customer Management
- Admin can manage internal users (create/reset passwords)
- Admin can enable/disable user accounts
- View all customers
- Add, update, delete customers
- Search customers by keyword

### 🧾 Account & Operations
- View account details (Current / Saving)
- Credit / Debit operations
- Transfer funds between accounts
- View operation logs per account
- Store operator identity for all transactions

### 📈 Dashboard
- Charts powered by Appex
- Pie chart for account types (Current vs Saving)
- Bar chart of operations by type (Debit, Credit, Transfer)
- Bar chart showing most active customers
- Summary stat cards: customer count, account count, total balance, operation count

### 📜 Action Logs
- Admin-only log section
- View backend logs for auditing and debugging

---

## 🧱 Tech Stack

| Layer    | Technology                    |
| -------- | ----------------------------- |
| Frontend | Angular 20                    |
| Backend  | Spring Boot, Spring Security  |
| Auth     | JWT,Route Guards,Interceptors |
| Charts   | Chart.js (via ng2-charts)     |
| Database | MongoDB Atlas / MySQL         |
| Docs     | Swagger UI                    |
| Build    | Maven                         |
| Language | Java 17+, TypeScript          |

---

## 📦 Project Structure

### Backend: `ebanking-backend/`

```
├── config/             # Spring Security + JWT
├── controllers/        # REST APIs (auth, customer, account, logs)
├── services/           # Business logic
├── entities/           # MongoDB models
├── dtos/               # Request/Response DTOs
├── repositories/       # Spring Data MongoDB
├── mappers/            # DTO ↔ Entity mapping
├── logs/               # Log fetching endpoints
└── resources/          # app config
```

### Frontend: `ebanking-frontend/`

```
├── app/
│   ├── services/           # Auth, Customer, Account, Log services
│   ├── guards/             # AuthGuard for routes
│   ├── interceptors/       # JWT token injection
│   ├── login/              # Login form
│   ├── dashboard/          # Dashboard with charts
│   ├── customers/          # Customer CRUD
│   ├── accounts/           # Account operations
│   ├── admin/              # Logs + User management
│   └── layout/             # Navbar + sidebar templates
```

---

## 📬 Key Backend Endpoints

| Method   | Endpoint                      | Description                |
|----------|-------------------------------|----------------------------|
| `POST`   | `/auth/login`                 | User login (JWT)           |
| `GET`    | `/customers`                  | List all customers         |
| `POST`   | `/customers`                  | Add customer               |
| `PUT`    | `/customers/{id}`             | Update customer            |
| `DELETE` | `/customers/{id}`             | Delete customer            |
| `POST`   | `/accounts/credit`            | Credit an account          |
| `POST`   | `/accounts/debit`             | Debit an account           |
| `POST`   | `/accounts/transfer`          | Transfer between accounts  |
| `GET`    | `/accounts/{id}`              | Account details            |
| `GET`    | `/logs`                       | View system logs           |
| `POST`   | `/users/reset-password`       | Admin resets user password |
| `POST`   | `/users/change-password`      | User changes own password  |

> ⚠️ All protected with JWT + Role-based scopes.

---

## ▶️ Run Backend

```bash
cd ebanking-backend
./mvnw spring-boot:run
```

Visit Swagger UI at:  
🔗 [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

---

## ▶️ Run Frontend

```bash
cd ebanking-frontend
npm install
ng serve
```

Frontend runs at:  
🔗 [http://localhost:4200](http://localhost:4200)

---

## 🎨 UI Preview

- Fully responsive layout with Bootstrap 5
- Charts are responsive and animated
- Sidebar with dynamic links based on user role
- SBR Bank logo branding throughout the UI

---

## 📄 License

This project is licensed under the **MIT License**.

You are free to fork and modify this codebase for **educational** or **personal learning** purposes only. Please give credit to the author if reused or published elsewhere.

---

## 👤 Author

**Saad El Mabrouk**  
[saadbr.me](https://saadbr.me)