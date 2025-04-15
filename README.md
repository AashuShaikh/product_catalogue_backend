# Product Catalog Backend (Spring Boot + Kotlin)

This is a **Product Catalog API** built using **Spring Boot with Kotlin** as part of my **"Learn and Implement" journey** to master Spring Boot for backend development.

It provides a RESTful API for managing products, categories, and tags, and includes JWT authentication.

---

## Features

- **Product Management**: Add, update, delete, and fetch products.
- **Category Management**: Organize products into categories.
- **Tagging System**: Give tags to products or Search product by Tags.
- **Authentication**: JWT-based login and registration.
- **Tech Stack**: Kotlin, Spring Boot, Spring Data JPA, PostgreSQL, Spring Security.
- **Clean Architecture**: Follows layered architecture with good separation of concerns.

---

## Learning Goals

This project is part of my Spring Boot with Kotlin roadmap. Here's what I focused on:
- Implementing a real-world REST API from scratch
- Working with relational databases using JPA
- Creating secure login and registration with JWT
- Building a maintainable backend with clear modular design

---

## Tech Stack

| Layer        | Tech                            |
|--------------|---------------------------------|
| Language     | Kotlin                          |
| Framework    | Spring Boot                     |
| Security     | Spring Security + JWT           |
| Database     | PostgreSQL                      |
| ORM          | Spring Data JPA                 |
| Build Tool   | Gradle                          |

---

## Steps to Use it:

- Clone the repository.
- In application.properties file present in src/main/kotlin/resources, update the **JWT_BASE64_SECRET** with your secret key and **USERNAME** with your database username.
- RUN the app.
