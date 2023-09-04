package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private static final String USERNAME = "testUsername";
    private static final long ITEM_ID = 1L;
    private static final int QUANTITY = 2;

    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername(USERNAME);
        user.setPassword("password");
        user.setCart(cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName("Test Item Name");
        BigDecimal price = BigDecimal.valueOf(3.5);
        item.setPrice(price);
        item.setDescription("Test Item Description");
        when(itemRepository.findById(ITEM_ID)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void addToCartSuccess() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(7.0), cart.getTotal());
    }

    @Test
    public void addToCartInvalidUserFail() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setUsername("userFail");
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartInvalidItemFail() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartSuccess() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setQuantity(QUANTITY);

        response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(0.0), cart.getTotal());
    }

    @Test
    public void removeFromCartFailureInvalidUser() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setUsername("userFail");
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartFailureInvalidItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setUsername(USERNAME);
        modifyCartRequest.setQuantity(QUANTITY);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
