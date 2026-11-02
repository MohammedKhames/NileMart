package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public Order placeOrder(ShoppingCart cart, Governorate governorate) {
        // 1) reject an empty cart (ORD-02)
        if (cart.isEmpty()) {
            throw new StoreException("Cannot check out an empty cart");
        }

        // 2) re-check the stock of every line before changing anything (ORD-03)
        for (Map.Entry<String, Integer> line : cart.getLines().entrySet()) {
            Product product = productService.findBySku(line.getKey());
            if (product.getStockQuantity() < line.getValue()) {
                throw new StoreException("Not enough stock for " + product.getName()
                        + ": only " + product.getStockQuantity() + " available");
            }
        }

        // 3) calculate the totals
        BigDecimal subtotal = cart.subtotal();
        BigDecimal vat = taxCalculator.calculateVat(subtotal);
        BigDecimal shippingFee = shippingQuoteService.quote(governorate, subtotal);
        BigDecimal total = subtotal.add(vat).add(shippingFee).setScale(2, RoundingMode.HALF_UP);

        // 4) reduce the stock (ORD-06)
        for (Map.Entry<String, Integer> line : cart.getLines().entrySet()) {
            productService.reduceStock(line.getKey(), line.getValue());
        }

        // 5) save the order
        Order order = new Order(orderRegistry.nextOrderNumber(), cart.getCustomer(), governorate,
                cart.getLines(), subtotal, vat, shippingFee, total);
        orderRegistry.save(order);

        // 6) notify through every sender
        String message = String.format(Locale.US, "Order %s confirmed. Total %,.2f EGP",
                order.orderNumber(), order.total());
        for (NotificationSender sender : notificationSenders) {
            sender.send(order.customer(), message);
        }

        // 7) clear the cart
        cart.clear();
        return order;
    }
}