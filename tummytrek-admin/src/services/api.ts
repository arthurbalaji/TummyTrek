import axios, { AxiosResponse } from 'axios';
import { 
  ApiResponse, 
  LoginRequest, 
  LoginResponse, 
  User, 
  Restaurant, 
  Customer, 
  Order, 
  DeliveryPartner,
  MenuItem,
  DashboardStats,
  RevenueData,
  OrderStats,
  Notification
} from '../types';

// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken');
      localStorage.removeItem('adminUser');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Authentication API
export const authAPI = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response: AxiosResponse<LoginResponse> = await api.post('/auth/login', credentials);
    return response.data;
  },

  getProfile: async (): Promise<User> => {
    const response: AxiosResponse<User> = await api.get('/auth/profile');
    return response.data;
  },
};

// Dashboard API
export const dashboardAPI = {
  getStats: async (): Promise<DashboardStats> => {
    const response: AxiosResponse<ApiResponse<DashboardStats>> = await api.get('/admin/dashboard/stats');
    return response.data.data!;
  },

  getRevenueData: async (period: string): Promise<RevenueData[]> => {
    const days = period === 'week' ? 7 : period === 'month' ? 30 : 90;
    const response: AxiosResponse<ApiResponse<RevenueData[]>> = await api.get(`/admin/dashboard/revenue?period=${period}&days=${days}`);
    return response.data.data || [];
  },

  getOrderStats: async (): Promise<OrderStats> => {
    const response: AxiosResponse<ApiResponse<OrderStats>> = await api.get('/admin/dashboard/orders/stats');
    return response.data.data!;
  },
};

// Restaurants API
export const restaurantsAPI = {
  getAll: async (): Promise<Restaurant[]> => {
    const response: AxiosResponse<ApiResponse<Restaurant[]>> = await api.get('/restaurants/admin/all');
    return response.data.data || [];
  },

  getById: async (id: number): Promise<Restaurant> => {
    const response: AxiosResponse<ApiResponse<Restaurant>> = await api.get(`/restaurants/${id}`);
    return response.data.data!;
  },

  approve: async (id: number): Promise<Restaurant> => {
    const response: AxiosResponse<ApiResponse<Restaurant>> = await api.post(`/restaurants/${id}/approve`);
    return response.data.data!;
  },

  reject: async (id: number, reason: string): Promise<Restaurant> => {
    const response: AxiosResponse<ApiResponse<Restaurant>> = await api.post(`/restaurants/${id}/reject?reason=${encodeURIComponent(reason)}`);
    return response.data.data!;
  },

  toggleStatus: async (id: number): Promise<Restaurant> => {
    const response: AxiosResponse<ApiResponse<Restaurant>> = await api.post(`/restaurants/${id}/toggle-status`);
    return response.data.data!;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/restaurants/${id}`);
  },
};

// Customers API
export const customersAPI = {
  getAll: async (): Promise<Customer[]> => {
    const response: AxiosResponse<ApiResponse<Customer[]>> = await api.get('/customers');
    return response.data.data || [];
  },

  getById: async (id: number): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.get(`/customers/${id}`);
    return response.data.data!;
  },

  addLoyaltyPoints: async (customerId: number, points: number): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.post(`/customers/${customerId}/loyalty-points/add?points=${points}`);
    return response.data.data!;
  },

  deductLoyaltyPoints: async (customerId: number, points: number): Promise<Customer> => {
    const response: AxiosResponse<ApiResponse<Customer>> = await api.post(`/customers/${customerId}/loyalty-points/deduct?points=${points}`);
    return response.data.data!;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/customers/${id}`);
  },
};

// Orders API
export const ordersAPI = {
  getAll: async (): Promise<Order[]> => {
    const response: AxiosResponse<ApiResponse<Order[]>> = await api.get('/admin/orders/recent?limit=100');
    return response.data.data || [];
  },

  getById: async (id: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.get(`/orders/${id}`);
    return response.data.data!;
  },

  updateStatus: async (id: number, status: string): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${id}/status`, { status });
    return response.data.data!;
  },

  getTodayRevenue: async (): Promise<number> => {
    const response: AxiosResponse<ApiResponse<number>> = await api.get('/orders/revenue/today');
    return response.data.data || 0;
  },

  getPendingOrders: async (): Promise<Order[]> => {
    const response: AxiosResponse<ApiResponse<Order[]>> = await api.get('/orders/pending');
    return response.data.data || [];
  },

  getCustomerOrders: async (customerId: number): Promise<Order[]> => {
    const response: AxiosResponse<ApiResponse<Order[]>> = await api.get(`/orders/customer/${customerId}`);
    return response.data.data || [];
  },

  getRestaurantOrders: async (restaurantId: number): Promise<Order[]> => {
    const response: AxiosResponse<ApiResponse<Order[]>> = await api.get(`/orders/restaurant/${restaurantId}`);
    return response.data.data || [];
  },

  assignDeliveryPartner: async (orderId: number, deliveryPartnerId: number): Promise<Order> => {
    const response: AxiosResponse<ApiResponse<Order>> = await api.put(`/orders/${orderId}/assign-delivery-partner?deliveryPartnerId=${deliveryPartnerId}`);
    return response.data.data!;
  },
};

// Delivery Partners API
export const deliveryPartnersAPI = {
  getAll: async (): Promise<DeliveryPartner[]> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner[]>> = await api.get('/delivery-partners');
    return response.data.data || [];
  },

  getById: async (id: number): Promise<DeliveryPartner> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner>> = await api.get(`/delivery-partners/${id}`);
    return response.data.data!;
  },

  approve: async (id: number): Promise<DeliveryPartner> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner>> = await api.post(`/delivery-partners/${id}/approve`);
    return response.data.data!;
  },

  reject: async (id: number, reason: string): Promise<DeliveryPartner> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner>> = await api.post(`/delivery-partners/${id}/reject?reason=${encodeURIComponent(reason)}`);
    return response.data.data!;
  },

  suspend: async (id: number, reason: string): Promise<DeliveryPartner> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner>> = await api.post(`/delivery-partners/${id}/suspend?reason=${encodeURIComponent(reason)}`);
    return response.data.data!;
  },

  reactivate: async (id: number): Promise<DeliveryPartner> => {
    const response: AxiosResponse<ApiResponse<DeliveryPartner>> = await api.post(`/delivery-partners/${id}/reactivate`);
    return response.data.data!;
  },

  toggleStatus: async (id: number): Promise<DeliveryPartner> => {
    // This will either suspend or reactivate based on current status
    const partner = await deliveryPartnersAPI.getById(id);
    if (partner.status === 'SUSPENDED') {
      return deliveryPartnersAPI.reactivate(id);
    } else {
      return deliveryPartnersAPI.suspend(id, 'Manual suspension');
    }
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/delivery-partners/${id}`);
  },
};

// Menu Items API
export const menuItemsAPI = {
  getByRestaurant: async (restaurantId: number): Promise<MenuItem[]> => {
    const response: AxiosResponse<ApiResponse<MenuItem[]>> = await api.get(`/menu-items/restaurant/${restaurantId}`);
    return response.data.data || [];
  },

  getById: async (id: number): Promise<MenuItem> => {
    const response: AxiosResponse<ApiResponse<MenuItem>> = await api.get(`/menu-items/${id}`);
    return response.data.data!;
  },

  toggleAvailability: async (id: number): Promise<MenuItem> => {
    const response: AxiosResponse<ApiResponse<MenuItem>> = await api.post(`/menu-items/${id}/toggle-availability`);
    return response.data.data!;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/menu-items/${id}`);
  },
};

// Notifications API
export const notificationsAPI = {
  getAll: async (): Promise<Notification[]> => {
    const response: AxiosResponse<ApiResponse<Notification[]>> = await api.get('/admin/notifications');
    return response.data.data || [];
  },

  markAsRead: async (id: number): Promise<void> => {
    await api.put(`/admin/notifications/${id}/read`);
  },

  markAllAsRead: async (): Promise<void> => {
    await api.put('/admin/notifications/read-all');
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/admin/notifications/${id}`);
  },
};

export default api;
