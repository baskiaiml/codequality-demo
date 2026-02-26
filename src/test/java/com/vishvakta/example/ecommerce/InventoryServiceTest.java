package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
    }

    @Test
    @DisplayName("addItem returns item with generated id")
    void addItem_returnsItemWithId() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 100, 10);
        assertNotNull(item.getId());
        assertEquals("SKU-001", item.getSku());
        assertEquals("Widget", item.getName());
        assertEquals(100, item.getQuantityInStock());
        assertEquals(10, item.getReorderThreshold());
        assertFalse(item.needsReorder());
    }

    @Test
    @DisplayName("addItem rejects null sku")
    void addItem_throwsWhenSkuNull() {
        assertThrows(IllegalArgumentException.class, () ->
                inventoryService.addItem(null, "Name", 10, 5));
    }

    @Test
    @DisplayName("addItem rejects null name")
    void addItem_throwsWhenNameNull() {
        assertThrows(IllegalArgumentException.class, () ->
                inventoryService.addItem("SKU-001", null, 10, 5));
    }

    @Test
    @DisplayName("addItem rejects duplicate SKU")
    void addItem_throwsWhenSkuExists() {
        inventoryService.addItem("SKU-001", "Widget", 10, 5);
        assertThrows(IllegalArgumentException.class, () ->
                inventoryService.addItem("SKU-001", "Other", 20, 5));
    }

    @Test
    @DisplayName("getItem returns null for unknown id")
    void getItem_returnsNullForUnknownId() {
        assertNull(inventoryService.getItem(999L));
    }

    @Test
    @DisplayName("getItem returns saved item")
    void getItem_returnsSavedItem() {
        InventoryItem created = inventoryService.addItem("SKU-001", "Widget", 50, 10);
        InventoryItem found = inventoryService.getItem(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("SKU-001", found.getSku());
    }

    @Test
    @DisplayName("getItemBySku returns item when exists")
    void getItemBySku_returnsItemWhenExists() {
        inventoryService.addItem("SKU-ABC", "Gadget", 25, 5);
        InventoryItem found = inventoryService.getItemBySku("SKU-ABC");
        assertNotNull(found);
        assertEquals("SKU-ABC", found.getSku());
    }

    @Test
    @DisplayName("getItemBySku returns null for unknown sku")
    void getItemBySku_returnsNullForUnknownSku() {
        assertNull(inventoryService.getItemBySku("UNKNOWN"));
    }

    @Test
    @DisplayName("needsReorder returns true when quantity at or below threshold")
    void needsReorder_returnsTrueWhenLowStock() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 5, 10);
        assertTrue(inventoryService.needsReorder(item.getId()));
    }

    @Test
    @DisplayName("needsReorder returns false when quantity above threshold")
    void needsReorder_returnsFalseWhenEnoughStock() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 50, 10);
        assertFalse(inventoryService.needsReorder(item.getId()));
    }

    @Test
    @DisplayName("adjustStock updates quantity and returns new value")
    void adjustStock_updatesAndReturnsNewQty() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 20, 5);
        int newQty = inventoryService.adjustStock(item.getId(), -7);
        assertEquals(13, newQty);
        assertEquals(13, inventoryService.getItem(item.getId()).getQuantityInStock());
    }

    @Test
    @DisplayName("adjustStock does not go below zero")
    void adjustStock_doesNotGoBelowZero() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 5, 5);
        int newQty = inventoryService.adjustStock(item.getId(), -100);
        assertEquals(0, newQty);
        assertEquals(0, inventoryService.getItem(item.getId()).getQuantityInStock());
    }

    @Test
    @DisplayName("adjustStock returns -1 for unknown id")
    void adjustStock_returnsMinusOneForUnknownId() {
        assertEquals(-1, inventoryService.adjustStock(999L, 10));
    }

    @Test
    @DisplayName("getItemDescription returns NOT_FOUND for unknown id")
    void getItemDescription_returnsNotFoundForUnknownId() {
        assertEquals("NOT_FOUND", inventoryService.getItemDescription(999L));
    }

    @Test
    @DisplayName("getItemDescription returns sku and name for existing item")
    void getItemDescription_returnsSkuAndName() {
        InventoryItem item = inventoryService.addItem("SKU-X", "Product X", 10, 2);
        assertEquals("SKU-X - Product X", inventoryService.getItemDescription(item.getId()));
    }

    @Test
    @DisplayName("hasSku returns true when sku matches")
    void hasSku_returnsTrueWhenMatch() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 10, 5);
        assertTrue(inventoryService.hasSku(item.getId(), "SKU-001"));
    }

    @Test
    @DisplayName("hasSku returns false when sku differs")
    void hasSku_returnsFalseWhenDifferent() {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 10, 5);
        assertFalse(inventoryService.hasSku(item.getId(), "SKU-002"));
    }

    @Test
    @DisplayName("findAllItems returns empty list when no items")
    void findAllItems_returnsEmptyWhenNone() {
        List<InventoryItem> items = inventoryService.findAllItems();
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("findAllItems returns all added items")
    void findAllItems_returnsAllItems() {
        inventoryService.addItem("SKU-1", "A", 10, 5);
        inventoryService.addItem("SKU-2", "B", 20, 10);
        List<InventoryItem> items = inventoryService.findAllItems();
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("findItemsNeedingReorder returns only low-stock items")
    void findItemsNeedingReorder_returnsOnlyLowStock() {
        inventoryService.addItem("SKU-HIGH", "High", 100, 10);
        inventoryService.addItem("SKU-LOW", "Low", 3, 10);
        List<InventoryItem> reorder = inventoryService.findItemsNeedingReorder();
        assertEquals(1, reorder.size());
        assertEquals("SKU-LOW", reorder.get(0).getSku());
    }

    @Test
    @DisplayName("clearAll removes all items")
    void clearAll_removesAllItems() {
        inventoryService.addItem("SKU-001", "Widget", 10, 5);
        inventoryService.clearAll();
        assertTrue(inventoryService.findAllItems().isEmpty());
        assertNull(inventoryService.getItem(1L));
        assertNull(inventoryService.getItemBySku("SKU-001"));
    }
}
