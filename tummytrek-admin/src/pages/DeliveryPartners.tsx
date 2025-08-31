import React, { useState, useEffect } from 'react';
import {
  Search,
  Filter,
  Eye,
  CheckCircle,
  XCircle,
  Clock,
  Truck,
  User,
  Phone,
  Mail,
  MapPin,
  Star,
  MoreVertical,
  Edit,
  Trash2,
} from 'lucide-react';
import { DeliveryPartner, DeliveryPartnerStatus } from '../types';
import { deliveryPartnersAPI } from '../services/api';
import toast from 'react-hot-toast';

const DeliveryPartners: React.FC = () => {
  const [deliveryPartners, setDeliveryPartners] = useState<DeliveryPartner[]>([]);
  const [filteredPartners, setFilteredPartners] = useState<DeliveryPartner[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedPartner, setSelectedPartner] = useState<DeliveryPartner | null>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    fetchDeliveryPartners();
  }, []);

  useEffect(() => {
    filterPartners();
  }, [deliveryPartners, searchTerm, statusFilter]);

  const fetchDeliveryPartners = async () => {
    try {
      setLoading(true);
      const data = await deliveryPartnersAPI.getAll();
      setDeliveryPartners(data);
    } catch (error) {
      console.error('Error fetching delivery partners:', error);
      toast.error('Failed to fetch delivery partners');
    } finally {
      setLoading(false);
    }
  };

  const filterPartners = () => {
    let filtered = deliveryPartners;

    if (searchTerm) {
      filtered = filtered.filter(partner =>
        partner.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        partner.user.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        partner.user.phone.includes(searchTerm) ||
        partner.vehicleNumber.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(partner => partner.status === statusFilter);
    }

    setFilteredPartners(filtered);
  };

  const handleApprove = async (partnerId: number) => {
    try {
      await deliveryPartnersAPI.approve(partnerId);
      toast.success('Delivery partner approved successfully');
      fetchDeliveryPartners();
    } catch (error) {
      console.error('Error approving delivery partner:', error);
      toast.error('Failed to approve delivery partner');
    }
  };

  const handleReject = async (partnerId: number) => {
    const reason = prompt('Please provide a reason for rejection:');
    if (!reason) return;

    try {
      await deliveryPartnersAPI.reject(partnerId, reason);
      toast.success('Delivery partner rejected');
      fetchDeliveryPartners();
    } catch (error) {
      console.error('Error rejecting delivery partner:', error);
      toast.error('Failed to reject delivery partner');
    }
  };

  const handleToggleStatus = async (partnerId: number) => {
    try {
      await deliveryPartnersAPI.toggleStatus(partnerId);
      toast.success('Delivery partner status updated');
      fetchDeliveryPartners();
    } catch (error) {
      console.error('Error updating delivery partner status:', error);
      toast.error('Failed to update delivery partner status');
    }
  };

  const getStatusColor = (status: DeliveryPartnerStatus) => {
    switch (status) {
      case DeliveryPartnerStatus.APPROVED:
        return 'bg-green-100 text-green-800';
      case DeliveryPartnerStatus.PENDING_APPROVAL:
        return 'bg-yellow-100 text-yellow-800';
      case DeliveryPartnerStatus.REJECTED:
        return 'bg-red-100 text-red-800';
      case DeliveryPartnerStatus.SUSPENDED:
        return 'bg-gray-100 text-gray-800';
      case DeliveryPartnerStatus.ON_DUTY:
        return 'bg-blue-100 text-blue-800';
      case DeliveryPartnerStatus.OFF_DUTY:
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: DeliveryPartnerStatus) => {
    switch (status) {
      case DeliveryPartnerStatus.APPROVED:
        return <CheckCircle className="w-4 h-4" />;
      case DeliveryPartnerStatus.PENDING_APPROVAL:
        return <Clock className="w-4 h-4" />;
      case DeliveryPartnerStatus.REJECTED:
        return <XCircle className="w-4 h-4" />;
      case DeliveryPartnerStatus.ON_DUTY:
        return <Truck className="w-4 h-4" />;
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
          <h1 className="text-3xl font-bold text-gray-900">Delivery Partners</h1>
          <p className="text-gray-600 mt-1">Manage delivery partner applications and performance</p>
        </div>
        <div className="flex items-center space-x-3">
          <button
            onClick={fetchDeliveryPartners}
            className="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center">
            <div className="p-3 bg-blue-100 rounded-xl">
              <Truck className="w-6 h-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-2xl font-bold text-gray-900">{deliveryPartners.length}</p>
              <p className="text-sm font-medium text-gray-600">Total Partners</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center">
            <div className="p-3 bg-green-100 rounded-xl">
              <CheckCircle className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-2xl font-bold text-gray-900">
                {deliveryPartners.filter(p => p.status === DeliveryPartnerStatus.APPROVED).length}
              </p>
              <p className="text-sm font-medium text-gray-600">Approved</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center">
            <div className="p-3 bg-yellow-100 rounded-xl">
              <Clock className="w-6 h-6 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-2xl font-bold text-gray-900">
                {deliveryPartners.filter(p => p.status === DeliveryPartnerStatus.PENDING_APPROVAL).length}
              </p>
              <p className="text-sm font-medium text-gray-600">Pending</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center">
            <div className="p-3 bg-orange-100 rounded-xl">
              <Truck className="w-6 h-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-2xl font-bold text-gray-900">
                {deliveryPartners.filter(p => p.isAvailable).length}
              </p>
              <p className="text-sm font-medium text-gray-600">Available</p>
            </div>
          </div>
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
              placeholder="Search delivery partners..."
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
              <option value="ON_DUTY">On Duty</option>
              <option value="OFF_DUTY">Off Duty</option>
            </select>
          </div>
        </div>
      </div>

      {/* Partners Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Partner
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vehicle
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Performance
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
              {filteredPartners.map((partner) => (
                <tr key={partner.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10">
                        <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                          <User className="w-5 h-5 text-blue-600" />
                        </div>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {partner.user.name}
                        </div>
                        <div className="text-sm text-gray-500">
                          ID: {partner.id}
                        </div>
                        {partner.isAvailable && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800 mt-1">
                            Available
                          </span>
                        )}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      <div className="flex items-center text-sm text-gray-600">
                        <Phone className="w-3 h-3 mr-2" />
                        {partner.user.phone}
                      </div>
                      {partner.user.email && (
                        <div className="flex items-center text-sm text-gray-600">
                          <Mail className="w-3 h-3 mr-2" />
                          {partner.user.email}
                        </div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      <div className="text-sm font-medium text-gray-900">
                        {partner.vehicleType}
                      </div>
                      <div className="text-sm text-gray-500">
                        {partner.vehicleNumber}
                      </div>
                      <div className="text-xs text-gray-400">
                        License: {partner.licenseNumber}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                        partner.status
                      )}`}
                    >
                      {getStatusIcon(partner.status)}
                      <span className="ml-1">{partner.status.replace('_', ' ')}</span>
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      <div className="flex items-center">
                        <Star className="w-4 h-4 text-yellow-400 fill-current mr-1" />
                        <span className="text-sm text-gray-900">
                          {partner.rating.toFixed(1)}
                        </span>
                        <span className="text-xs text-gray-500 ml-1">
                          ({partner.totalReviews})
                        </span>
                      </div>
                      <div className="text-sm text-gray-600">
                        {partner.totalDeliveries} deliveries
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(partner.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex items-center justify-end space-x-2">
                      <button
                        onClick={() => {
                          setSelectedPartner(partner);
                          setShowModal(true);
                        }}
                        className="text-primary-600 hover:text-primary-900 p-1 rounded"
                        title="View Details"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      
                      {partner.status === DeliveryPartnerStatus.PENDING_APPROVAL && (
                        <>
                          <button
                            onClick={() => handleApprove(partner.id)}
                            className="text-green-600 hover:text-green-900 p-1 rounded"
                            title="Approve"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleReject(partner.id)}
                            className="text-red-600 hover:text-red-900 p-1 rounded"
                            title="Reject"
                          >
                            <XCircle className="w-4 h-4" />
                          </button>
                        </>
                      )}
                      
                      {partner.status === DeliveryPartnerStatus.APPROVED && (
                        <button
                          onClick={() => handleToggleStatus(partner.id)}
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
                              Edit Profile
                            </button>
                            <button className="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50">
                              <Trash2 className="w-4 h-4 mr-2" />
                              Delete Account
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

      {/* Partner Details Modal */}
      {showModal && selectedPartner && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Delivery Partner Details</h2>
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
                    <p className="mt-1 text-sm text-gray-900">{selectedPartner.user.name}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Status</label>
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1 ${getStatusColor(
                        selectedPartner.status
                      )}`}
                    >
                      {getStatusIcon(selectedPartner.status)}
                      <span className="ml-1">{selectedPartner.status.replace('_', ' ')}</span>
                    </span>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Phone</label>
                    <p className="mt-1 text-sm text-gray-900">{selectedPartner.user.phone}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Email</label>
                    <p className="mt-1 text-sm text-gray-900">
                      {selectedPartner.user.email || 'Not provided'}
                    </p>
                  </div>
                </div>

                {/* Vehicle Information */}
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-3">Vehicle Information</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Vehicle Type</label>
                      <p className="mt-1 text-sm text-gray-900">{selectedPartner.vehicleType}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Vehicle Number</label>
                      <p className="mt-1 text-sm text-gray-900">{selectedPartner.vehicleNumber}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">License Number</label>
                      <p className="mt-1 text-sm text-gray-900">{selectedPartner.licenseNumber}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Availability</label>
                      <span
                        className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium mt-1 ${
                          selectedPartner.isAvailable 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {selectedPartner.isAvailable ? 'Available' : 'Unavailable'}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Performance Metrics */}
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-3">Performance</h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="text-center p-4 bg-blue-50 rounded-lg">
                      <p className="text-2xl font-bold text-blue-600">{selectedPartner.totalDeliveries}</p>
                      <p className="text-sm text-blue-600">Total Deliveries</p>
                    </div>
                    <div className="text-center p-4 bg-yellow-50 rounded-lg">
                      <p className="text-2xl font-bold text-yellow-600">{selectedPartner.rating.toFixed(1)}</p>
                      <p className="text-sm text-yellow-600">Average Rating</p>
                    </div>
                    <div className="text-center p-4 bg-green-50 rounded-lg">
                      <p className="text-2xl font-bold text-green-600">{selectedPartner.totalReviews}</p>
                      <p className="text-sm text-green-600">Total Reviews</p>
                    </div>
                  </div>
                </div>

                {/* Location Information */}
                {(selectedPartner.currentLatitude && selectedPartner.currentLongitude) && (
                  <div>
                    <h3 className="text-lg font-medium text-gray-900 mb-3">Current Location</h3>
                    <div className="flex items-center text-sm text-gray-600">
                      <MapPin className="w-4 h-4 mr-2" />
                      <span>
                        Lat: {selectedPartner.currentLatitude.toFixed(6)}, 
                        Lng: {selectedPartner.currentLongitude.toFixed(6)}
                      </span>
                    </div>
                  </div>
                )}

                {/* Account Information */}
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-3">Account Information</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">User Status</label>
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 mt-1">
                        {selectedPartner.user.status}
                      </span>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Joined Date</label>
                      <p className="mt-1 text-sm text-gray-900">
                        {new Date(selectedPartner.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Email Verified</label>
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1 bg-green-100 text-green-800">
                        {selectedPartner.user.emailVerified ? 'Verified' : 'Not Verified'}
                      </span>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">Phone Verified</label>
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1 bg-green-100 text-green-800">
                        {selectedPartner.user.phoneVerified ? 'Verified' : 'Not Verified'}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DeliveryPartners;
