# Audition API

## Overview

The **Audition API** is a Spring Boot-based microservice that provides endpoints to retrieve **posts** and **comments**
data
from [JSONPlaceholder](https://jsonplaceholder.typicode.com). It acts as a proxy API, fetching external data and
exposing it
through structured endpoints, while also providing input validation, error handling, logging, and tracing capabilities.

## Prerequisites

- Java 17
- Gradle 7.6
- An IDE such as IntelliJ IDEA

## Getting Started

### Clone the Repository

```
git clone https://github.com/shahriar048/audition-api.git
cd audition-api
```

### Build the Project

```
./gradlew clean build
```

### Run the Application

```
./gradlew bootRun
````

The application will start on http://localhost:8080.

### Running Tests & Code Analysis

```
./gradlew test
./gradlew check
```

## API Documentation

This application uses Swagger for API documentation. Once the application is running,
you can access the Swagger UI at the following URL: http://localhost:8080/swagger-ui/index.html

### License

This project is licensed under the MIT License. See the LICENSE file for details.

### Contact

For any inquiries or issues, please contact shahriar048@gmail.com.
