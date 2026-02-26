package com.vishvakta.example.ecommerce;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inventory service with deliberate SpotBugs/PMD triggers for demo.
 */
@Service
public class InventoryService {

    // Deliberate: unused field - UUF_UNUSED_FIELD
    private static final String UNUSED_PREFIX = "INV-";

    private final Map<Long, InventoryItem> items = new ConcurrentHashMap<>();
    private final Map<String, Long> skuToId = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public InventoryItem addItem(String sku, String name, int quantityInStock, int reorderThreshold) {
        if (sku == null || name == null) {
            throw new IllegalArgumentException("sku and name must not be null");
        }
        if (skuToId.containsKey(sku)) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }
        InventoryItem item = new InventoryItem(nextId++, sku, name, quantityInStock, reorderThreshold);
        items.put(item.getId(), item);
        skuToId.put(sku, item.getId());
        return item;
    }

    public InventoryItem getItem(Long id) {
        return items.get(id);
    }

    public InventoryItem getItemBySku(String sku) {
        Long id = skuToId.get(sku);
        return id != null ? items.get(id) : null;
    }

    /**
     * Deliberate: possible NPE - NP_NULL_ON_SOME_PATH when item is missing.
     */
    public boolean needsReorder(Long id) {
        InventoryItem item = items.get(id);
        return item.needsReorder();
    }

    /**
     * Deliberate: empty catch block - DE_MIGHT_IGNORE / EmptyCatchBlock.
     */
    public int adjustStock(Long id, int delta) {
        try {
            InventoryItem item = items.get(id);
            if (item != null) {
                int newQty = Math.max(0, item.getQuantityInStock() + delta);
                item.setQuantityInStock(newQty);
                return newQty;
            }
            return -1;
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * Deliberate: dead store - DLS_DEAD_STORE.
     */
    public String getItemDescription(Long id) {
        InventoryItem item = items.get(id);
        if (item == null) {
            return "NOT_FOUND";
        }
        String desc = item.getSku() + " - " + item.getName();
        String unused = desc;
        return desc;
    }

    /**
     * Deliberate: string comparison with == - ES_COMPARING_STRINGS_WITH_EQ.
     */
    public boolean hasSku(Long id, String sku) {
        InventoryItem item = items.get(id);
        if (item == null) {
            return false;
        }
        return item.getSku() == sku;
    }

    public List<InventoryItem> findAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<InventoryItem> findItemsNeedingReorder() {
        List<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : items.values()) {
            if (item.needsReorder()) {
                result.add(item);
            }
        }
        return result;
    }

    public void clearAll() {
        items.clear();
        skuToId.clear();
    }
}
