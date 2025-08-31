import axios, { AxiosResponse } from 'axios';
import {
  VendorDashboard,
  VendorEarnings,
  VendorOrderStats,
  Order,
  MenuItem,
  MenuCategory,
  MenuItemRequest,
  Restaurant,
  LoginRequest,
  RegisterRequest,
  LoginResponse,
  User,
  ApiResponse,
  VendorNotification
} from '../types';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('vendor_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('vendor_token');
      localStorage.removeItem('vendor_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> =>
    apiClient.post('/auth/login', credentials).then((res: AxiosResponse<ApiResponse<LoginResponse>>) => res.data),
  
  register: (userData: RegisterRequest): Promise<ApiResponse<LoginResponse>> =>
    apiClient.post('/auth/register', userData).then((res: AxiosResponse<ApiResponse<LoginResponse>>) => res.data),
  
  getProfile: (): Promise<ApiResponse<User>> =>
    apiClient.get('/auth/profile').then((res: AxiosResponse<ApiResponse<User>>) => res.data),
};

// Vendor Dashboard API
export const vendorAPI = {
  // Test endpoint
  test: (): Promise<ApiResponse<string>> =>
    apiClient.get('/vendor/test').then((res: AxiosResponse<ApiResponse<string>>) => res.data),
    
  // Dashboard
  getDashboardStats: (): Promise<ApiResponse<VendorDashboard>> =>
    apiClient.get('/vendor/dashboard').then((res: AxiosResponse<ApiResponse<VendorDashboard>>) => res.data),
  
  getEarningsData: (period: string = 'week', days: number = 7): Promise<ApiResponse<VendorEarnings[]>> =>
    apiClient.get(`/vendor/dashboard/earnings?period=${period}&days=${days}`).then((res: AxiosResponse<ApiResponse<VendorEarnings[]>>) => res.data),
  
  getOrderStats: (): Promise<ApiResponse<VendorOrderStats>> =>
    apiClient.get('/vendor/dashboard/order-stats').then((res: AxiosResponse<ApiResponse<VendorOrderStats>>) => res.data),

  // Orders
  getOrders: (page: number = 0, size: number = 10): Promise<ApiResponse<{ content: Order[], totalElements: number, totalPages: number }>> =>
    apiClient.get(`/vendor/orders?page=${page}&size=${size}`).then((res: AxiosResponse<ApiResponse<{ content: Order[], totalElements: number, totalPages: number }>>) => res.data),
  
  getPendingOrders: (): Promise<ApiResponse<Order[]>> =>
    apiClient.get('/vendor/orders/pending').then((res: AxiosResponse<ApiResponse<Order[]>>) => res.data),
  
  acceptOrder: (orderId: number, prepTimeMinutes?: number): Promise<ApiResponse<Order>> =>
    apiClient.post(`/vendor/orders/${orderId}/accept?${prepTimeMinutes ? `prepTimeMinutes=${prepTimeMinutes}` : ''}`).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),
  
  rejectOrder: (orderId: number, reason: string): Promise<ApiResponse<Order>> =>
    apiClient.post(`/vendor/orders/${orderId}/reject?reason=${encodeURIComponent(reason)}`).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),
  
  markOrderPreparing: (orderId: number): Promise<ApiResponse<Order>> =>
    apiClient.post(`/vendor/orders/${orderId}/preparing`).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),
  
  markOrderReady: (orderId: number): Promise<ApiResponse<Order>> =>
    apiClient.post(`/vendor/orders/${orderId}/ready`).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),

  // Menu Management
  getMenuItems: (): Promise<ApiResponse<MenuItem[]>> =>
    apiClient.get('/vendor/menu-items').then((res: AxiosResponse<ApiResponse<MenuItem[]>>) => res.data),
  
  getMenuCategories: (): Promise<ApiResponse<MenuCategory[]>> =>
    apiClient.get('/vendor/categories').then((res: AxiosResponse<ApiResponse<MenuCategory[]>>) => res.data),
  
  addMenuItem: (menuItem: any): Promise<ApiResponse<MenuItem>> =>
    apiClient.post('/vendor/menu-items', menuItem).then((res: AxiosResponse<ApiResponse<MenuItem>>) => res.data),
  
  updateMenuItem: (id: number, menuItem: any): Promise<ApiResponse<MenuItem>> =>
    apiClient.put(`/vendor/menu-items/${id}`, menuItem).then((res: AxiosResponse<ApiResponse<MenuItem>>) => res.data),
  
  deleteMenuItem: (id: number): Promise<ApiResponse<void>> =>
    apiClient.delete(`/vendor/menu-items/${id}`).then((res: AxiosResponse<ApiResponse<void>>) => res.data),
  
  updateMenuItemAvailability: (id: number, isAvailable: boolean): Promise<ApiResponse<void>> =>
    apiClient.put(`/vendor/menu-items/${id}/availability`, { isAvailable }).then((res: AxiosResponse<ApiResponse<void>>) => res.data),

  // Restaurant Profile
  getRestaurantProfile: (): Promise<ApiResponse<Restaurant>> =>
    apiClient.get('/vendor/restaurant').then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
  
  updateRestaurantProfile: (restaurant: any): Promise<ApiResponse<Restaurant>> =>
    apiClient.put('/vendor/restaurant', restaurant).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),

  // Settings
  getVendorSettings: (): Promise<ApiResponse<any>> =>
    apiClient.get('/vendor/settings').then((res: AxiosResponse<ApiResponse<any>>) => res.data),
  
  updateVendorProfile: (profile: any): Promise<ApiResponse<any>> =>
    apiClient.put('/vendor/profile', profile).then((res: AxiosResponse<ApiResponse<any>>) => res.data),
  
  updateNotificationSettings: (settings: any): Promise<ApiResponse<any>> =>
    apiClient.put('/vendor/settings/notifications', settings).then((res: AxiosResponse<ApiResponse<any>>) => res.data),
  
  updateSecuritySettings: (settings: any): Promise<ApiResponse<any>> =>
    apiClient.put('/vendor/settings/security', settings).then((res: AxiosResponse<ApiResponse<any>>) => res.data),
  
  updatePaymentSettings: (settings: any): Promise<ApiResponse<any>> =>
    apiClient.put('/vendor/settings/payment', settings).then((res: AxiosResponse<ApiResponse<any>>) => res.data),

  // Restaurant Status
  getRestaurant: (): Promise<ApiResponse<Restaurant>> =>
    apiClient.get('/vendor/restaurant').then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
  
  toggleRestaurantStatus: (isOpen: boolean, isAcceptingOrders: boolean): Promise<ApiResponse<Restaurant>> =>
    apiClient.post(`/vendor/restaurant/toggle-status?isOpen=${isOpen}&isAcceptingOrders=${isAcceptingOrders}`).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),

  // Analytics
  getTopSellingItems: (limit: number = 10): Promise<ApiResponse<MenuItem[]>> =>
    apiClient.get(`/vendor/menu/top-selling?limit=${limit}`).then((res: AxiosResponse<ApiResponse<MenuItem[]>>) => res.data),
  
  getOrdersByHour: (): Promise<ApiResponse<{ [key: string]: number }>> =>
    apiClient.get('/vendor/analytics/orders-by-hour').then((res: AxiosResponse<ApiResponse<{ [key: string]: number }>>) => res.data),
};

// Menu API
export const menuAPI = {
  getMenuItems: (restaurantId: number): Promise<ApiResponse<MenuItem[]>> =>
    apiClient.get(`/menu-items/restaurant/${restaurantId}`).then((res: AxiosResponse<ApiResponse<MenuItem[]>>) => res.data),
  
  getAvailableMenuItems: (restaurantId: number): Promise<ApiResponse<MenuItem[]>> =>
    apiClient.get(`/menu-items/restaurant/${restaurantId}/available`).then((res: AxiosResponse<ApiResponse<MenuItem[]>>) => res.data),
  
  getMenuItemsByCategory: (restaurantId: number, category: string): Promise<ApiResponse<MenuItem[]>> =>
    apiClient.get(`/menu-items/restaurant/${restaurantId}/category/${category}`).then((res: AxiosResponse<ApiResponse<MenuItem[]>>) => res.data),
  
  createMenuItem: (restaurantId: number, menuItem: MenuItemRequest): Promise<ApiResponse<MenuItem>> =>
    apiClient.post(`/menu-items/restaurant/${restaurantId}`, menuItem).then((res: AxiosResponse<ApiResponse<MenuItem>>) => res.data),
  
  updateMenuItem: (menuItemId: number, menuItem: MenuItemRequest): Promise<ApiResponse<MenuItem>> =>
    apiClient.put(`/menu-items/${menuItemId}`, menuItem).then((res: AxiosResponse<ApiResponse<MenuItem>>) => res.data),
  
  toggleAvailability: (menuItemId: number): Promise<ApiResponse<MenuItem>> =>
    apiClient.post(`/menu-items/${menuItemId}/toggle-availability`).then((res: AxiosResponse<ApiResponse<MenuItem>>) => res.data),
  
  deleteMenuItem: (menuItemId: number): Promise<ApiResponse<void>> =>
    apiClient.delete(`/menu-items/${menuItemId}`).then((res: AxiosResponse<ApiResponse<void>>) => res.data),
};

// Restaurant API
export const restaurantAPI = {
  getRestaurant: (restaurantId: number): Promise<ApiResponse<Restaurant>> =>
    apiClient.get(`/restaurants/${restaurantId}`).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
  
  updateRestaurant: (restaurantId: number, restaurant: Partial<Restaurant>): Promise<ApiResponse<Restaurant>> =>
    apiClient.put(`/restaurants/${restaurantId}`, restaurant).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
  
  toggleRestaurantStatus: (restaurantId: number): Promise<ApiResponse<Restaurant>> =>
    apiClient.post(`/restaurants/${restaurantId}/toggle-status`).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
  
  toggleAcceptingOrders: (restaurantId: number): Promise<ApiResponse<Restaurant>> =>
    apiClient.post(`/restaurants/${restaurantId}/toggle-orders`).then((res: AxiosResponse<ApiResponse<Restaurant>>) => res.data),
};

// Orders API
export const ordersAPI = {
  getOrder: (orderId: number): Promise<ApiResponse<Order>> =>
    apiClient.get(`/orders/${orderId}`).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),
  
  updateOrderStatus: (orderId: number, status: string, remarks?: string): Promise<ApiResponse<Order>> =>
    apiClient.put(`/orders/${orderId}/status`, { status, remarks }).then((res: AxiosResponse<ApiResponse<Order>>) => res.data),
};

// Notifications API (if needed)
export const notificationsAPI = {
  getNotifications: (): Promise<ApiResponse<VendorNotification[]>> =>
    apiClient.get('/notifications').then((res: AxiosResponse<ApiResponse<VendorNotification[]>>) => res.data),
  
  markAsRead: (notificationId: number): Promise<ApiResponse<void>> =>
    apiClient.put(`/notifications/${notificationId}/read`).then((res: AxiosResponse<ApiResponse<void>>) => res.data),
  
  markAllAsRead: (): Promise<ApiResponse<void>> =>
    apiClient.put('/notifications/read-all').then((res: AxiosResponse<ApiResponse<void>>) => res.data),
};

export default apiClient;
