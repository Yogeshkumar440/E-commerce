# E-commerce Backend (Spring Boot + JWT Authentication)

This is a backend API for an e-commerce platform built using **Spring Boot** and **MySQL**, featuring **JWT authentication** and **role-based access control**.

## 🚀 Features

- **User Authentication & Authorization** (JWT-based)
- **Role-based Access Control** (Admin & User)
- **Product Management** (Add, Update, Delete, List)
- **Category Management**
- **Cart Management**
- **Order Management** (Place Orders, View Order History, Admin Order Control)
- **Image Upload for Products**

## 🛠️ Tech Stack

- **Backend:** Spring Boot, Spring Security, JWT, Spring Data JPA
- **Database:** MySQL
- **API Documentation:** Swagger
- **Build Tool:** Maven

## 📌 Installation & Setup

### 1️⃣ Clone the Repository

### 2️⃣ Configure the Database

Update `application.properties`:

### 3️⃣ Run the Application

The server will start at **[http://localhost:8080](http://localhost:8080)**.

## 🔐 Authentication (JWT)

- **Register:** `POST /api/auth/register`
- **Login:** `POST /api/auth/login`
- **Get JWT Token on Login** to access protected routes

## 📌 API Endpoints

### **🔑 Authentication**

| Method | Endpoint             | Description                       |
| ------ | -------------------- | --------------------------------- |
| `POST` | `/api/auth/register` | User Registration                 |
| `POST` | `/api/auth/login`    | User Login & JWT Token Generation |

### **🛍️ Product Management**

| Method   | Endpoint             | Description                      |
| -------- | -------------------- | -------------------------------- |
| `POST`   | `/api/products`      | Add a New Product *(Admin Only)* |
| `POST`   | `/api/products/bulk` | Add Products in bulk *(Admin Only)* |
| `GET`    | `/api/products`      | Get All Products                 |
| `GET`    | `/api/products/{id}` | Get Product by ID                |
| `PUT`    | `/api/products/{id}` | Update Product *(Admin Only)*    |
| `DELETE` | `/api/products/{id}` | Delete Product *(Admin Only)*    |

### **🛒 Cart & Order Management**

| Method | Endpoint            | Description         |
| ------ | ------------------- | ------------------- |
| `POST` | `/api/cart/add`     | Add Product to Cart |
| `GET`  | `/api/cart`         | View Cart           |
| `POST` | `/api/orders/place` | Place Order         |
| `GET`  | `/api/orders`       | View Order History  |


## 🏗️ Contributing

Feel free to submit a pull request! 😊


🚀 **Developed by Yogesh Kumar** | [GitHub](https://github.com/Yogeshkumar440)

Email : [yks20893@gmail.com](mailto\:yks20893@gmail.com)

LinkedIn: linkedin.com/in/YogeshKumar440
