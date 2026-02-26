package com.vishvakta.example.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for carts. Contains deliberate triggers for static analysis demo.
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestParam String customerId) {
        Cart cart = cartService.createCart(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        Cart cart = cartService.getCart(id);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    /**
     * Deliberate: possible NPE when cart missing - service getTotalItemCount can NPE.
     */
    @GetMapping("/{id}/total-items")
    public ResponseEntity<Integer> getTotalItemCount(@PathVariable Long id) {
        int total = cartService.getTotalItemCount(id);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Boolean> addItem(
            @PathVariable Long id,
            @RequestParam String productId,
            @RequestParam int quantity) {
        boolean added = cartService.addItem(id, productId, quantity);
        if (!added) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}/items/{productId}")
    public ResponseEntity<Boolean> removeItem(@PathVariable Long id, @PathVariable String productId) {
        boolean removed = cartService.removeItem(id, productId);
        return ResponseEntity.ok(removed);
    }

    @GetMapping("/{id}/line-count")
    public ResponseEntity<Integer> getLineItemCount(@PathVariable Long id) {
        int count = cartService.getLineItemCount(id);
        if (count < 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/contains")
    public ResponseEntity<Boolean> containsProduct(@PathVariable Long id, @RequestParam String productId) {
        boolean contains = cartService.containsProduct(id, productId);
        return ResponseEntity.ok(contains);
    }

    @GetMapping
    public ResponseEntity<List<Cart>> listCarts() {
        List<Cart> carts = cartService.findAllCarts();
        return ResponseEntity.ok(carts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok().build();
    }
}
