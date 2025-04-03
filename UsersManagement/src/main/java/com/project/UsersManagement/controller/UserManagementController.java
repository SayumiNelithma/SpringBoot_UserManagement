package com.project.UsersManagement.controller;

import com.project.UsersManagement.dto.ReqRes;
import com.project.UsersManagement.entity.OurUsers;
import com.project.UsersManagement.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserByID(@PathVariable String userId) {
        ReqRes response = usersManagementService.getUsersById(userId);

        if (response.getOurUsers() == null) {
            response.setStatusCode(404);
            response.setMessage("User not found");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable String userId, @RequestBody OurUsers reqres) {
        ReqRes response = usersManagementService.updateUser(userId, reqres);

        if (response.getOurUsers() == null) {
            response.setStatusCode(404);
            response.setMessage("User not found or update failed");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(401);
            response.setMessage("User is not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        String email = authentication.getName();
        System.out.println("🔍 Fetching profile for: " + email); // Debugging

        ReqRes response = usersManagementService.getMyInfo(email);

        if (response == null || response.getOurUsers() == null) {
            response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("Profile not found");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable String userId) {
        ReqRes response = usersManagementService.deleteUser(userId);

        if (response.getMessage() == null) {
            response.setStatusCode(404);
            response.setMessage("User not found or deletion failed");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
