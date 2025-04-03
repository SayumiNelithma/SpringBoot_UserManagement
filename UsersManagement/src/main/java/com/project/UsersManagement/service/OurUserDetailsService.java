package com.project.UsersManagement.service;

import com.project.UsersManagement.entity.OurUsers;
import com.project.UsersManagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OurUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user by email (username)
        OurUsers user = usersRepo.findByEmail(username).orElseThrow(() -> 
            new UsernameNotFoundException("User not found with email: " + username));
        
        // Return the user (since OurUsers implements UserDetails)
        return user;
    }
}
