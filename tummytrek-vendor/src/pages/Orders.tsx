import React, { useState, useEffect } from 'react';
import {
  ShoppingBag,
  Search,
  Filter,
  Eye,
  Check,
  X,
  Clock,
  ChefHat,
  Truck,
  MapPin,
  Phone,
  Calendar
} from 'lucide-react';
import { vendorAPI } from '../services/api';
import { Order, OrderStatus } from '../types';

const Orders: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [filteredOrders, setFilteredOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<OrderStatus | 'ALL'>('ALL');
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchOrders();
  }, [currentPage]);

  useEffect(() => {
    filterOrders();
  }, [orders, searchTerm, statusFilter]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await vendorAPI.getOrders(currentPage, 20);
      
      if (response.success) {
        setOrders(response.data.content);
        setTotalPages(response.data.totalPages);
      }
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterOrders = () => {
    let filtered = orders;

    if (searchTerm) {
      filtered = filtered.filter(order =>
        order.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.customer.phone.includes(searchTerm)
      );
    }

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(order => order.status === statusFilter);
    }

    setFilteredOrders(filtered);
  };

  const handleOrderAction = async (orderId: number, action: 'accept' | 'reject' | 'preparing' | 'ready') => {
    try {
      let response;
      
      switch (action) {
        case 'accept':
          response = await vendorAPI.acceptOrder(orderId, 30); // 30 minutes prep time
          break;
        case 'reject':
          const reason = window.prompt('Please provide a reason for rejection:');
          if (!reason) return;
          response = await vendorAPI.rejectOrder(orderId, reason);
          break;
        case 'preparing':
          response = await vendorAPI.markOrderPreparing(orderId);
          break;
        case 'ready':
          response = await vendorAPI.markOrderReady(orderId);
          break;
      }

      if (response?.success) {
        fetchOrders(); // Refresh orders
        setSelectedOrder(null);
      }
    } catch (error) {
      console.error(`Error ${action} order:`, error);
    }
  };

  const getStatusColor = (status: OrderStatus) => {
    switch (status) {
      case OrderStatus.PLACED:
        return 'bg-blue-100 text-blue-800';
      case OrderStatus.CONFIRMED:
        return 'bg-green-100 text-green-800';
      case OrderStatus.PREPARING:
        return 'bg-yellow-100 text-yellow-800';
      case OrderStatus.READY_FOR_PICKUP:
        return 'bg-purple-100 text-purple-800';
      case OrderStatus.PICKED_UP:
        return 'bg-indigo-100 text-indigo-800';
      case OrderStatus.OUT_FOR_DELIVERY:
        return 'bg-orange-100 text-orange-800';
      case OrderStatus.DELIVERED:
        return 'bg-green-100 text-green-800';
      case OrderStatus.CANCELLED:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: OrderStatus) => {
    switch (status) {
      case OrderStatus.PLACED:
        return <ShoppingBag className="h-4 w-4" />;
      case OrderStatus.CONFIRMED:
        return <Check className="h-4 w-4" />;
      case OrderStatus.PREPARING:
        return <ChefHat className="h-4 w-4" />;
      case OrderStatus.READY_FOR_PICKUP:
        return <Clock className="h-4 w-4" />;
      case OrderStatus.PICKED_UP:
      case OrderStatus.OUT_FOR_DELIVERY:
        return <Truck className="h-4 w-4" />;
      case OrderStatus.DELIVERED:
        return <Check className="h-4 w-4" />;
      case OrderStatus.CANCELLED:
        return <X className="h-4 w-4" />;
      default:
        return <ShoppingBag className="h-4 w-4" />;
    }
  };

  const canAcceptOrder = (status: OrderStatus) => status === OrderStatus.PLACED;
  const canRejectOrder = (status: OrderStatus) => [OrderStatus.PLACED, OrderStatus.CONFIRMED].includes(status);
  const canMarkPreparing = (status: OrderStatus) => status === OrderStatus.CONFIRMED;
  const canMarkReady = (status: OrderStatus) => status === OrderStatus.PREPARING;

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading orders...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Orders</h1>
          <p className="text-gray-600">Manage your restaurant orders</p>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search by order number, customer name, or phone..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Status Filter */}
          <div className="flex items-center space-x-2">
            <Filter className="h-4 w-4 text-gray-400" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as OrderStatus | 'ALL')}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="ALL">All Orders</option>
              {Object.values(OrderStatus).map(status => (
                <option key={status} value={status}>
                  {status.replace(/_/g, ' ')}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Orders List */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Order Details
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Customer
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Time
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredOrders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        #{order.orderNumber}
                      </div>
                      <div className="text-sm text-gray-500">
                        {order.orderItems.length} item(s)
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {order.customer.name}
                      </div>
                      <div className="text-sm text-gray-500 flex items-center">
                        <Phone className="h-3 w-3 mr-1" />
                        {order.customer.phone}
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(order.status)}`}>
                      {getStatusIcon(order.status)}
                      <span className="ml-1">{order.status.replace(/_/g, ' ')}</span>
                    </span>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    ₹{order.totalAmount.toFixed(2)}
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900 flex items-center">
                      <Calendar className="h-3 w-3 mr-1" />
                      {new Date(order.createdAt).toLocaleTimeString()}
                    </div>
                    <div className="text-xs text-gray-500">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => setSelectedOrder(order)}
                        className="text-blue-600 hover:text-blue-900 p-1"
                        title="View Details"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      
                      {canAcceptOrder(order.status) && (
                        <button
                          onClick={() => handleOrderAction(order.id, 'accept')}
                          className="text-green-600 hover:text-green-900 p-1"
                          title="Accept Order"
                        >
                          <Check className="h-4 w-4" />
                        </button>
                      )}
                      
                      {canRejectOrder(order.status) && (
                        <button
                          onClick={() => handleOrderAction(order.id, 'reject')}
                          className="text-red-600 hover:text-red-900 p-1"
                          title="Reject Order"
                        >
                          <X className="h-4 w-4" />
                        </button>
                      )}
                      
                      {canMarkPreparing(order.status) && (
                        <button
                          onClick={() => handleOrderAction(order.id, 'preparing')}
                          className="text-yellow-600 hover:text-yellow-900 p-1"
                          title="Mark as Preparing"
                        >
                          <ChefHat className="h-4 w-4" />
                        </button>
                      )}
                      
                      {canMarkReady(order.status) && (
                        <button
                          onClick={() => handleOrderAction(order.id, 'ready')}
                          className="text-purple-600 hover:text-purple-900 p-1"
                          title="Mark as Ready"
                        >
                          <Clock className="h-4 w-4" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {filteredOrders.length === 0 && (
          <div className="text-center py-12">
            <ShoppingBag className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No orders found</p>
          </div>
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between">
          <button
            onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Previous
          </button>
          
          <span className="text-sm text-gray-700">
            Page {currentPage + 1} of {totalPages}
          </span>
          
          <button
            onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage === totalPages - 1}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </div>
      )}

      {/* Order Details Modal */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-2xl w-full max-h-96 overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-gray-900">
                  Order #{selectedOrder.orderNumber}
                </h3>
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>

              <div className="space-y-6">
                {/* Customer Info */}
                <div>
                  <h4 className="text-sm font-medium text-gray-900 mb-2">Customer Information</h4>
                  <div className="bg-gray-50 p-4 rounded-lg space-y-2">
                    <p className="text-sm">
                      <span className="font-medium">Name:</span> {selectedOrder.customer.name}
                    </p>
                    <p className="text-sm">
                      <span className="font-medium">Phone:</span> {selectedOrder.customer.phone}
                    </p>
                    <p className="text-sm">
                      <span className="font-medium">Email:</span> {selectedOrder.customer.email}
                    </p>
                  </div>
                </div>

                {/* Delivery Address */}
                <div>
                  <h4 className="text-sm font-medium text-gray-900 mb-2">Delivery Address</h4>
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <p className="text-sm flex items-start">
                      <MapPin className="h-4 w-4 text-gray-400 mr-2 mt-0.5 flex-shrink-0" />
                      {selectedOrder.deliveryAddress}
                    </p>
                  </div>
                </div>

                {/* Order Items */}
                <div>
                  <h4 className="text-sm font-medium text-gray-900 mb-2">Order Items</h4>
                  <div className="bg-gray-50 p-4 rounded-lg space-y-3">
                    {selectedOrder.orderItems.map((item, index) => (
                      <div key={index} className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-900">
                            {item.menuItem.name}
                          </p>
                          <p className="text-xs text-gray-500">
                            Quantity: {item.quantity}
                          </p>
                          {item.customizations && (
                            <p className="text-xs text-gray-500">
                              Note: {item.customizations}
                            </p>
                          )}
                        </div>
                        <p className="text-sm font-medium text-gray-900">
                          ₹{(item.price * item.quantity).toFixed(2)}
                        </p>
                      </div>
                    ))}
                    
                    <div className="border-t pt-3 mt-3">
                      <div className="flex items-center justify-between font-medium">
                        <span>Total Amount:</span>
                        <span className="text-lg">₹{selectedOrder.totalAmount.toFixed(2)}</span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Special Instructions */}
                {selectedOrder.specialInstructions && (
                  <div>
                    <h4 className="text-sm font-medium text-gray-900 mb-2">Special Instructions</h4>
                    <div className="bg-yellow-50 p-4 rounded-lg">
                      <p className="text-sm text-gray-700">{selectedOrder.specialInstructions}</p>
                    </div>
                  </div>
                )}

                {/* Action Buttons */}
                <div className="flex items-center space-x-3 pt-4 border-t">
                  {canAcceptOrder(selectedOrder.status) && (
                    <button
                      onClick={() => handleOrderAction(selectedOrder.id, 'accept')}
                      className="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                    >
                      Accept Order
                    </button>
                  )}
                  
                  {canRejectOrder(selectedOrder.status) && (
                    <button
                      onClick={() => handleOrderAction(selectedOrder.id, 'reject')}
                      className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 transition-colors"
                    >
                      Reject Order
                    </button>
                  )}
                  
                  {canMarkPreparing(selectedOrder.status) && (
                    <button
                      onClick={() => handleOrderAction(selectedOrder.id, 'preparing')}
                      className="px-4 py-2 bg-yellow-600 text-white text-sm font-medium rounded-lg hover:bg-yellow-700 transition-colors"
                    >
                      Mark as Preparing
                    </button>
                  )}
                  
                  {canMarkReady(selectedOrder.status) && (
                    <button
                      onClick={() => handleOrderAction(selectedOrder.id, 'ready')}
                      className="px-4 py-2 bg-purple-600 text-white text-sm font-medium rounded-lg hover:bg-purple-700 transition-colors"
                    >
                      Mark as Ready
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Orders;
