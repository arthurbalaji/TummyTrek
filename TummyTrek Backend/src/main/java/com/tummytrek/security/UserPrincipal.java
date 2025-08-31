package com.tummytrek.security;

import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private UserRole role;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    
    public UserPrincipal(Long id, String name, String email, String phone, String password, 
                        UserRole role, Boolean emailVerified, Boolean phoneVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
    }
    
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getPassword(),
            user.getRole(),
            user.getEmailVerified(),
            user.getPhoneVerified()
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email != null ? email : phone;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true; // You might want to check user status here
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public Boolean getPhoneVerified() {
        return phoneVerified;
    }
}
