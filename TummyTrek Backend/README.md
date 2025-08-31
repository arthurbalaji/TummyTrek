# TummyTrek Food Delivery Backend

A complete Spring Boot backend for a food delivery application with PostgreSQL database.

## Features

### User Management
- Customer registration and authentication
- Vendor/Restaurant owner registration
- Delivery partner registration
- Admin management
- JWT-based authentication
- Role-based access control

### Customer Features
- Browse restaurants and menus
- Place and track orders
- Multiple delivery addresses
- Payment processing
- Order history
- Loyalty points system
- Restaurant and delivery partner reviews

### Vendor/Restaurant Features
- Restaurant profile management
- Menu management with customizations
- Order acceptance/rejection
- Real-time order status updates
- Earnings and sales reports
- Promotional offers management

### Delivery Partner Features
- Real-time order assignment
- GPS location tracking
- Delivery status updates
- Earnings tracking
- Shift management

### Admin Features
- User and restaurant approval
- Order monitoring
- Analytics dashboard
- Payment management
- Dispute resolution

## Technology Stack

- **Framework**: Spring Boot 3.1.0
- **Database**: PostgreSQL 15
- **Security**: Spring Security with JWT
- **ORM**: JPA/Hibernate
- **Validation**: Bean Validation
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Containerization**: Docker

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- Git

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Food\ App
```

### 2. Start PostgreSQL with Docker
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Redis cache on port 6379

### 3. Configure Application
Update `src/main/resources/application.properties` if needed:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/tummytrek_db
spring.datasource.username=tummytrek_user
spring.datasource.password=tummytrek_password

# JWT Configuration
jwt.secret=TummyTrekSecretKeyForJWTTokenGenerationAndValidation2024
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### 4. Build and Run the Application
```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access API Documentation
Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile

### Public Endpoints
- `GET /api/public/restaurants` - Get available restaurants
- `GET /api/public/restaurants/{id}/menu` - Get restaurant menu

### Customer Endpoints
- `GET /api/customers/profile` - Get customer profile
- `POST /api/customers/addresses` - Add delivery address
- `GET /api/orders/customer/history` - Get order history
- `POST /api/orders` - Place new order

### Vendor Endpoints
- `GET /api/vendors/dashboard` - Vendor dashboard
- `POST /api/restaurants` - Create/update restaurant
- `POST /api/menu-items` - Add menu items
- `GET /api/orders/vendor/pending` - Get pending orders

### Delivery Partner Endpoints
- `POST /api/delivery-partners/location` - Update location
- `GET /api/orders/delivery/assigned` - Get assigned orders
- `PUT /api/orders/{id}/status` - Update order status

### Admin Endpoints
- `GET /api/admin/dashboard` - Admin dashboard
- `GET /api/admin/users` - Manage users
- `GET /api/admin/restaurants/pending` - Approve restaurants

## Database Schema

The application uses the following main entities:

1. **User** - Base user entity for all roles
2. **Customer** - Customer-specific information
3. **Restaurant** - Restaurant/vendor information
4. **MenuItem** - Menu items with customizations
5. **Order** - Order management
6. **OrderItem** - Individual order items
7. **DeliveryPartner** - Delivery partner information
8. **Reviews** - Restaurant and delivery partner reviews

## Environment Variables

You can override configuration using environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/tummytrek_db
export DATABASE_USERNAME=tummytrek_user
export DATABASE_PASSWORD=tummytrek_password
export JWT_SECRET=your-secret-key
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -DskipTests
java -jar target/tummytrek-backend-1.0.0.jar
```

### Database Migration
The application uses Hibernate DDL auto-generation. For production, consider using Flyway or Liquibase for database migrations.

## Docker Deployment

### Build Docker Image
```bash
docker build -t tummytrek-backend .
```

### Run with Docker Compose
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support, please contact the development team or create an issue in the repository.
