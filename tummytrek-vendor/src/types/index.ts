// Vendor Dashboard Types
export interface VendorDashboard {
  restaurantId: number;
  restaurantName: string;
  isOpen: boolean;
  isAcceptingOrders: boolean;
  totalOrders: number;
  totalMenuItems: number;
  todayOrders: number;
  todayEarnings: number;
  weekOrders: number;
  weekEarnings: number;
  monthOrders: number;
  monthEarnings: number;
  totalEarnings: number;
  pendingOrders: number;
  averageRating: number;
}

// Vendor Earnings Types
export interface VendorEarnings {
  date: string;
  earnings: number;
  orderCount: number;
}

// Vendor Order Stats Types
export interface VendorOrderStats {
  placed: number;
  confirmed: number;
  preparing: number;
  ready: number;
  pickedUp: number;
  delivered: number;
  cancelled: number;
}

// Vendor Earnings Response Types
export interface VendorEarningsResponse {
  totalEarnings: number;
  averageOrderValue: number;
  dailyEarnings: VendorEarnings[];
}

// Vendor Order Stats Response Types  
export interface VendorOrderStatsResponse {
  totalOrders: number;
  deliveredOrders: number;
  preparingOrders: number;
  confirmedOrders: number;
  cancelledOrders: number;
}

// Menu Category Types
export interface MenuCategory {
  id: number;
  name: string;
  description?: string;
  displayOrder: number;
}

// Business Hours Types
export interface BusinessHours {
  dayOfWeek: number;
  openTime: string;
  closeTime: string;
  isOpen: boolean;
}

// Restaurant Types
export interface Restaurant {
  id: number;
  name: string;
  description?: string;
  address: string;
  phone: string;
  email?: string;
  cuisine: string;
  priceRange?: string;
  website?: string;
  imageUrl?: string;
  openingTime: string;
  closingTime: string;
  minimumOrderAmount?: number;
  deliveryFee?: number;
  maxDeliveryDistance?: number;
  rating?: number;
  isOpen: boolean;
  isAcceptingOrders: boolean;
  isActive: boolean;
  status: RestaurantStatus;
  fssaiLicense: string;
  gstNumber: string;
  bankAccountNumber: string;
  bankName: string;
  ifscCode: string;
  latitude: number;
  longitude: number;
  totalOrders?: number;
  businessHours?: BusinessHours[];
  createdAt: string;
  updatedAt: string;
}

export enum RestaurantStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SUSPENDED = 'SUSPENDED'
}

// Menu Item Types
export interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  discountedPrice?: number;
  category: MenuCategory;
  isVegetarian: boolean;
  isVegan: boolean;
  isGlutenFree: boolean;
  isAvailable: boolean;
  isActive: boolean;
  imageUrl?: string;
  preparationTime: number;
  ingredients?: string[];
  nutritionInfo?: string;
  tags?: string;
  rating?: number;
  totalOrders: number;
  createdAt: string;
  updatedAt: string;
}

// Order Types
export interface Order {
  id: number;
  orderNumber: string;
  totalAmount: number;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethod;
  deliveryAddress: string;
  specialInstructions?: string;
  estimatedDeliveryTime?: string;
  actualDeliveryTime?: string;
  createdAt: string;
  updatedAt: string;
  customer: Customer;
  restaurant: Restaurant;
  orderItems: OrderItem[];
  deliveryPartner?: DeliveryPartner;
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

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

export enum PaymentMethod {
  CASH_ON_DELIVERY = 'CASH_ON_DELIVERY',
  UPI = 'UPI',
  CARD = 'CARD',
  WALLET = 'WALLET'
}

export interface OrderItem {
  id: number;
  quantity: number;
  price: number;
  menuItem: MenuItem;
  customizations?: string;
}

export interface Customer {
  id: number;
  name: string;
  email: string;
  phone: string;
  addresses: Address[];
}

export interface Address {
  id: number;
  fullAddress: string;
  landmark?: string;
  latitude: number;
  longitude: number;
  isDefault: boolean;
}

export interface DeliveryPartner {
  id: number;
  name: string;
  phone: string;
  vehicleNumber: string;
  rating: number;
  status: string;
}

// Auth Types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  isActive: boolean;
  createdAt: string;
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  VENDOR = 'VENDOR',
  DELIVERY_PARTNER = 'DELIVERY_PARTNER',
  ADMIN = 'ADMIN'
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// Menu Item Request Types
export interface MenuItemRequest {
  name: string;
  description: string;
  price: number;
  discountedPrice?: number;
  category: string;
  isVegetarian: boolean;
  isAvailable: boolean;
  imageUrl?: string;
  preparationTime: number;
  tags?: string;
}

// Notification Types
export interface VendorNotification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  referenceId?: number;
  referenceType?: string;
}

export enum NotificationType {
  ORDER = 'ORDER',
  RESTAURANT = 'RESTAURANT',
  DELIVERY_PARTNER = 'DELIVERY_PARTNER',
  COMPLAINT = 'COMPLAINT',
  SYSTEM = 'SYSTEM'
}
