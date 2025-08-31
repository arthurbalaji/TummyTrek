// User related types
export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  status: UserStatus;
  profileImageUrl?: string;
  emailVerified: boolean;
  phoneVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  VENDOR = 'VENDOR',
  DELIVERY_PARTNER = 'DELIVERY_PARTNER',
  ADMIN = 'ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// Restaurant related types
export interface Restaurant {
  id: number;
  name: string;
  description?: string;
  address: string;
  latitude?: number;
  longitude?: number;
  phone?: string;
  email?: string;
  cuisineType?: string;
  imageUrl?: string;
  bannerImageUrl?: string;
  rating: number;
  totalReviews: number;
  openingTime?: string;
  closingTime?: string;
  deliveryTimeMin: number;
  deliveryTimeMax: number;
  minimumOrderAmount: number;
  deliveryFee: number;
  freeDeliveryAbove?: number;
  status: RestaurantStatus;
  isOpen: boolean;
  isAcceptingOrders: boolean;
  fssaiLicense?: string;
  gstNumber?: string;
  vendor: User;
  createdAt: string;
  updatedAt: string;
}

export enum RestaurantStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SUSPENDED = 'SUSPENDED',
  TEMPORARILY_CLOSED = 'TEMPORARILY_CLOSED'
}

// Order related types
export interface Order {
  id: number;
  orderNumber: string;
  customer: Customer;
  restaurant: Restaurant;
  deliveryPartner?: DeliveryPartner;
  status: OrderStatus;
  subtotal: number;
  deliveryFee: number;
  platformFee: number;
  taxAmount: number;
  discountAmount: number;
  totalAmount: number;
  paymentMethod?: PaymentMethod;
  paymentStatus: PaymentStatus;
  paymentTransactionId?: string;
  deliveryAddress: string;
  deliveryLatitude?: number;
  deliveryLongitude?: number;
  deliveryInstructions?: string;
  estimatedDeliveryTime?: string;
  actualDeliveryTime?: string;
  preparationTimeMinutes?: number;
  cookingStartedAt?: string;
  readyForPickupAt?: string;
  pickedUpAt?: string;
  outForDeliveryAt?: string;
  deliveredAt?: string;
  orderItems: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export enum OrderStatus {
  PLACED = 'PLACED',
  CONFIRMED = 'CONFIRMED',
  PREPARING = 'PREPARING',
  READY_FOR_PICKUP = 'READY_FOR_PICKUP',
  PICKED_UP = 'PICKED_UP',
  OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export enum PaymentMethod {
  CASH = 'CASH',
  CARD = 'CARD',
  UPI = 'UPI',
  WALLET = 'WALLET'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

// Customer related types
export interface Customer {
  id: number;
  user: User;
  loyaltyPoints: number;
  totalOrders: number;
  totalSpent: number;
  averageOrderValue: number;
  preferredCuisines?: string[];
  addresses: CustomerAddress[];
  createdAt: string;
  updatedAt: string;
}

export interface CustomerAddress {
  id: number;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  zipCode: string;
  latitude?: number;
  longitude?: number;
  isDefault: boolean;
  label: string;
}

// Delivery Partner related types
export interface DeliveryPartner {
  id: number;
  user: User;
  vehicleType: string;
  vehicleNumber: string;
  licenseNumber: string;
  currentLatitude?: number;
  currentLongitude?: number;
  status: DeliveryPartnerStatus;
  isAvailable: boolean;
  totalDeliveries: number;
  rating: number;
  totalReviews: number;
  createdAt: string;
  updatedAt: string;
}

export enum DeliveryPartnerStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SUSPENDED = 'SUSPENDED',
  ON_DUTY = 'ON_DUTY',
  OFF_DUTY = 'OFF_DUTY'
}

// Menu Item related types
export interface MenuItem {
  id: number;
  name: string;
  description?: string;
  price: number;
  category: string;
  imageUrl?: string;
  isVegetarian: boolean;
  isVegan: boolean;
  isGlutenFree: boolean;
  spiceLevel?: SpiceLevel;
  isAvailable: boolean;
  prepTimeMinutes: number;
  calories?: number;
  restaurant: Restaurant;
  customizations: MenuItemCustomization[];
  createdAt: string;
  updatedAt: string;
}

export enum SpiceLevel {
  MILD = 'MILD',
  MEDIUM = 'MEDIUM',
  SPICY = 'SPICY',
  EXTRA_SPICY = 'EXTRA_SPICY'
}

export interface MenuItemCustomization {
  id: number;
  name: string;
  type: CustomizationType;
  options: string[];
  minSelections: number;
  maxSelections: number;
  isRequired: boolean;
}

export enum CustomizationType {
  SINGLE_SELECT = 'SINGLE_SELECT',
  MULTI_SELECT = 'MULTI_SELECT'
}

export interface OrderItem {
  id: number;
  menuItem: MenuItem;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  specialInstructions?: string;
  customizations: OrderItemCustomization[];
}

export interface OrderItemCustomization {
  id: number;
  customization: MenuItemCustomization;
  selectedOptions: string[];
  additionalPrice: number;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

// Dashboard Analytics types
export interface DashboardStats {
  totalOrders: number;
  totalRevenue: number;
  totalCustomers: number;
  totalRestaurants: number;
  totalDeliveryPartners: number;
  todayOrders: number;
  todayRevenue: number;
  pendingApprovals: number;
}

export interface RevenueData {
  date: string;
  revenue: number;
  orders: number;
}

export interface OrderStats {
  placed: number;
  confirmed: number;
  preparing: number;
  readyForPickup: number;
  pickedUp: number;
  outForDelivery: number;
  delivered: number;
  cancelled: number;
}

// Authentication types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: User;
}

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  status: UserStatus;
}

// Notification related types
export interface Notification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  priority: NotificationPriority;
  read: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum NotificationType {
  ORDER = 'ORDER',
  RESTAURANT = 'RESTAURANT',
  DELIVERY_PARTNER = 'DELIVERY_PARTNER',
  COMPLAINT = 'COMPLAINT',
  SYSTEM = 'SYSTEM'
}

export enum NotificationPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}
