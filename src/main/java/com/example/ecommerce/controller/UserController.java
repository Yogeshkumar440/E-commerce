package com.example.ecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.dto.ChangePasswordRequest;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.entity.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            userRepository.delete(user);
            return ResponseEntity.ok("User deleted successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in the database");
    }


    @PostMapping("/{userId}/assign-role/{roleName}")
    public ResponseEntity<String> assignRole(@PathVariable Long userId,@PathVariable String roleName){
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if(userOptional.isEmpty() || roleOptional.isEmpty()){
            return ResponseEntity.badRequest().body("User or role not found!");
        }

        User user = userOptional.get();
        user.getRoles().add(roleOptional.get());
        userRepository.save(user);

        return ResponseEntity.ok("Role assigned successfully!");
    }


    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
                    @AuthenticationPrincipal UserDetails userDetails,
                    @RequestBody ChangePasswordRequest request){

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
                return ResponseEntity.badRequest().body("Old password is incorrect!");
            }

            if(passwordEncoder.matches(request.getNewPassword(),user.getPassword())){
                return ResponseEntity.badRequest().body("New password cannot be the same as the old password!");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Password changed successfully!");
        }

        return ResponseEntity.notFound().build();

    }
    

    // Get user Profile (Authenticated User)
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update User info
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,@RequestBody User updatedUser){
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if(userOptional.isPresent()){
            User user = userOptional.get();
            
            if(!user.getEmail().equals(updatedUser.getEmail()) && userRepository.findByEmail(updatedUser.getEmail()).isPresent()){
                return ResponseEntity.badRequest().body("Email already exist! Please use a different email:");
            }
            user.setName(updatedUser.getName()==null?user.getName():updatedUser.getName());
            user.setEmail(updatedUser.getEmail()==null?user.getEmail():updatedUser.getEmail());

            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully!");

        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }
}
