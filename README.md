# Chachatte Team GraphQL

**Spring Boot** application to expose **GraphQL** endpoint for **Chachatte team** mobile application

![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg?style=for-the-badge)

![Version](https://img.shields.io/badge/JDK-11-red.svg)
![Version](https://img.shields.io/badge/Spring%20Boot-2.3.4-green.svg)

## Documentation

User roles :
- NORMAL : default user role, not member of the team
- MEMBER : members that have been accepted in the team
- ADMIN : administrator users

Application uses only a few starters and libraries :
- **Spring data JPA** starter as ORM
- **GraphQL** starter to expose data via GraphQL
- **GraphiQL** starter to get an UI for testing GraphQL queries
- **Lombok** library to avoid boilerplate code
- **MariaDB** client to connect to the database

