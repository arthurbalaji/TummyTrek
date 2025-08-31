-- Create initial admin user for TummyTrek
-- Run this script if you need to create admin user manually

-- Insert admin user (password is 'admin123' encoded with BCrypt)
-- Note: The password hash below is for 'admin123' - please change after first login
INSERT INTO users (
    name, 
    email, 
    phone, 
    password, 
    role, 
    status, 
    email_verified, 
    phone_verified, 
    created_at, 
    updated_at
) VALUES (
    'Admin User',
    'admin@tummytrek.com',
    '+1234567890',
    '$2a$10$X3JZ9Q7K1.vZ2f8h6mX7Xu7gB5q8YxL2kR6wT9mV3nC1sP4hJ8aEu',
    'ADMIN',
    'ACTIVE',
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Display success message
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE email = 'admin@tummytrek.com') THEN
        RAISE NOTICE '✅ Admin user created/exists successfully!';
        RAISE NOTICE '📧 Email: admin@tummytrek.com';
        RAISE NOTICE '🔑 Password: admin123';
        RAISE NOTICE '⚠️  Please change the password after first login!';
    END IF;
END $$;
