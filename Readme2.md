# 🚀 API Scan SaaS Backend

A scalable backend system for API security scanning built with Spring Boot, PostgreSQL, and RabbitMQ. Designed with asynchronous processing to handle large-scale scan jobs efficiently.

---

## 🧱 Tech Stack

* Java (Spring Boot)
* PostgreSQL (Database)
* RabbitMQ (Message Queue)
* Docker (Containerization)
* Maven (Build Tool)

---

## ⚙️ Features

* User Authentication & Authorization
* Organization (Multi-tenant) support
* Async API scan processing using RabbitMQ
* Scalable worker-based architecture
* RESTful APIs

---

## 🏗️ Architecture

```
Client → REST API → RabbitMQ → Worker → Database
```

* **Producer**: Sends scan jobs to queue
* **RabbitMQ**: Message broker
* **Consumer (Worker)**: Processes scan asynchronously

---

## 🐇 RabbitMQ Setup

### Step 1: Run RabbitMQ using Docker

```bash
docker run -d \
--hostname rabbit \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
-e RABBITMQ_DEFAULT_USER=apiscan_prod \
-e RABBITMQ_DEFAULT_PASS=StrongPass@123 \
rabbitmq:3-management
```

---

### Step 2: Access RabbitMQ Dashboard

URL:

```
http://localhost:15672
```

Credentials:

```
Username: apiscan_prod
Password: StrongPass@123
```

---

## ⚙️ Application Configuration

Update `application.yml`:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: apiscan_prod
    password: StrongPass@123
    virtual-host: /
```

---

## 🏃 Running the Application

### Step 1: Build project

```bash
mvn clean install
```

---

### Step 2: Run application

```bash
mvn spring-boot:run
```

---

## 🧪 Testing Flow

1. Start RabbitMQ
2. Start backend
3. Call API:

```
POST /scan
```

4. Observe logs:

```
Processing scan...
Scan completed...
```

5. Verify in RabbitMQ UI (Queues)

---

## 📂 Project Structure

```
src/
 ├── controller/
 ├── service/
 ├── config/
 ├── worker/
 ├── model/
 └── repository/
```

---

## 🔐 Environment Variables (Recommended)

Instead of hardcoding:

```env
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=apiscan_prod
RABBITMQ_PASSWORD=StrongPass@123
```

---

## 🚧 Future Enhancements

* Redis integration (caching, rate limiting)
* Scan result persistence
* Email notifications
* Role-based access control
* Kubernetes deployment

---

## 🧠 Key Concept

* Organizations interact with APIs only
* RabbitMQ is internal infrastructure
* Async processing ensures scalability

---

## 👨‍💻 Author

ApiScan Backend System
