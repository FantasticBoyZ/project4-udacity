package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class ItemControllerTest {

    public static final Long ITEM_ID = 1L;

    public static final String ITEM_NAME = "Item Name";

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        // create item
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName(ITEM_NAME);
        item.setPrice(BigDecimal.valueOf(3.5));
        item.setDescription("Item Description");

        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(itemRepository.findByName(ITEM_NAME)).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void getAllSuccess() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void GetItemByIdSuccess() {
        ResponseEntity<Item> response = itemController.getItemById(ITEM_ID);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        assertNotNull(item);
    }

    @Test
    public void getItemByIdNotFound() {
        ResponseEntity<Item> response = itemController.getItemById(2L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemByNameSuccess() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName(ITEM_NAME);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }
    @Test
    public void getItemByNameNotFound() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item Name Not Found");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
