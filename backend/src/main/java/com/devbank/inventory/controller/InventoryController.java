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
 * REST controller exposing all public API endpoints.
 *
 * CORS is opened broadly here so the frontend container
 * (served from a different origin) can call the backend
 * without browser-side errors.
 */
@RestController
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * GET /
     * Simple liveness message confirming the service is running.
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
     * GET /health
     * Health-check endpoint used by load balancers and ECS.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "DevBank Inventory Backend");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/inventory
     * Returns the full list of inventory items as JSON.
     */
    @GetMapping("/api/inventory")
    public ResponseEntity<List<InventoryItem>> getInventory() {
        List<InventoryItem> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }
}
