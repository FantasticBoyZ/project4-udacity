package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class OrderControllerTest {
    private static final Long ITEM_ID = 1L;
    private static final String ITEM_NAME = "Item Test";
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "validuser";
    private static final String NOTFOUND_USERNAME = "notfound";
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock((UserRepository.class));

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        // Create item
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName(ITEM_NAME);
        item.setPrice(BigDecimal.valueOf(3.5));
        item.setDescription("Test description");

        List<Item> items = new ArrayList<>();
        items.add(item);

        // create user
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword("password");

        // create cart
        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(3.5));

        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userRepository.findByUsername(NOTFOUND_USERNAME)).thenReturn(null);
    }

    @Test
    public void orderSuccess() {
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(1, userOrder.getItems().size());
    }

    @Test
    public void orderFailByUserNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit(NOTFOUND_USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void getOrdersByUserSuccess() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
    }

    @Test
    public void getOrdersFailByUserNotFound() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(NOTFOUND_USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }
}
