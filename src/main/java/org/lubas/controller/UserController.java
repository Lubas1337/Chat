package org.lubas.controller;


import org.lubas.domain.entity.AppUser;
import org.lubas.domain.dto.request.LoginRequest;
import org.lubas.domain.dto.request.RegisterRequest;
import org.lubas.domain.dto.response.JwtResponse;
import org.lubas.domain.dto.response.MessageResponse;
import org.lubas.domain.dto.response.UserResponse;
import org.lubas.security.jwt.JwtUtils;
import org.lubas.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        if (userService.existsByUsername(registerRequest.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already taken." , registerRequest.getUsername()));
        }

        userService.registerUser(registerRequest);

        return ResponseEntity
                .ok(new MessageResponse("User registered successfully" , registerRequest.getUsername()));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        userService.updateLastSeenAt(appUser, "Online");

        return ResponseEntity
                .ok()
                .body(new JwtResponse(
                        jwt,
                        new UserResponse(appUser.getUsername(), appUser.getLastSeenAt()),
                        "Login is successful"
                ));
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String jwtToken){
        AppUser appUser = userService.findUserFromJwtToken(jwtToken);

        if(appUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();
        String strDate = dateFormatter.format(currentDate);
        userService.updateLastSeenAt(appUser, strDate);
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Logout was successful" , appUser.getUsername()));

    }

    @DeleteMapping(value = "/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username,
                                        @RequestHeader("Authorization") String jwtToken){

        AppUser appUser = userService.findUserFromJwtToken(jwtToken);

        if(appUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }

        if(!appUser.getUsername().equals(username)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User only can delete itself" , username));
        }

        userService.deleteUser(appUser);
        return ResponseEntity
                .ok()
                .body(new MessageResponse("User deleted successfully" , username));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String jwtToken){
        AppUser appUser = userService.findUserFromJwtToken(jwtToken);

        if(appUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }

        return ResponseEntity
                .ok()
                .body(new UserResponse(appUser.getUsername(), appUser.getLastSeenAt()));
    }

    @GetMapping(value = "/getAllUser")
    public ResponseEntity<?> getAllUser(@RequestHeader("Authorization") String jwtToken){
        AppUser appUser = userService.findUserFromJwtToken(jwtToken);
        if(appUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }

        List<UserResponse> userList = userService.getAllUsers();
        userList.removeIf(s -> s.getUsername().equals(appUser.getUsername()));
        return ResponseEntity
                .ok()
                .body(userList);
    }
}
