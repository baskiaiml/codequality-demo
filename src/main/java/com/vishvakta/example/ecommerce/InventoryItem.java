package com.vishvakta.example.ecommerce;

import java.util.Objects;

/**
 * Inventory item entity for ecommerce demo.
 */
public class InventoryItem {

    private Long id;
    private String sku;
    private String name;
    private int quantityInStock;
    private int reorderThreshold;

    public InventoryItem() {
    }

    public InventoryItem(Long id, String sku, String name, int quantityInStock, int reorderThreshold) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.reorderThreshold = reorderThreshold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public boolean needsReorder() {
        return quantityInStock <= reorderThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return quantityInStock == that.quantityInStock
                && reorderThreshold == that.reorderThreshold
                && Objects.equals(id, that.id)
                && Objects.equals(sku, that.sku)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku, name, quantityInStock, reorderThreshold);
    }
}
