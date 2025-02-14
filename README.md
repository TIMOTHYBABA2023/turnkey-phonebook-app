# Spring Boot Application

This is a Spring Boot application built with Java 17, PostgreSQL as the database, Spring OpenAPI for API documentation, and ModelMapper for entity-to-DTO conversion.

## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)

## Requirements
Ensure you have the following installed before running the application:
- [Java 17](https://adoptium.net/)
- [Gradle](https://gradle.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Git](https://git-scm.com/)

## Installation
1. Clone the repository:
   ```sh
   git clone <repository-url>
   cd <project-directory>
   ```
2. Build the project using Gradle:
   ```sh
   ./gradlew build
   ```

## Configuration
The application requires PostgreSQL configuration. Update `application.yml` in the `src/main/resources` directory:

### Example (application.yml):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database_name
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

## Running the Application
Run the application using:
```sh
./gradlew bootRun
```
OR
```sh
java -jar build/libs/your-application.jar
```

## API Documentation
This application uses Spring OpenAPI (Swagger) for API documentation. Once the application is running, access the API documentation at:
```
http://localhost:8080/swagger-ui.html
```
OR
```
http://localhost:8080/v3/api-docs
```

## Technologies Used
- Java 17
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Spring OpenAPI (Swagger)
- ModelMapper
- Gradle

## Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a Pull Request.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

