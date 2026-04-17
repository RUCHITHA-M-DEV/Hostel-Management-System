package com.hostel.factory;

import com.hostel.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creational Pattern: Factory Method
 * Encapsulates User object creation logic for different roles.
 */
@Component
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createStudent(String username, String rawPassword, String email, String fullName, String phoneNumber) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setRole(User.Role.STUDENT);
        user.setEnabled(true);
        return user;
    }

    public User createWarden(String username, String rawPassword, String email, String fullName, String phoneNumber) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setRole(User.Role.WARDEN);
        user.setEnabled(true);
        return user;
    }

    public User createAdmin(String username, String rawPassword, String email, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(User.Role.ADMIN);
        user.setEnabled(true);
        return user;
    }
}
