package com.tummytrek.service;

import com.tummytrek.dto.request.CustomerRegistrationRequest;
import com.tummytrek.dto.response.CustomerResponse;
import com.tummytrek.entity.Customer;
import com.tummytrek.entity.CustomerAddress;
import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import com.tummytrek.exception.ResourceNotFoundException;
import com.tummytrek.repository.CustomerRepository;
import com.tummytrek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Customer createCustomer(CustomerRegistrationRequest request) {
        // Create user first
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        
        User savedUser = userRepository.save(user);
        
        // Create customer
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setPreferredLanguage(request.getPreferredLanguage());
        customer.setDietaryPreferences(request.getDietaryPreferences());
        
        return customerRepository.save(customer);
    }
    
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }
    
    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));
    }
    
    public Customer updateCustomer(Long customerId, CustomerRegistrationRequest request) {
        Customer customer = getCustomerById(customerId);
        
        // Update user details
        User user = customer.getUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // Update customer details
        customer.setPreferredLanguage(request.getPreferredLanguage());
        customer.setDietaryPreferences(request.getDietaryPreferences());
        
        return customerRepository.save(customer);
    }
    
    public void deleteCustomer(Long customerId) {
        Customer customer = getCustomerById(customerId);
        customer.setIsActive(false);
        customerRepository.save(customer);
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findByIsActiveTrue();
    }
    
    public Customer addLoyaltyPoints(Long customerId, Integer points) {
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        Customer customer = getCustomerById(customerId);
        Integer currentPoints = customer.getLoyaltyPoints();
        if (currentPoints == null) currentPoints = 0;
        customer.setLoyaltyPoints(currentPoints + points);
        return customerRepository.save(customer);
    }
    
    public Customer deductLoyaltyPoints(Long customerId, Integer points) {
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        Customer customer = getCustomerById(customerId);
        Integer currentPoints = customer.getLoyaltyPoints();
        if (currentPoints == null) currentPoints = 0;
        if (currentPoints < points) {
            throw new IllegalArgumentException("Insufficient loyalty points");
        }
        customer.setLoyaltyPoints(currentPoints - points);
        return customerRepository.save(customer);
    }
    
    public List<CustomerAddress> getCustomerAddresses(Long customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getAddresses();
    }
    
    // Missing methods for controllers
    public long countActiveCustomers() {
        return customerRepository.countActiveCustomers();
    }
    
    public Page<Customer> findAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    }
}
