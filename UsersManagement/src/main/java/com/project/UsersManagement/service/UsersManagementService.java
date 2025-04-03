package com.project.UsersManagement.service;

import com.project.UsersManagement.dto.ReqRes;
import com.project.UsersManagement.entity.OurUsers;
import com.project.UsersManagement.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (usersRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("Email already registered");
                return resp;
            }

            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            OurUsers savedUser = usersRepo.save(ourUser);
            resp.setOurUsers(savedUser);
            resp.setMessage("User registered successfully");
            resp.setStatusCode(201);

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error: " + e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));

            OurUsers user = usersRepo.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setRole(user.getRole());
            response.setMessage("Successfully logged in");

        } catch (BadCredentialsException e) {
            response.setStatusCode(401);
            response.setMessage("Invalid email or password");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Login failed: " + e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String userEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsers user = usersRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                String newJwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(newJwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hrs");
                response.setMessage("Token refreshed successfully");
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid refresh token");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error refreshing token: " + e.getMessage());
        }
        return response;
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();
        try {
            List<OurUsers> users = usersRepo.findAll();
            if (users.isEmpty()) {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            } else {
                reqRes.setOurUsersList(users);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Users retrieved successfully");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error retrieving users: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getUsersById(String id) {  // Change Integer to String
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(String userId) {  // Change Integer to String
        ReqRes reqRes = new ReqRes();
        try {
            if (usersRepo.existsById(userId)) {  // Use String ID
                usersRepo.deleteById(userId);  // Use String ID
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(String userId, OurUsers updatedUser) {  // Change Integer to String
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);  // Use String ID
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());
    
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
    
                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers user = usersRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
    
            System.out.println("✅ User found: " + user.getEmail()); // Debugging
    
            reqRes.setOurUsers(user);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User info retrieved successfully");
    
        } catch (Exception e) {
            System.out.println("❌ Error retrieving user info: " + e.getMessage()); // Debugging
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error retrieving user info: " + e.getMessage());
        }
        return reqRes;
    }
    
}
