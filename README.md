# 💳 E-Banking Backend API

This is the backend implementation of a **Digital Banking System** built using **Spring Boot**, following clean architecture principles, DTO usage, layered services, and RESTful API conventions.

> 📌 The project is still in progress — frontend (Angular) will be added later.

---

## 🚀 Features

- Customer & Bank Account Management
- Current & Saving Account Types (via Inheritance)
- Account Operations:
  - ✅ Credit
  - ✅ Debit
  - 🔁 Transfer between accounts
- Transaction History with Pagination
- RESTful API with Swagger UI
- DTO abstraction layer for clean API interaction
- Exception handling for common banking errors

---

## 🧱 Tech Stack

| Layer           | Tech                             |
|----------------|----------------------------------|
| Framework      | Spring Boot                      |
| ORM            | Spring Data JPA, Hibernate       |
| Database       | H2 (in-memory) or MySQL/PostgreSQL |
| API Docs       | Swagger (Springdoc OpenAPI)      |
| Logging        | Lombok + SLF4J                   |
| Build Tool     | Maven                            |
| Java Version   | Java 17+                         |

---

## 🗂️ Project Structure

```
ebanking-backend/
│
├── entities/               # JPA Entities
├── dtos/                   # Data Transfer Objects
├── services/               # Business Logic (Interfaces + Implementations)
├── repositories/           # Spring Data JPA Interfaces
├── web/                    # REST Controllers
├── enums/                  # Enum Types (AccountStatus, OperationType)
├── mappers/                # Entity-DTO Converters (MapStruct or manual)
└── exceptions/             # Custom exceptions
```

---

## 📬 Sample Endpoints

| Method | Endpoint                          | Description                       |
|--------|-----------------------------------|-----------------------------------|
| `GET`  | `/accounts/{id}`                  | Get account details               |
| `POST` | `/accounts/debit`                 | Debit from account                |
| `POST` | `/accounts/credit`                | Credit to account                 |
| `POST` | `/accounts/transfer`              | Transfer between two accounts     |
| `GET`  | `/accounts/{id}/operations`       | Get full transaction history      |
| `GET`  | `/accounts/{id}/pageOperations`   | Get paginated transaction history |

Use [Swagger UI](http://localhost:8080/swagger-ui.html) to explore and test endpoints.

---

## ▶️ Run Locally

1. **Clone the repo**
```bash
git clone https://github.com/yourusername/ebanking-backend.git
cd ebanking-backend
```

2. **Build the project**
```bash
./mvnw clean install
```

3. **Run it**
```bash
./mvnw spring-boot:run
```

---

## 📌 Next Steps

- [ ] Add Angular frontend for GUI interactions
- [ ] Implement JWT-based authentication
- [ ] Add unit & integration tests
- [ ] Containerize with Docker

---

## 👤 Author

**Saad El Mabrouk**  
_M1 Big Data & Cloud Computing_  
📫 Contact: [Your Email or LinkedIn]

---

## 🛡️ License

This project is open-source and available under the [MIT License](LICENSE).