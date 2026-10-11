package com.devbank.inventory.service;

import com.devbank.inventory.model.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service layer for inventory operations.
 * In a production system this would connect to a database.
 * For this demo, sample data is returned in-memory.
 */
@Service
public class InventoryService {

    /**
     * Returns a list of sample inventory items.
     */
    public List<InventoryItem> getAllItems() {
        return Arrays.asList(
            new InventoryItem(1L, "Laptop",           "Computing",   45, "Available"),
            new InventoryItem(2L, "Desktop Computer", "Computing",   30, "Available"),
            new InventoryItem(3L, "Monitor",          "Display",     60, "Available"),
            new InventoryItem(4L, "Keyboard",         "Peripherals", 80, "Available"),
            new InventoryItem(5L, "Mouse",            "Peripherals", 75, "Available"),
            new InventoryItem(6L, "Network Switch",   "Networking",  15, "In Use"),
            new InventoryItem(7L, "USB-C Hub",        "Accessories", 50, "Available"),
            new InventoryItem(8L, "Webcam",           "Peripherals", 25, "Low Stock")
        );
    }
}
