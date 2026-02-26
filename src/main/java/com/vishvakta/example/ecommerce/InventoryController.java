package com.vishvakta.example.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for inventory. Contains deliberate triggers for static analysis demo.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> addItem(
            @RequestParam String sku,
            @RequestParam String name,
            @RequestParam int quantityInStock,
            @RequestParam int reorderThreshold) {
        InventoryItem item = inventoryService.addItem(sku, name, quantityInStock, reorderThreshold);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItem(@PathVariable Long id) {
        InventoryItem item = inventoryService.getItem(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventoryItem> getItemBySku(@PathVariable String sku) {
        InventoryItem item = inventoryService.getItemBySku(sku);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    /**
     * Deliberate: possible NPE when item missing - controller calls service that can NPE.
     */
    @GetMapping("/{id}/needs-reorder")
    public ResponseEntity<Boolean> needsReorder(@PathVariable Long id) {
        boolean needs = inventoryService.needsReorder(id);
        return ResponseEntity.ok(needs);
    }

    @PostMapping("/{id}/adjust")
    public ResponseEntity<Integer> adjustStock(@PathVariable Long id, @RequestParam int delta) {
        int newQty = inventoryService.adjustStock(id, delta);
        if (newQty < 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newQty);
    }

    @GetMapping("/{id}/description")
    public ResponseEntity<String> getDescription(@PathVariable Long id) {
        String desc = inventoryService.getItemDescription(id);
        if ("NOT_FOUND".equals(desc)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(desc);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> listItems() {
        List<InventoryItem> items = inventoryService.findAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/reorder")
    public ResponseEntity<List<InventoryItem>> itemsNeedingReorder() {
        List<InventoryItem> items = inventoryService.findItemsNeedingReorder();
        return ResponseEntity.ok(items);
    }
}
