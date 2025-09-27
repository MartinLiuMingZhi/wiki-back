# 📚 Wiki Knowledge Management System

[![CI/CD](https://github.com/xichen/wiki/workflows/CI%2FCD/badge.svg)](https://github.com/xichen/wiki/actions)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-blue.svg)](https://github.com/xichen/wiki/security/dependabot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> A modern, full-featured knowledge management system built with Spring Boot, featuring document management, ebook library, and collaborative features.

[中文文档](README_CN.md) | [English Documentation](README.md)

## ✨ Features

### 📖 Document Management
- **Rich Text Editor**: Create and edit documents with advanced formatting
- **Version Control**: Track document changes and maintain history
- **Collaborative Editing**: Real-time collaboration with multiple users
- **Document Categories**: Organize documents with hierarchical categories
- **Search & Filter**: Powerful search capabilities with full-text search

### 📚 Ebook Library
- **Ebook Upload**: Support for PDF, EPUB, and other formats
- **Reading Progress**: Track reading progress across devices
- **Bookmark System**: Save and organize bookmarks
- **Personal Library**: Private ebook collections
- **Public Sharing**: Share ebooks with the community

### 👥 User Management
- **Authentication**: Secure JWT-based authentication
- **User Profiles**: Customizable user profiles and preferences
- **Role-based Access**: Granular permission system
- **Email Verification**: Secure account verification process

### 🔍 Advanced Features
- **Full-text Search**: Elasticsearch-powered search engine
- **Tag System**: Flexible tagging for content organization
- **Statistics Dashboard**: Comprehensive analytics and insights
- **File Management**: Secure file upload and storage
- **API Documentation**: Complete REST API with Swagger

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/xichen/wiki.git
   cd wiki
   ```

2. **Configure the database**
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE wiki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure application properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   # Edit the configuration file with your database and Redis settings
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Application: http://localhost:8080

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# Or build the Docker image
docker build -t wiki-app .
docker run -p 8080:8080 wiki-app
```

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.6, Spring Security, MyBatis Plus
- **Database**: MySQL 8.0, Redis 6.0
- **Authentication**: JWT (JSON Web Tokens)
- **File Storage**: Local filesystem / Cloud storage
- **Search**: Elasticsearch (optional)
- **Build Tool**: Maven 3.9+
- **Java Version**: OpenJDK 17

### Project Structure
```
src/
├── main/
│   ├── java/com/xichen/wiki/
│   │   ├── controller/     # REST API controllers
│   │   ├── service/        # Business logic services
│   │   ├── entity/         # Database entities
│   │   ├── mapper/         # MyBatis mappers
│   │   ├── dto/            # Data transfer objects
│   │   ├── config/         # Configuration classes
│   │   ├── security/       # Security configuration
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── application.properties
│       ├── sql/            # Database scripts
│       └── templates/      # Email templates
└── test/                   # Test files
```

## 📖 API Documentation

### Core Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `POST /api/auth/verify-email` - Email verification

#### Documents
- `GET /api/documents` - List documents
- `POST /api/documents` - Create document
- `PUT /api/documents/{id}` - Update document
- `DELETE /api/documents/{id}` - Delete document
- `GET /api/documents/{id}` - Get document details

#### Ebooks
- `GET /api/ebooks` - List ebooks
- `POST /api/ebooks/upload` - Upload ebook
- `GET /api/ebooks/{id}` - Get ebook details
- `POST /api/ebooks/{id}/bookmark` - Add bookmark
- `GET /api/ebooks/{id}/progress` - Get reading progress

#### Categories
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

#### Search
- `GET /api/search` - Global search
- `GET /api/search/documents` - Search documents
- `GET /api/search/ebooks` - Search ebooks

### Interactive API Documentation
Visit http://localhost:8080/swagger-ui.html for complete API documentation with interactive testing capabilities.

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/wiki
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=your_redis_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with coverage
./mvnw test jacoco:report
```

## 🚀 Deployment

### Production Deployment

1. **Build the application**
   ```bash
   ./mvnw clean package -Pproduction
   ```

2. **Deploy with Docker**
   ```bash
   docker-compose -f docker-compose.production.yml up -d
   ```

3. **Deploy to Kubernetes**
   ```bash
   kubectl apply -f k8s/production/
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

- **Documentation**: [Wiki](https://github.com/xichen/wiki/wiki)
- **Issues**: [GitHub Issues](https://github.com/xichen/wiki/issues)
- **Discussions**: [GitHub Discussions](https://github.com/xichen/wiki/discussions)

---

⭐ **Star this repository if you find it helpful!**
