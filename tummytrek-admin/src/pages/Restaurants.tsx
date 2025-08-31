import React, { useState, useEffect } from 'react';
import {
  Search,
  Filter,
  Eye,
  CheckCircle,
  XCircle,
  Clock,
  Star,
  MapPin,
  Phone,
  Mail,
  MoreVertical,
  Edit,
  Trash2,
} from 'lucide-react';
import { Restaurant, RestaurantStatus } from '../types';
import { restaurantsAPI } from '../services/api';
import toast from 'react-hot-toast';

const Restaurants: React.FC = () => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [filteredRestaurants, setFilteredRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedRestaurant, setSelectedRestaurant] = useState<Restaurant | null>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    fetchRestaurants();
  }, []);

  useEffect(() => {
    filterRestaurants();
  }, [restaurants, searchTerm, statusFilter]);

  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      const data = await restaurantsAPI.getAll();
      setRestaurants(data);
    } catch (error) {
      console.error('Error fetching restaurants:', error);
      toast.error('Failed to fetch restaurants');
    } finally {
      setLoading(false);
    }
  };

  const filterRestaurants = () => {
    let filtered = restaurants;

    if (searchTerm) {
      filtered = filtered.filter(restaurant =>
        restaurant.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        restaurant.cuisineType?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        restaurant.address.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(restaurant => restaurant.status === statusFilter);
    }

    setFilteredRestaurants(filtered);
  };

  const handleApprove = async (restaurantId: number) => {
    try {
      await restaurantsAPI.approve(restaurantId);
      toast.success('Restaurant approved successfully');
      fetchRestaurants();
    } catch (error) {
      console.error('Error approving restaurant:', error);
      toast.error('Failed to approve restaurant');
    }
  };

  const handleReject = async (restaurantId: number) => {
    const reason = prompt('Please provide a reason for rejection:');
    if (!reason) return;

    try {
      await restaurantsAPI.reject(restaurantId, reason);
      toast.success('Restaurant rejected');
      fetchRestaurants();
    } catch (error) {
      console.error('Error rejecting restaurant:', error);
      toast.error('Failed to reject restaurant');
    }
  };

  const handleToggleStatus = async (restaurantId: number) => {
    try {
      await restaurantsAPI.toggleStatus(restaurantId);
      toast.success('Restaurant status updated');
      fetchRestaurants();
    } catch (error) {
      console.error('Error updating restaurant status:', error);
      toast.error('Failed to update restaurant status');
    }
  };

  const getStatusColor = (status: RestaurantStatus) => {
    switch (status) {
      case RestaurantStatus.APPROVED:
        return 'bg-green-100 text-green-800';
      case RestaurantStatus.PENDING_APPROVAL:
        return 'bg-yellow-100 text-yellow-800';
      case RestaurantStatus.REJECTED:
        return 'bg-red-100 text-red-800';
      case RestaurantStatus.SUSPENDED:
        return 'bg-gray-100 text-gray-800';
      case RestaurantStatus.TEMPORARILY_CLOSED:
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: RestaurantStatus) => {
    switch (status) {
      case RestaurantStatus.APPROVED:
        return <CheckCircle className="w-4 h-4" />;
      case RestaurantStatus.PENDING_APPROVAL:
        return <Clock className="w-4 h-4" />;
      case RestaurantStatus.REJECTED:
        return <XCircle className="w-4 h-4" />;
      default:
        return <Clock className="w-4 h-4" />;
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Restaurants</h1>
          <p className="text-gray-600 mt-1">Manage restaurant registrations and approvals</p>
        </div>
        <div className="flex items-center space-x-3">
          <button
            onClick={fetchRestaurants}
            className="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0 sm:space-x-4">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search restaurants..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Status Filter */}
          <div className="flex items-center space-x-2">
            <Filter className="w-5 h-5 text-gray-400" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="ALL">All Status</option>
              <option value="PENDING_APPROVAL">Pending Approval</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="SUSPENDED">Suspended</option>
              <option value="TEMPORARILY_CLOSED">Temporarily Closed</option>
            </select>
          </div>
        </div>
      </div>

      {/* Restaurants Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Restaurant
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Rating
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Joined
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredRestaurants.map((restaurant) => (
                <tr key={restaurant.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-12 w-12">
                        <img
                          className="h-12 w-12 rounded-lg object-cover"
                          src={restaurant.imageUrl || '/api/placeholder/48/48'}
                          alt={restaurant.name}
                        />
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {restaurant.name}
                        </div>
                        <div className="text-sm text-gray-500 flex items-center">
                          <MapPin className="w-3 h-3 mr-1" />
                          {restaurant.address.substring(0, 50)}...
                        </div>
                        {restaurant.cuisineType && (
                          <div className="text-xs text-primary-600 mt-1">
                            {restaurant.cuisineType}
                          </div>
                        )}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                        restaurant.status
                      )}`}
                    >
                      {getStatusIcon(restaurant.status)}
                      <span className="ml-1">{restaurant.status.replace('_', ' ')}</span>
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <Star className="w-4 h-4 text-yellow-400 fill-current" />
                      <span className="ml-1 text-sm text-gray-900">
                        {restaurant.rating.toFixed(1)}
                      </span>
                      <span className="ml-1 text-xs text-gray-500">
                        ({restaurant.totalReviews})
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      {restaurant.phone && (
                        <div className="flex items-center text-sm text-gray-600">
                          <Phone className="w-3 h-3 mr-2" />
                          {restaurant.phone}
                        </div>
                      )}
                      {restaurant.email && (
                        <div className="flex items-center text-sm text-gray-600">
                          <Mail className="w-3 h-3 mr-2" />
                          {restaurant.email}
                        </div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(restaurant.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex items-center justify-end space-x-2">
                      <button
                        onClick={() => {
                          setSelectedRestaurant(restaurant);
                          setShowModal(true);
                        }}
                        className="text-primary-600 hover:text-primary-900 p-1 rounded"
                        title="View Details"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      
                      {restaurant.status === RestaurantStatus.PENDING_APPROVAL && (
                        <>
                          <button
                            onClick={() => handleApprove(restaurant.id)}
                            className="text-green-600 hover:text-green-900 p-1 rounded"
                            title="Approve"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleReject(restaurant.id)}
                            className="text-red-600 hover:text-red-900 p-1 rounded"
                            title="Reject"
                          >
                            <XCircle className="w-4 h-4" />
                          </button>
                        </>
                      )}
                      
                      {restaurant.status === RestaurantStatus.APPROVED && (
                        <button
                          onClick={() => handleToggleStatus(restaurant.id)}
                          className="text-yellow-600 hover:text-yellow-900 p-1 rounded"
                          title="Suspend"
                        >
                          <Clock className="w-4 h-4" />
                        </button>
                      )}
                      
                      <div className="relative group">
                        <button className="text-gray-400 hover:text-gray-600 p-1 rounded">
                          <MoreVertical className="w-4 h-4" />
                        </button>
                        <div className="absolute right-0 top-full mt-1 w-48 bg-white rounded-md shadow-lg border border-gray-200 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-10">
                          <div className="py-1">
                            <button className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                              <Edit className="w-4 h-4 mr-2" />
                              Edit
                            </button>
                            <button className="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50">
                              <Trash2 className="w-4 h-4 mr-2" />
                              Delete
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Restaurant Details Modal */}
      {showModal && selectedRestaurant && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Restaurant Details</h2>
                <button
                  onClick={() => setShowModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <XCircle className="w-6 h-6" />
                </button>
              </div>

              <div className="space-y-6">
                {/* Basic Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Name</label>
                    <p className="mt-1 text-sm text-gray-900">{selectedRestaurant.name}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Status</label>
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1 ${getStatusColor(
                        selectedRestaurant.status
                      )}`}
                    >
                      {getStatusIcon(selectedRestaurant.status)}
                      <span className="ml-1">{selectedRestaurant.status.replace('_', ' ')}</span>
                    </span>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Cuisine Type</label>
                    <p className="mt-1 text-sm text-gray-900">
                      {selectedRestaurant.cuisineType || 'Not specified'}
                    </p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Rating</label>
                    <div className="flex items-center mt-1">
                      <Star className="w-4 h-4 text-yellow-400 fill-current" />
                      <span className="ml-1 text-sm text-gray-900">
                        {selectedRestaurant.rating.toFixed(1)} ({selectedRestaurant.totalReviews} reviews)
                      </span>
                    </div>
                  </div>
                </div>

                {/* Contact Info */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Contact Information</label>
                  <div className="space-y-2">
                    <div className="flex items-center">
                      <Phone className="w-4 h-4 text-gray-400 mr-2" />
                      <span className="text-sm text-gray-900">{selectedRestaurant.phone || 'Not provided'}</span>
                    </div>
                    <div className="flex items-center">
                      <Mail className="w-4 h-4 text-gray-400 mr-2" />
                      <span className="text-sm text-gray-900">{selectedRestaurant.email || 'Not provided'}</span>
                    </div>
                    <div className="flex items-start">
                      <MapPin className="w-4 h-4 text-gray-400 mr-2 mt-0.5" />
                      <span className="text-sm text-gray-900">{selectedRestaurant.address}</span>
                    </div>
                  </div>
                </div>

                {/* Business Details */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Minimum Order</label>
                    <p className="mt-1 text-sm text-gray-900">${selectedRestaurant.minimumOrderAmount}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Delivery Fee</label>
                    <p className="mt-1 text-sm text-gray-900">${selectedRestaurant.deliveryFee}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Delivery Time</label>
                    <p className="mt-1 text-sm text-gray-900">
                      {selectedRestaurant.deliveryTimeMin}-{selectedRestaurant.deliveryTimeMax} min
                    </p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">FSSAI License</label>
                    <p className="mt-1 text-sm text-gray-900">
                      {selectedRestaurant.fssaiLicense || 'Not provided'}
                    </p>
                  </div>
                </div>

                {/* Description */}
                {selectedRestaurant.description && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Description</label>
                    <p className="mt-1 text-sm text-gray-900">{selectedRestaurant.description}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Restaurants;
