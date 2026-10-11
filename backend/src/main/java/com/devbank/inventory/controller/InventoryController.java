package com.devbank.inventory.controller;

import com.devbank.inventory.model.InventoryItem;
import com.devbank.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller exposing inventory and health endpoints.
 */
@RestController
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Root endpoint — confirms the backend is running.
     * GET /
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("service", "DevBank Inventory Backend");
        response.put("status", "running");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "inventory-backend");
        return ResponseEntity.ok(response);
    }

    /**
     * Returns all inventory items as JSON.
     * GET /api/inventory
     */
    @GetMapping("/api/inventory")
    public ResponseEntity<List<InventoryItem>> getInventory() {
        List<InventoryItem> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }
}
