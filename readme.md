# 🏢 Real Estate CRM - Enterprise Backend API

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0%2B-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-lightgrey.svg)
![JWT Security](https://img.shields.io/badge/Security-JWT-orange.svg)
![Flyway](https://img.shields.io/badge/Database-Flyway-red.svg)

A robust, full-stack enterprise backend designed specifically for real estate agencies and property management firms. Built to handle high-value transactions, this system features role-based access control, polymorphic audit trails, financial installment tracking, and automated branded reporting.

Developed by Muhammad Hasnain Tahir ~cofounder **Shanayn Labs**.

## 🚀 Tech Stack

* **Core Framework:** Java 17, Spring Boot (Spring Web, Spring Data JPA)
* **Security:** Spring Security, JSON Web Tokens (JWT), Role-Based Access Control (RBAC)
* **Database:** MySQL, Flyway (for automated schema migrations)
* **Reporting:** OpenPDF (Automated watermarked document generation)
* **Documentation:** OpenAPI / Swagger UI
* **Storage:** Local File System (Scalable to AWS S3)

---

## 🛠 Core Modules & Features

### 1. Identity & Access Management
* JWT-based authentication and authorization.
* Strict API-level RBAC restricting endpoints to specific roles (`ADMIN`, `MANAGER`, `AGENT`).

### 2. Inventory & Property Management
* Complete tracking of real estate units, blocks, and plot sizes.
* Real-time status updates (`AVAILABLE`, `BOOKED`, `SOLD`, `ON_HOLD`).

### 3. Financial Engine (Bookings & Installments)
* End-to-end booking lifecycle management.
* Dynamic installment plan generation (Monthly, Quarterly, Bi-Annually) linked to specific property bookings.
* Payment tracking, due date management, and automated defaulter identification.

### 4. Operations & Task Management
* Internal task assignment and follow-up tracking for sales agents.
* Priority levels and status workflows (`PENDING`, `IN_PROGRESS`, `COMPLETED`).

### 5. Document Management
* Secure, multipart file uploads for KYC, payment receipts, and property deeds.
* Polymorphic attachment system (attach a document to a Customer, Booking, or Unit).
* Secure file renaming using UUIDs to prevent path traversal and file overwriting.

### 6. The "Flight Recorder" (Audit Logs)
* An immutable, polymorphic background audit system.
* Silently tracks critical actions (Status Changes, Payment Processing, Document Uploads) across the entire application.
* Provides a complete history timeline of *who* did *what*, and *when*.

### 7. Executive Dashboard & Analytics
* High-performance JPA/Native SQL aggregations for top-level KPIs (Total Revenue, Cash Collected, Active Defaulters).
* Time-series data formatting for frontend charting.
* Automated, downloadable PDF analytics reports featuring dynamic data and custom agency watermarking.

---

## ⚙️ Getting Started

### Prerequisites
* Java 17 or higher
* Maven
* MySQL 8.0+

### Database Configuration
1. Create a fresh MySQL database named `realestate` (or your preferred name).
2. Open `src/main/resources/application.yml` (or `.properties`) and update your credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/realestate?createDatabaseIfNotExist=true
    username: your_mysql_username
    password: your_mysql_password



