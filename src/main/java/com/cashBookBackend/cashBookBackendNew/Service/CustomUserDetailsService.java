package com.cashBookBackend.cashBookBackendNew.Service;

import com.cashBookBackend.cashBookBackendNew.Entity.User;
import com.cashBookBackend.cashBookBackendNew.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("--> [DEBUG] Attempting to find user by email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("--> [DEBUG] User NOT found in database!");
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        System.out.println("--> [DEBUG] User found! Username: " + user.getUsername() + ", Stored Hash: " + user.getPassword());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
