import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Restaurants from './pages/Restaurants';
import Customers from './pages/Customers';
import Orders from './pages/Orders';
import DeliveryPartners from './pages/DeliveryPartners';
import Analytics from './pages/Analytics';
import Notifications from './pages/Notifications';
import Settings from './pages/Settings';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Toaster
            position="top-right"
            toastOptions={{
              duration: 4000,
              style: {
                background: '#363636',
                color: '#fff',
              },
              success: {
                duration: 3000,
                iconTheme: {
                  primary: '#10B981',
                  secondary: '#FFFFFF',
                },
              },
              error: {
                duration: 4000,
                iconTheme: {
                  primary: '#EF4444',
                  secondary: '#FFFFFF',
                },
              },
            }}
          />
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route
              path="/*"
              element={
                <ProtectedRoute>
                  <Routes>
                    <Route path="/" element={<Layout><Dashboard /></Layout>} />
                    <Route path="/restaurants" element={<Layout><Restaurants /></Layout>} />
                    <Route path="/customers" element={<Layout><Customers /></Layout>} />
                    <Route path="/orders" element={<Layout><Orders /></Layout>} />
                    <Route path="/delivery-partners" element={<Layout><DeliveryPartners /></Layout>} />
                    <Route path="/analytics" element={<Layout><Analytics /></Layout>} />
                    <Route path="/notifications" element={<Layout><Notifications /></Layout>} />
                    <Route path="/settings" element={<Layout><Settings /></Layout>} />
                  </Routes>
                </ProtectedRoute>
              }
            />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
