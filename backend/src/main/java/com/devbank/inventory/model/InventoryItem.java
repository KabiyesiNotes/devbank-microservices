package com.devbank.inventory.model;

/**
 * Represents an inventory item in the DevBank system.
 */
public class InventoryItem {

    private Long id;
    private String name;
    private String category;
    private int quantity;
    private String status;

    public InventoryItem() {}

    public InventoryItem(Long id, String name, String category, int quantity, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
