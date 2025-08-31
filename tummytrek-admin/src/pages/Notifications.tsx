import React, { useState, useEffect } from 'react';
import { Bell, Mail, MessageSquare, AlertTriangle, RefreshCw, CheckCircle, Eye } from 'lucide-react';
import { notificationsAPI } from '../services/api';
import { Notification, NotificationType, NotificationPriority } from '../types';

const Notifications: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<string>('ALL');

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      console.log('Fetching notifications from API...');
      console.log('API Base URL:', process.env.REACT_APP_API_BASE_URL);
      console.log('Auth token exists:', !!localStorage.getItem('adminToken'));
      
      const data = await notificationsAPI.getAll();
      console.log('Notifications data received:', data);
      console.log('Number of notifications:', data.length);
      setNotifications(data);
    } catch (error: any) {
      console.error('Error fetching notifications:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);
      console.error('Error status:', error.response?.status);
      
      // Show empty state when API fails - no fallback static data
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationsAPI.markAsRead(id);
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === id 
            ? { ...notification, read: true }
            : notification
        )
      );
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationsAPI.markAllAsRead();
      setNotifications(prev => 
        prev.map(notification => ({ ...notification, read: true }))
      );
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const getNotificationIcon = (type: NotificationType) => {
    switch (type) {
      case NotificationType.ORDER:
        return Bell;
      case NotificationType.RESTAURANT:
        return Mail;
      case NotificationType.COMPLAINT:
        return AlertTriangle;
      case NotificationType.DELIVERY_PARTNER:
        return MessageSquare;
      default:
        return Bell;
    }
  };

  const getNotificationColor = (type: NotificationType) => {
    switch (type) {
      case NotificationType.ORDER:
        return 'blue';
      case NotificationType.RESTAURANT:
        return 'green';
      case NotificationType.COMPLAINT:
        return 'red';
      case NotificationType.DELIVERY_PARTNER:
        return 'purple';
      default:
        return 'gray';
    }
  };

  const getTimeAgo = (dateString: string) => {
    const now = new Date();
    const date = new Date(dateString);
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes} minutes ago`;
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours} hours ago`;
    
    const diffInDays = Math.floor(diffInHours / 24);
    return `${diffInDays} days ago`;
  };

  const filteredNotifications = notifications.filter(notification => {
    if (filter === 'ALL') return true;
    if (filter === 'UNREAD') return !notification.read;
    if (filter === 'READ') return notification.read;
    return notification.type === filter;
  });

  if (loading) {
    return (
      <div className="p-6">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded mb-6"></div>
          <div className="space-y-4">
            {[1, 2, 3, 4, 5].map((i) => (
              <div key={i} className="h-20 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
          <p className="text-gray-600">Stay updated with platform activities</p>
        </div>
        <div className="flex space-x-3">
          <button 
            onClick={handleMarkAllAsRead}
            className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-200 rounded-lg hover:bg-gray-50"
          >
            <CheckCircle className="h-4 w-4 mr-2" />
            Mark All Read
          </button>
          <button
            onClick={fetchNotifications}
            className="flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg hover:bg-blue-600"
          >
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Notification Filters */}
      <div className="bg-white rounded-lg shadow mb-6 p-4">
        <div className="flex flex-wrap gap-2">
          <button 
            onClick={() => setFilter('ALL')}
            className={`px-4 py-2 text-sm font-medium rounded-lg ${
              filter === 'ALL' 
                ? 'text-blue-600 bg-blue-50' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            All ({notifications.length})
          </button>
          <button 
            onClick={() => setFilter('UNREAD')}
            className={`px-4 py-2 text-sm font-medium rounded-lg ${
              filter === 'UNREAD' 
                ? 'text-blue-600 bg-blue-50' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            Unread ({notifications.filter(n => !n.read).length})
          </button>
          <button 
            onClick={() => setFilter(NotificationType.ORDER)}
            className={`px-4 py-2 text-sm font-medium rounded-lg ${
              filter === NotificationType.ORDER 
                ? 'text-blue-600 bg-blue-50' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            Orders ({notifications.filter(n => n.type === NotificationType.ORDER).length})
          </button>
          <button 
            onClick={() => setFilter(NotificationType.RESTAURANT)}
            className={`px-4 py-2 text-sm font-medium rounded-lg ${
              filter === NotificationType.RESTAURANT 
                ? 'text-blue-600 bg-blue-50' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            Restaurants ({notifications.filter(n => n.type === NotificationType.RESTAURANT).length})
          </button>
          <button 
            onClick={() => setFilter(NotificationType.COMPLAINT)}
            className={`px-4 py-2 text-sm font-medium rounded-lg ${
              filter === NotificationType.COMPLAINT 
                ? 'text-blue-600 bg-blue-50' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            Complaints ({notifications.filter(n => n.type === NotificationType.COMPLAINT).length})
          </button>
        </div>
      </div>

      {/* Notifications List */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        {filteredNotifications.length === 0 ? (
          <div className="text-center py-12">
            <Bell className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No notifications found</h3>
            <p className="text-gray-500">No notifications match your current filter.</p>
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {filteredNotifications.map((notification) => {
              const Icon = getNotificationIcon(notification.type);
              const color = getNotificationColor(notification.type);
              return (
                <div
                  key={notification.id}
                  className={`p-6 hover:bg-gray-50 transition-colors ${
                    !notification.read ? 'bg-blue-50/30' : ''
                  }`}
                >
                  <div className="flex items-start">
                    <div className={`p-2 rounded-lg bg-${color}-100 mr-4`}>
                      <Icon className={`w-5 h-5 text-${color}-600`} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between">
                        <h3 className={`text-sm font-medium ${
                          !notification.read ? 'text-gray-900' : 'text-gray-700'
                        }`}>
                          {notification.title}
                        </h3>
                        <div className="flex items-center">
                          <span className="text-xs text-gray-500 mr-2">
                            {getTimeAgo(notification.createdAt)}
                          </span>
                          {!notification.read && (
                            <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                          )}
                        </div>
                      </div>
                      <p className="mt-1 text-sm text-gray-600">
                        {notification.message}
                      </p>
                      <div className={`mt-1 inline-flex items-center px-2 py-1 rounded text-xs font-medium ${
                        notification.priority === NotificationPriority.HIGH || notification.priority === NotificationPriority.URGENT 
                          ? 'bg-red-100 text-red-800'
                          : notification.priority === NotificationPriority.MEDIUM
                          ? 'bg-yellow-100 text-yellow-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}>
                        {notification.priority}
                      </div>
                      <div className="mt-2 flex space-x-2">
                        <button 
                          className="text-xs text-blue-600 hover:text-blue-800"
                        >
                          View Details
                        </button>
                        {!notification.read && (
                          <button 
                            onClick={() => handleMarkAsRead(notification.id)}
                            className="text-xs text-gray-500 hover:text-gray-700"
                          >
                            Mark as Read
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default Notifications;
