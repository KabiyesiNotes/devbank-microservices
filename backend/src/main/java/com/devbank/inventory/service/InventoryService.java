package com.devbank.inventory.service;

import com.devbank.inventory.model.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service layer for inventory operations.
 * Returns in-memory sample data for demonstration purposes.
 */
@Service
public class InventoryService {

    /**
     * Returns the full list of inventory items.
     */
    public List<InventoryItem> getAllItems() {
        return Arrays.asList(
            new InventoryItem(1L,  "Laptop",           "Computers",    45,  1299.99),
            new InventoryItem(2L,  "Desktop Computer", "Computers",    30,   899.99),
            new InventoryItem(3L,  "Monitor",          "Displays",     60,   349.99),
            new InventoryItem(4L,  "Keyboard",         "Peripherals", 120,    49.99),
            new InventoryItem(5L,  "Mouse",            "Peripherals", 150,    29.99),
            new InventoryItem(6L,  "USB-C Hub",        "Accessories",  80,    39.99),
            new InventoryItem(7L,  "Webcam",           "Peripherals",  40,    89.99),
            new InventoryItem(8L,  "Headset",          "Audio",        55,    79.99),
            new InventoryItem(9L,  "Network Switch",   "Networking",   20,   199.99),
            new InventoryItem(10L, "External SSD",     "Storage",      75,   109.99)
        );
    }
}
