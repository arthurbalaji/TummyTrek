# TummyTrek Admin Dashboard

A comprehensive React TypeScript admin dashboard for managing the TummyTrek food delivery platform.

## Features

### 🏪 Restaurant Management
- View all restaurants with status indicators
- Manage restaurant information and status
- Monitor restaurant performance metrics
- Handle restaurant onboarding

### 👥 Customer Management
- Customer list with search and filtering
- View customer details and order history
- Manage customer loyalty points
- Customer support and account management

### 📦 Order Management
- Real-time order tracking and status updates
- Order details with timeline view
- Order assignment to delivery partners
- Revenue and order analytics

### 🚴 Delivery Partner Management
- Manage delivery partner profiles
- Track performance metrics and ratings
- Handle partner onboarding and verification
- Monitor delivery statistics

### 📊 Analytics Dashboard
- Revenue trends and key metrics
- Order volume and growth analytics
- Performance insights across all entities
- Real-time statistics and reporting

### 🔐 Secure Authentication
- JWT-based admin authentication
- Role-based access control
- Secure API integration with backend

## Technology Stack

- **Frontend**: React 18 + TypeScript
- **Styling**: Tailwind CSS
- **Routing**: React Router v6
- **Icons**: Lucide React
- **Charts**: Recharts
- **Forms**: React Hook Form
- **HTTP Client**: Axios
- **Notifications**: React Hot Toast

## Project Structure

```
src/
├── components/           # Reusable UI components
│   ├── Layout.tsx       # Main layout wrapper
│   ├── Sidebar.tsx      # Navigation sidebar
│   ├── Header.tsx       # Top navigation header
│   └── ProtectedRoute.tsx # Route protection
├── contexts/            # React contexts
│   └── AuthContext.tsx  # Authentication state management
├── pages/              # Main application pages
│   ├── Login.tsx       # Authentication page
│   ├── Dashboard.tsx   # Main dashboard with analytics
│   ├── Restaurants.tsx # Restaurant management
│   ├── Customers.tsx   # Customer management
│   ├── Orders.tsx      # Order tracking and management
│   ├── DeliveryPartners.tsx # Delivery partner management
│   ├── Analytics.tsx   # Detailed analytics
│   ├── Notifications.tsx # System notifications
│   └── Settings.tsx    # Application settings
├── services/           # API services
│   └── api.ts          # Axios configuration and API calls
├── types/              # TypeScript type definitions
│   └── index.ts        # All application types
└── App.tsx            # Main application component
```

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- TummyTrek Backend API running

### Installation

1. Clone the repository and navigate to the admin directory:
```bash
cd TummyTrek/tummytrek-admin
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment variables:
```bash
# .env file is already created with:
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

4. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

### Demo Credentials

For testing purposes, use these demo credentials:
- **Email**: admin@tummytrek.com
- **Password**: admin123

*Note: These work with the mock authentication in the frontend. Update the API service to connect to your actual backend.*

## API Integration

The admin dashboard is designed to work with the TummyTrek Spring Boot backend. Key endpoints include:

- `POST /auth/login` - Admin authentication
- `GET /admin/restaurants` - Restaurant management
- `GET /admin/customers` - Customer data
- `GET /admin/orders` - Order management
- `GET /admin/delivery-partners` - Delivery partner data
- `GET /admin/analytics/*` - Various analytics endpoints

## Development

### Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

### Code Style

The project uses:
- TypeScript for type safety
- Tailwind CSS for styling
- ESLint for code quality
- Consistent component structure

### Adding New Features

1. Define types in `src/types/index.ts`
2. Add API methods in `src/services/api.ts`
3. Create components in `src/components/`
4. Add pages in `src/pages/`
5. Update routing in `src/App.tsx`

## Deployment

### Production Build

```bash
npm run build
```

### Environment Configuration

Update the `.env` file with your production API URL:
```
REACT_APP_API_BASE_URL=https://your-api-domain.com/api
```

## Contributing

1. Follow the existing code structure and patterns
2. Use TypeScript interfaces for all data structures
3. Implement proper error handling
4. Add loading states for async operations
5. Ensure responsive design with Tailwind CSS

## License

This project is part of the TummyTrek food delivery platform.

## Support

For technical support or feature requests, please refer to the main TummyTrek documentation or contact the development team.
