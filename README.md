# Firebase Cloud Messaging (FCM) Notification Backend - Spring Boot

## Overview

This project implements Firebase Cloud Messaging (FCM) in a Spring Boot backend. It provides endpoints to register devices, manage topics, and send push notifications using Firebase Admin SDK. All data is persisted with PostgreSQL, following clean separation of concerns across layers.

---

## Features

* Device token registration and storage
* Topic creation and management
* Subscribe/unsubscribe tokens to topics
* Send notifications to topics or individual tokens
* Custom DTOs for clean request/response
* Exception handling and API error structure
* JPA Auditing and layered service architecture

---

## Technologies Used

* Java 17
* Spring Boot 3
* Firebase Admin SDK
* PostgreSQL
* Maven

---

## Project Structure

```
├── configuration
│   ├── AuditModelConfiguration.java         # Enables JPA auditing
│   ├── FirebaseConfiguration.java           # Initializes Firebase app with credentials
│   └── GlobalConfiguration.java             # Global beans and settings
│
├── controller
│   ├── FcmNotificationController.java       # Endpoint for sending messages
│   ├── FcmRecipientController.java          # Endpoint for device registration
│   └── FcmTopicController.java              # Endpoint for topic operations
│
├── dto
│   ├── request/
│   │   ├── FcmNotificationDTO.java          # Payload to send FCM messages
│   │   ├── FcmNotificationStatusDTO.java    # Status feedback
│   │   ├── FcmRecipientDTO.java             # Register token + user info
│   │   ├── FcmSubscribeDTO.java             # Subscribe/unsubscribe info
│   │   └── FcmTopicDTO.java                 # Create topic
│   ├── response/
│   │   ├── ApiResponse.java                 # Standard response wrapper
│   │   ├── FcmRecipientResponseDTO.java     # Response for token fetch
│   │   └── TokenAndTopicDTO.java            # Token-topic mapping response
│   └── error/ApiErrorResponse.java          # Error format
│
├── entity
│   ├── FcmRecipient.java                   # User-token entity
│   ├── FcmTopic.java                       # Topic entity
│   └── audit/AuditModel.java               # Auditable base class
│
├── enums
│   └── ErrorCode.java                      # Enum for error codes
│
├── exception
│   ├── FirebaseInternalException.java      # Firebase wrapper exception
│   ├── RecordNotFoundException.java        # Custom 404 exception
│   └── handler/GlobalExceptionHandler.java # Global @ControllerAdvice
│
├── repository
│   ├── FcmRecipientRepository.java         # CRUD for recipients
│   └── FcmTopicRepository.java             # CRUD for topics
│
├── service
│   ├── FcmRecipientService.java            # Recipient interface
│   ├── FcmTopicService.java                # Topic interface
│   ├── FirebaseMessagingService.java       # Messaging interface
│   └── impl/
│       ├── FcmRecipientServiceImpl.java
│       ├── FcmTopicServiceImpl.java
│       └── FirebaseMessagingServiceImpl.java
│
├── util
│   └── Constants.java                      # Constant keys and messages
│
├── resources
│   ├── application.properties              # Optional
│   └── application.yml                     # Firebase path, DB config
└── test/
    └── FcmNotificationImplementationApplicationTests.java
```

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/sathishmadem123/fcm-notification-implementation.git
cd fcm-notification-implementation
```

### 2. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a project or open existing one
3. Navigate to **Project Settings → Service Accounts**
4. Click **Generate New Private Key**, save the JSON file
5. Move it to: `src/main/resources/firebase/firebase-config.json`
6. Make sure `application.yml` has:

```yaml
fcm:
  service-account:
    file-path: classpath:firebase/firebase-config.json
```

### 3. PostgreSQL Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fcm_db
    username: your_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Create the database manually:

```sql
CREATE DATABASE fcm_db;
```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The app will run at `http://localhost:8080`

---

## Postman Collection

A complete Postman collection is already included in the root of the GitHub repository. You can import it directly into Postman to test all the endpoints:

1. Open Postman
2. Click **Import** → **Upload Files**
3. Choose the provided `.postman_collection.json` file from the repo

---

## Notes

* Ensure Firebase credential path is correct in `application.yml`
* All Firebase logic is encapsulated in `FirebaseMessagingServiceImpl`
* Global exception responses follow a consistent error model
* Uses JPA auditing for created/updated timestamps

---

## License

This repository is open for learning, prototyping and backend integration with Firebase Cloud Messaging using Spring Boot.

---

For any suggestions or issues, please raise an issue in the GitHub repository.
