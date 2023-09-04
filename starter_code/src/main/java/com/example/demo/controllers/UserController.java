package com.example.demo.controllers;

import java.util.Date;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.example.demo.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.info("username {}", username);
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        boolean isBadRequest = false;
        // Check username valid
        if (createUserRequest.getUsername() == null) {
            log.error("Your username is invalid! Please try again!");
            isBadRequest = true;
        }

        if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
            log.error("Your username is existed! Please try another!");
            isBadRequest = true;
        }


        // Check password valid
        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().length() < 8 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("Your password is invalid! Please try again!");
            isBadRequest = true;
        }

        // Check confirmPassword valid
        if (createUserRequest.getConfirmPassword() == null) {
            log.error("Your confirmPassword is invalid! Please try again!");
            isBadRequest = true;
        }

        if (isBadRequest) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);
        log.info("User created successfully with username: {} ", user.getUsername());
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
        log.info("Token: {} ", token);
        return ResponseEntity.ok(user);
    }

}
