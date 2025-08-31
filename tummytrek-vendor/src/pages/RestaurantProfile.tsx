import React, { useState, useEffect } from 'react';
import {
  Store,
  Clock,
  Phone,
  Mail,
  MapPin,
  Star,
  Edit3,
  Save,
  X,
  Camera,
  Globe,
  Users,
  Calendar,
  Award,
  Settings
} from 'lucide-react';
import { vendorAPI } from '../services/api';
import { Restaurant, BusinessHours } from '../types';

const RestaurantProfile: React.FC = () => {
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    address: '',
    phone: '',
    email: '',
    website: '',
    cuisine: '',
    priceRange: '',
    maxDeliveryDistance: '',
    deliveryFee: '',
    minimumOrderAmount: '',
    imageUrl: ''
  });
  const [businessHours, setBusinessHours] = useState<BusinessHours[]>([]);

  useEffect(() => {
    fetchRestaurantProfile();
  }, []);

  const fetchRestaurantProfile = async () => {
    try {
      setLoading(true);
      const response = await vendorAPI.getRestaurantProfile();
      
      if (response.success) {
        const restaurantData = response.data;
        setRestaurant(restaurantData);
        
        setFormData({
          name: restaurantData.name,
          description: restaurantData.description || '',
          address: restaurantData.address,
          phone: restaurantData.phone,
          email: restaurantData.email || '',
          website: restaurantData.website || '',
          cuisine: restaurantData.cuisine,
          priceRange: restaurantData.priceRange || '',
          maxDeliveryDistance: restaurantData.maxDeliveryDistance?.toString() || '',
          deliveryFee: restaurantData.deliveryFee?.toString() || '',
          minimumOrderAmount: restaurantData.minimumOrderAmount?.toString() || '',
          imageUrl: restaurantData.imageUrl || ''
        });
        
        setBusinessHours(restaurantData.businessHours || []);
      }
    } catch (error) {
      console.error('Error fetching restaurant profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const updateData = {
        ...formData,
        maxDeliveryDistance: formData.maxDeliveryDistance ? parseFloat(formData.maxDeliveryDistance) : null,
        deliveryFee: formData.deliveryFee ? parseFloat(formData.deliveryFee) : null,
        minimumOrderAmount: formData.minimumOrderAmount ? parseFloat(formData.minimumOrderAmount) : null,
        businessHours
      };

      const response = await vendorAPI.updateRestaurantProfile(updateData);
      
      if (response.success) {
        fetchRestaurantProfile();
        setEditing(false);
      }
    } catch (error) {
      console.error('Error updating restaurant profile:', error);
    }
  };

  const handleBusinessHoursChange = (dayOfWeek: number, field: 'openTime' | 'closeTime' | 'isOpen', value: string | boolean) => {
    setBusinessHours(prev => {
      const existing = prev.find(bh => bh.dayOfWeek === dayOfWeek);
      
      if (existing) {
        return prev.map(bh => 
          bh.dayOfWeek === dayOfWeek 
            ? { ...bh, [field]: value }
            : bh
        );
      } else {
        return [
          ...prev,
          {
            dayOfWeek,
            openTime: field === 'openTime' ? value as string : '09:00',
            closeTime: field === 'closeTime' ? value as string : '22:00',
            isOpen: field === 'isOpen' ? value as boolean : true
          }
        ];
      }
    });
  };

  const getBusinessHoursForDay = (dayOfWeek: number) => {
    return businessHours.find(bh => bh.dayOfWeek === dayOfWeek) || {
      dayOfWeek,
      openTime: '09:00',
      closeTime: '22:00',
      isOpen: false
    };
  };

  const dayNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Create preview URL
      const previewUrl = URL.createObjectURL(file);
      setFormData(prev => ({ ...prev, imageUrl: previewUrl }));
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading restaurant profile...</p>
        </div>
      </div>
    );
  }

  if (!restaurant) {
    return (
      <div className="text-center py-12">
        <Store className="h-12 w-12 text-gray-400 mx-auto mb-4" />
        <p className="text-gray-600">Restaurant profile not found</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Restaurant Profile</h1>
          <p className="text-gray-600">Manage your restaurant information and settings</p>
        </div>
        {!editing && (
          <button
            onClick={() => setEditing(true)}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
          >
            <Edit3 className="h-4 w-4" />
            <span>Edit Profile</span>
          </button>
        )}
      </div>

      {editing ? (
        // Edit Form
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Basic Information */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Restaurant Name *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  placeholder="Describe your restaurant, cuisine style, and specialties..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cuisine Type *
                </label>
                <input
                  type="text"
                  value={formData.cuisine}
                  onChange={(e) => setFormData(prev => ({ ...prev, cuisine: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  placeholder="e.g., Indian, Chinese, Italian"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Price Range
                </label>
                <select
                  value={formData.priceRange}
                  onChange={(e) => setFormData(prev => ({ ...prev, priceRange: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Select Price Range</option>
                  <option value="₹">₹ - Budget Friendly (Under ₹200)</option>
                  <option value="₹₹">₹₹ - Moderate (₹200-₹500)</option>
                  <option value="₹₹₹">₹₹₹ - Expensive (₹500-₹1000)</option>
                  <option value="₹₹₹₹">₹₹₹₹ - Fine Dining (Above ₹1000)</option>
                </select>
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Contact Information</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Address *
                </label>
                <textarea
                  value={formData.address}
                  onChange={(e) => setFormData(prev => ({ ...prev, address: e.target.value }))}
                  rows={2}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Phone Number *
                </label>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData(prev => ({ ...prev, phone: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email Address
                </label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData(prev => ({ ...prev, email: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Website
                </label>
                <input
                  type="url"
                  value={formData.website}
                  onChange={(e) => setFormData(prev => ({ ...prev, website: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  placeholder="https://your-website.com"
                />
              </div>
            </div>
          </div>

          {/* Delivery Settings */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Delivery Settings</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Max Delivery Distance (km)
                </label>
                <input
                  type="number"
                  step="0.1"
                  value={formData.maxDeliveryDistance}
                  onChange={(e) => setFormData(prev => ({ ...prev, maxDeliveryDistance: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Delivery Fee (₹)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.deliveryFee}
                  onChange={(e) => setFormData(prev => ({ ...prev, deliveryFee: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Minimum Order Amount (₹)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.minimumOrderAmount}
                  onChange={(e) => setFormData(prev => ({ ...prev, minimumOrderAmount: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
            </div>
          </div>

          {/* Business Hours */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Business Hours</h2>
            
            <div className="space-y-4">
              {dayNames.map((day, index) => {
                const dayHours = getBusinessHoursForDay(index + 1);
                return (
                  <div key={day} className="flex items-center space-x-4">
                    <div className="w-24 text-sm font-medium text-gray-700">
                      {day}
                    </div>
                    
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={dayHours.isOpen}
                        onChange={(e) => handleBusinessHoursChange(index + 1, 'isOpen', e.target.checked)}
                        className="mr-2"
                      />
                      <span className="text-sm">Open</span>
                    </label>
                    
                    {dayHours.isOpen && (
                      <>
                        <input
                          type="time"
                          value={dayHours.openTime}
                          onChange={(e) => handleBusinessHoursChange(index + 1, 'openTime', e.target.value)}
                          className="px-2 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                        />
                        <span className="text-gray-500">to</span>
                        <input
                          type="time"
                          value={dayHours.closeTime}
                          onChange={(e) => handleBusinessHoursChange(index + 1, 'closeTime', e.target.value)}
                          className="px-2 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                        />
                      </>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Restaurant Image */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Restaurant Image</h2>
            
            <div className="flex items-center space-x-6">
              <div className="relative">
                {formData.imageUrl ? (
                  <img
                    src={formData.imageUrl}
                    alt="Restaurant"
                    className="w-32 h-32 object-cover rounded-lg"
                  />
                ) : (
                  <div className="w-32 h-32 bg-gray-200 rounded-lg flex items-center justify-center">
                    <Store className="h-8 w-8 text-gray-400" />
                  </div>
                )}
                
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageUpload}
                  className="hidden"
                  id="restaurant-image"
                />
                <label
                  htmlFor="restaurant-image"
                  className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center rounded-lg opacity-0 hover:opacity-100 transition-opacity cursor-pointer"
                >
                  <Camera className="h-6 w-6 text-white" />
                </label>
              </div>
              
              <div>
                <p className="text-sm text-gray-600">
                  Upload a high-quality image of your restaurant. This will be shown to customers.
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Recommended: 800x600 pixels, JPEG or PNG format, max 5MB
                </p>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center space-x-3">
            <button
              type="submit"
              className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
            >
              <Save className="h-4 w-4" />
              <span>Save Changes</span>
            </button>
            <button
              type="button"
              onClick={() => setEditing(false)}
              className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
          </div>
        </form>
      ) : (
        // View Mode
        <div className="space-y-6">
          {/* Restaurant Overview */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="relative h-48 bg-gradient-to-r from-primary-500 to-primary-600">
              {restaurant.imageUrl && (
                <img
                  src={restaurant.imageUrl}
                  alt={restaurant.name}
                  className="w-full h-full object-cover"
                />
              )}
              <div className="absolute inset-0 bg-black bg-opacity-30"></div>
              <div className="absolute bottom-4 left-6 text-white">
                <h1 className="text-2xl font-bold">{restaurant.name}</h1>
                <div className="flex items-center space-x-4 mt-2">
                  <div className="flex items-center space-x-1">
                    <Star className="h-4 w-4 text-yellow-400 fill-current" />
                    <span>{restaurant.rating?.toFixed(1) || 'New'}</span>
                  </div>
                  <span className="text-primary-100">•</span>
                  <span>{restaurant.cuisine}</span>
                  {restaurant.priceRange && (
                    <>
                      <span className="text-primary-100">•</span>
                      <span>{restaurant.priceRange}</span>
                    </>
                  )}
                </div>
              </div>
            </div>

            <div className="p-6">
              {restaurant.description && (
                <p className="text-gray-600 mb-4">{restaurant.description}</p>
              )}
              
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <div className="flex items-center space-x-3">
                  <MapPin className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-700">{restaurant.address}</span>
                </div>
                
                <div className="flex items-center space-x-3">
                  <Phone className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-700">{restaurant.phone}</span>
                </div>
                
                {restaurant.email && (
                  <div className="flex items-center space-x-3">
                    <Mail className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">{restaurant.email}</span>
                  </div>
                )}
                
                {restaurant.website && (
                  <div className="flex items-center space-x-3">
                    <Globe className="h-5 w-5 text-gray-400" />
                    <a 
                      href={restaurant.website}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-primary-600 hover:text-primary-700"
                    >
                      Website
                    </a>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Restaurant Stats */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center space-x-3">
                <div className="bg-green-100 p-3 rounded-lg">
                  <Star className="h-6 w-6 text-green-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900">
                    {restaurant.rating?.toFixed(1) || 'N/A'}
                  </p>
                  <p className="text-sm text-gray-600">Rating</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center space-x-3">
                <div className="bg-blue-100 p-3 rounded-lg">
                  <Users className="h-6 w-6 text-blue-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900">
                    {restaurant.totalOrders || 0}
                  </p>
                  <p className="text-sm text-gray-600">Total Orders</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center space-x-3">
                <div className="bg-purple-100 p-3 rounded-lg">
                  <Calendar className="h-6 w-6 text-purple-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900">
                    {restaurant.createdAt ? new Date(restaurant.createdAt).getFullYear() : 'N/A'}
                  </p>
                  <p className="text-sm text-gray-600">Since</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center space-x-3">
                <div className="bg-yellow-100 p-3 rounded-lg">
                  <Award className="h-6 w-6 text-yellow-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900">
                    {restaurant.isActive ? 'Active' : 'Inactive'}
                  </p>
                  <p className="text-sm text-gray-600">Status</p>
                </div>
              </div>
            </div>
          </div>

          {/* Business Hours */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Clock className="h-5 w-5 mr-2" />
              Business Hours
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {dayNames.map((day, index) => {
                const dayHours = businessHours.find(bh => bh.dayOfWeek === index + 1);
                return (
                  <div key={day} className="flex items-center justify-between py-2">
                    <span className="font-medium text-gray-700">{day}</span>
                    {dayHours?.isOpen ? (
                      <span className="text-green-600">
                        {dayHours.openTime} - {dayHours.closeTime}
                      </span>
                    ) : (
                      <span className="text-red-600">Closed</span>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Delivery Settings */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Settings className="h-5 w-5 mr-2" />
              Delivery Settings
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <p className="text-sm text-gray-500">Max Delivery Distance</p>
                <p className="text-lg font-semibold text-gray-900">
                  {restaurant.maxDeliveryDistance ? `${restaurant.maxDeliveryDistance} km` : 'Not set'}
                </p>
              </div>
              
              <div>
                <p className="text-sm text-gray-500">Delivery Fee</p>
                <p className="text-lg font-semibold text-gray-900">
                  {restaurant.deliveryFee ? `₹${restaurant.deliveryFee}` : 'Free'}
                </p>
              </div>
              
              <div>
                <p className="text-sm text-gray-500">Minimum Order Amount</p>
                <p className="text-lg font-semibold text-gray-900">
                  {restaurant.minimumOrderAmount ? `₹${restaurant.minimumOrderAmount}` : 'No minimum'}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RestaurantProfile;
