import React, { useState, useEffect } from 'react';
import {
  ShoppingCart,
  DollarSign,
  Users,
  Store,
  Truck,
  TrendingUp,
  TrendingDown,
  Clock,
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
import { dashboardAPI } from '../services/api';
import { DashboardStats, RevenueData, OrderStats } from '../types';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [revenueData, setRevenueData] = useState<RevenueData[]>([]);
  const [orderStats, setOrderStats] = useState<OrderStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [statsData, revenueChartData, orderStatsData] = await Promise.all([
        dashboardAPI.getStats(),
        dashboardAPI.getRevenueData('week'),
        dashboardAPI.getOrderStats(),
      ]);

      setStats(statsData);
      setRevenueData(revenueChartData);
      setOrderStats(orderStatsData);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      title: 'Total Revenue',
      value: stats?.totalRevenue ? `$${stats.totalRevenue.toLocaleString()}` : '$0',
      change: '+12.5%',
      changeType: 'positive' as const,
      icon: DollarSign,
      color: 'green',
    },
    {
      title: 'Today\'s Revenue',
      value: stats?.todayRevenue ? `$${stats.todayRevenue.toLocaleString()}` : '$0',
      change: '+8.2%',
      changeType: 'positive' as const,
      icon: TrendingUp,
      color: 'blue',
    },
    {
      title: 'Total Orders',
      value: stats?.totalOrders?.toLocaleString() || '0',
      change: '+15.3%',
      changeType: 'positive' as const,
      icon: ShoppingCart,
      color: 'purple',
    },
    {
      title: 'Today\'s Orders',
      value: stats?.todayOrders?.toLocaleString() || '0',
      change: '+5.7%',
      changeType: 'positive' as const,
      icon: Clock,
      color: 'orange',
    },
    {
      title: 'Total Customers',
      value: stats?.totalCustomers?.toLocaleString() || '0',
      change: '+6.8%',
      changeType: 'positive' as const,
      icon: Users,
      color: 'pink',
    },
    {
      title: 'Total Restaurants',
      value: stats?.totalRestaurants?.toLocaleString() || '0',
      change: '+2.1%',
      changeType: 'positive' as const,
      icon: Store,
      color: 'indigo',
    },
    {
      title: 'Delivery Partners',
      value: stats?.totalDeliveryPartners?.toLocaleString() || '0',
      change: '+4.3%',
      changeType: 'positive' as const,
      icon: Truck,
      color: 'teal',
    },
    {
      title: 'Pending Approvals',
      value: stats?.pendingApprovals?.toLocaleString() || '0',
      change: '-2.5%',
      changeType: 'negative' as const,
      icon: Clock,
      color: 'red',
    },
  ];

  const orderStatusData = orderStats ? [
    { name: 'Placed', value: orderStats.placed, color: '#3B82F6' },
    { name: 'Confirmed', value: orderStats.confirmed, color: '#10B981' },
    { name: 'Preparing', value: orderStats.preparing, color: '#F59E0B' },
    { name: 'Ready', value: orderStats.readyForPickup, color: '#8B5CF6' },
    { name: 'Picked Up', value: orderStats.pickedUp, color: '#06B6D4' },
    { name: 'Out for Delivery', value: orderStats.outForDelivery, color: '#F97316' },
    { name: 'Delivered', value: orderStats.delivered, color: '#22C55E' },
    { name: 'Cancelled', value: orderStats.cancelled, color: '#EF4444' },
  ] : [];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">Overview of your TummyTrek platform</p>
        </div>
        <button
          onClick={fetchDashboardData}
          className="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors"
        >
          Refresh Data
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((card, index) => {
          const Icon = card.icon;
          return (
            <div key={index} className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{card.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-2">{card.value}</p>
                  <div className="flex items-center mt-2">
                    {card.changeType === 'positive' ? (
                      <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
                    ) : (
                      <TrendingDown className="w-4 h-4 text-red-500 mr-1" />
                    )}
                    <span
                      className={`text-sm font-medium ${
                        card.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                      }`}
                    >
                      {card.change}
                    </span>
                    <span className="text-sm text-gray-500 ml-1">vs last period</span>
                  </div>
                </div>
                <div className={`p-3 bg-${card.color}-100 rounded-xl`}>
                  <Icon className={`w-6 h-6 text-${card.color}-600`} />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Revenue Trend</h3>
            <select className="px-3 py-1 border border-gray-200 rounded-lg text-sm">
              <option value="week">Last 7 days</option>
              <option value="month">Last 30 days</option>
              <option value="year">Last 12 months</option>
            </select>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="date" 
                tickFormatter={(value) => new Date(value).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
              />
              <YAxis />
              <Tooltip 
                formatter={(value: number) => [`$${value.toLocaleString()}`, 'Revenue']}
                labelFormatter={(value) => new Date(value).toLocaleDateString()}
              />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="revenue" 
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
                outerRadius={80}
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

      {/* Recent Activity */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100">
        <div className="p-6 border-b border-gray-100">
          <h3 className="text-lg font-semibold text-gray-900">Recent Activity</h3>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            <div className="flex items-center p-4 bg-blue-50 rounded-lg">
              <div className="w-2 h-2 bg-blue-500 rounded-full mr-3"></div>
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">
                  New restaurant application received from "Spice Garden"
                </p>
                <p className="text-xs text-gray-500">2 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center p-4 bg-green-50 rounded-lg">
              <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">
                  Order #TT-2024-1234 has been successfully delivered
                </p>
                <p className="text-xs text-gray-500">5 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center p-4 bg-yellow-50 rounded-lg">
              <div className="w-2 h-2 bg-yellow-500 rounded-full mr-3"></div>
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">
                  Payment issue reported for order #TT-2024-1235
                </p>
                <p className="text-xs text-gray-500">10 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center p-4 bg-purple-50 rounded-lg">
              <div className="w-2 h-2 bg-purple-500 rounded-full mr-3"></div>
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">
                  New delivery partner "John Smith" has joined the platform
                </p>
                <p className="text-xs text-gray-500">15 minutes ago</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
