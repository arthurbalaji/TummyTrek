import React, { useState, useEffect } from 'react';
import {
  Store,
  DollarSign,
  ShoppingBag,
  Clock,
  TrendingUp,
  TrendingDown,
  Users,
  Star,
  AlertTriangle
} from 'lucide-react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { vendorAPI } from '../services/api';
import { VendorDashboard, VendorEarnings, VendorOrderStats } from '../types';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<VendorDashboard | null>(null);
  const [earningsData, setEarningsData] = useState<VendorEarnings[]>([]);
  const [orderStats, setOrderStats] = useState<VendorOrderStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Test authentication first
      console.log('Testing vendor authentication...');
      const testResponse = await vendorAPI.test();
      console.log('Test response:', testResponse);
      
      const [dashboardResponse, earningsResponse, orderStatsResponse] = await Promise.all([
        vendorAPI.getDashboardStats(),
        vendorAPI.getEarningsData('week', 7),
        vendorAPI.getOrderStats(),
      ]);

      if (dashboardResponse.success) {
        setStats(dashboardResponse.data);
      }

      if (earningsResponse.success) {
        setEarningsData(earningsResponse.data);
      }

      if (orderStatsResponse.success) {
        setOrderStats(orderStatsResponse.data);
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (!stats) {
    return (
      <div className="text-center py-12">
        <AlertTriangle className="h-12 w-12 text-yellow-500 mx-auto mb-4" />
        <p className="text-gray-600">Unable to load dashboard data</p>
      </div>
    );
  }

  const statCards = [
    {
      title: 'Today\'s Earnings',
      value: `₹${stats.todayEarnings.toLocaleString()}`,
      change: '+12.5%',
      changeType: 'positive' as const,
      icon: DollarSign,
      color: 'green',
    },
    {
      title: 'Today\'s Orders',
      value: stats.todayOrders.toLocaleString(),
      change: '+8.2%',
      changeType: 'positive' as const,
      icon: ShoppingBag,
      color: 'blue',
    },
    {
      title: 'Pending Orders',
      value: stats.pendingOrders.toLocaleString(),
      change: stats.pendingOrders > 5 ? 'High' : 'Normal',
      changeType: stats.pendingOrders > 5 ? 'negative' as const : 'positive' as const,
      icon: Clock,
      color: stats.pendingOrders > 5 ? 'red' : 'orange',
    },
    {
      title: 'Rating',
      value: stats.averageRating.toFixed(1),
      change: '4.8',
      changeType: 'positive' as const,
      icon: Star,
      color: 'yellow',
    },
    {
      title: 'Total Orders',
      value: stats.totalOrders.toLocaleString(),
      change: '+15.3%',
      changeType: 'positive' as const,
      icon: ShoppingBag,
      color: 'purple',
    },
    {
      title: 'Total Earnings',
      value: `₹${stats.totalEarnings.toLocaleString()}`,
      change: '+18.1%',
      changeType: 'positive' as const,
      icon: TrendingUp,
      color: 'indigo',
    },
  ];

  const orderStatusData = orderStats ? [
    { name: 'Placed', value: orderStats.placed, color: '#3B82F6' },
    { name: 'Confirmed', value: orderStats.confirmed, color: '#10B981' },
    { name: 'Preparing', value: orderStats.preparing, color: '#F59E0B' },
    { name: 'Ready', value: orderStats.ready, color: '#8B5CF6' },
    { name: 'Picked Up', value: orderStats.pickedUp, color: '#06B6D4' },
    { name: 'Delivered', value: orderStats.delivered, color: '#84CC16' },
    { name: 'Cancelled', value: orderStats.cancelled, color: '#EF4444' },
  ] : [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">Welcome back to {stats.restaurantName}</p>
        </div>
        
        <div className="flex items-center space-x-3">
          <div className={`px-3 py-2 rounded-full text-sm font-medium ${
            stats.isOpen 
              ? 'bg-green-100 text-green-800' 
              : 'bg-red-100 text-red-800'
          }`}>
            {stats.isOpen ? 'Open' : 'Closed'}
          </div>
          <div className={`px-3 py-2 rounded-full text-sm font-medium ${
            stats.isAcceptingOrders 
              ? 'bg-blue-100 text-blue-800' 
              : 'bg-gray-100 text-gray-800'
          }`}>
            {stats.isAcceptingOrders ? 'Accepting Orders' : 'Not Accepting'}
          </div>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {statCards.map((card, index) => {
          const Icon = card.icon;
          return (
            <div key={index} className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{card.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-1">{card.value}</p>
                </div>
                <div className={`h-12 w-12 rounded-lg flex items-center justify-center bg-${card.color}-100`}>
                  <Icon className={`h-6 w-6 text-${card.color}-600`} />
                </div>
              </div>
              <div className="flex items-center mt-4">
                {card.changeType === 'positive' ? (
                  <TrendingUp className="h-4 w-4 text-green-500 mr-1" />
                ) : (
                  <TrendingDown className="h-4 w-4 text-red-500 mr-1" />
                )}
                <span className={`text-sm ${
                  card.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {card.change}
                </span>
                <span className="text-sm text-gray-500 ml-1">from yesterday</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Earnings Chart */}
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 className="text-lg font-semibold text-gray-900 mb-6">Weekly Earnings</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={earningsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip formatter={(value: any) => [`₹${value}`, 'Earnings']} />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="earnings" 
                stroke="#3B82F6" 
                strokeWidth={3}
                dot={{ fill: '#3B82F6', strokeWidth: 2, r: 4 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Order Status Distribution */}
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <h3 className="text-lg font-semibold text-gray-900 mb-6">Order Status Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={orderStatusData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${((percent || 0) * 100).toFixed(0)}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {orderStatusData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <button className="p-4 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors">
            <Store className="h-6 w-6 text-blue-600 mb-2" />
            <p className="text-sm font-medium text-blue-900">Manage Menu</p>
          </button>
          <button className="p-4 bg-green-50 hover:bg-green-100 rounded-lg transition-colors">
            <ShoppingBag className="h-6 w-6 text-green-600 mb-2" />
            <p className="text-sm font-medium text-green-900">View Orders</p>
          </button>
          <button className="p-4 bg-orange-50 hover:bg-orange-100 rounded-lg transition-colors">
            <Clock className="h-6 w-6 text-orange-600 mb-2" />
            <p className="text-sm font-medium text-orange-900">Pending Orders</p>
          </button>
          <button className="p-4 bg-purple-50 hover:bg-purple-100 rounded-lg transition-colors">
            <DollarSign className="h-6 w-6 text-purple-600 mb-2" />
            <p className="text-sm font-medium text-purple-900">View Earnings</p>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
