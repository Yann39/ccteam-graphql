# CCTeam GraphQL

**Spring Boot** application to expose **GraphQL** endpoint for **CCTeam** mobile application.

![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg?style=for-the-badge)

![Version](https://img.shields.io/badge/JDK-19-red.svg)
![Version](https://img.shields.io/badge/Spring%20Boot-3.0.0-green.svg)

## Documentation

User roles :
- NORMAL : default user role, not member of the team
- MEMBER : members that have been accepted in the team
- ADMIN : administrator users

Application uses only a few starters and libraries :
- **Spring data JPA** starter as ORM
- **Spring Boot GraphQL** starter to expose data via GraphQL
- **Lombok** library to avoid boilerplate code
- **MariaDB** client to connect to the database

# License

[General Public License (GPL) v3](https://www.gnu.org/licenses/gpl-3.0.en.html)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not,
see <http://www.gnu.org/licenses/>.