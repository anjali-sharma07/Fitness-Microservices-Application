package com.fitness.userservice.controller;

import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userID}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userID){
        return new ResponseEntity( userService.getByID(userID),HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterRequest request){
        return new ResponseEntity(userService.register(request), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{userID}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userID){
        return new ResponseEntity( userService.existById(userID),HttpStatus.ACCEPTED);
    }
}
