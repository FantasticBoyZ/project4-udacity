package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class UserControllerTest {
    private static final String USERNAME = "validuser";
    private static final String PASSWORD = "12345678";
    private static final String NOTFOUND_USERNAME = "notfound";
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp () {
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);

        // create user
        User user = new User();

        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(new Cart());

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(NOTFOUND_USERNAME)).thenReturn(null);
    }

    @Test
    public void createUserSuccess() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword(PASSWORD);
        request.setConfirmPassword(PASSWORD);
        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("newuser", user.getUsername());

    }

    @Test
    public void findUserByUsername() {
        final ResponseEntity<User> response = userController.findByUserName(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void findUserByUsernameNotFound() {
        final ResponseEntity<User> response = userController.findByUserName(NOTFOUND_USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void findUserById() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(1, user.getId());;
    }

    @Test
    public void findUserByIdNotFound() {
        final ResponseEntity<User> response = userController.findById(2L);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }
}
