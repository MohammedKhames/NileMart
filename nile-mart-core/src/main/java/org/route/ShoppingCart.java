package org.route;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Scope("prototype")
public class ShoppingCart {

    private final ProductService productService;
    private final int maxQuantityPerProduct;

    private final Map<String, Integer> lines = new LinkedHashMap<>();

    @Getter @Setter
    private int cartNumber;

    @Getter @Setter
    private Customer customer;

    public ShoppingCart(ProductService productService,
                        @Value("${store.max-quantity-per-product}") int maxQuantityPerProduct) {
        this.productService = productService;
        this.maxQuantityPerProduct = maxQuantityPerProduct;
    }

    public Map<String, Integer> getLines() {
        return Collections.unmodifiableMap(lines);
    }


    public void addItem(String sku, int qty) {
        if (qty <= 0) {
            throw new StoreException("Quantity must be greater than zero");
        }

        Product product = productService.findBySku(sku);

        // CART-01: out-of-stock products cannot be added
        if (product.getStockQuantity() == 0) {
            throw new StoreException(product.getName() + " is out of stock");
        }

        // CART-02: adding an existing product increases its quantity
        int newQuantity = lines.getOrDefault(sku, 0) + qty;

        // CART-03: a line cannot exceed the max quantity or the stock
        if (newQuantity > maxQuantityPerProduct) {
            throw new StoreException("You can add at most " + maxQuantityPerProduct
                    + " units of " + product.getName());
        }
        if (newQuantity > product.getStockQuantity()) {
            throw new StoreException("Only " + product.getStockQuantity()
                    + " available for " + product.getName());
        }

        lines.put(sku, newQuantity);
    }


    public BigDecimal subtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> line : lines.entrySet()) {
            total = total.add(productService.getPrice(line.getKey(), line.getValue()));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
    }


}