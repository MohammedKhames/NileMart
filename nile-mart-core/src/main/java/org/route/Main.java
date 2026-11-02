package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final Map<Integer, ShoppingCart> carts = new LinkedHashMap<>();
    private static int nextCartNumber = 1;

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService productService = context.getBean(ProductService.class);
            CheckoutService checkoutService = context.getBean(CheckoutService.class);

            boolean running = true;
            while (running) {
                printMenu();
                String choice = in.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> showProducts(productService);
                        case "2" -> createCart(context);
                        case "3" -> addToCart();
                        case "4" -> showCart(productService);
                        case "5" -> checkOut(checkoutService);
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option, try again.");
                    }
                } catch (StoreException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } // the context is closed here, so the @PreDestroy methods run
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========== NILEMART ==========");
        System.out.println("1. Show products");
        System.out.println("2. Create a cart");
        System.out.println("3. Add a product to a cart");
        System.out.println("4. Show a cart");
        System.out.println("5. Check out a cart");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private static String ask(String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }

    private static int readInt(String label) {
        String text = ask(label);
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new StoreException("Please enter a valid number");
        }
    }

    private static ShoppingCart findCart() {
        int number = readInt("Cart number");
        ShoppingCart cart = carts.get(number);
        if (cart == null) {
            throw new StoreException("Cart #" + number + " does not exist");
        }
        return cart;
    }

    private static void showProducts(ProductService productService) {
        System.out.printf("%-13s %-28s %-15s %12s %6s%n", "SKU", "Name", "Category", "Price (EGP)", "Stock");
        for (Product p : productService.findAll()) {
            System.out.println(String.format(Locale.US, "%-13s %-28s %-15s %,12.2f %6d",
                    p.getSku(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQuantity()));
        }
    }

    private static void createCart(AnnotationConfigApplicationContext context) {
        String name = ask("Customer name");
        String phone = ask("Phone");
        String email = ask("Email (press Enter to skip)");

        ShoppingCart cart = context.getBean(ShoppingCart.class);   // a new cart from the container
        cart.setCartNumber(nextCartNumber++);
        cart.setCustomer(new Customer(name, phone, email.isBlank() ? null : email));
        carts.put(cart.getCartNumber(), cart);

        System.out.println("Cart #" + cart.getCartNumber() + " created for " + name);
    }

    private static void addToCart() {
        ShoppingCart cart = findCart();
        String sku = ask("Product SKU").toUpperCase();
        int quantity = readInt("Quantity");

        cart.addItem(sku, quantity);
        System.out.println("Added " + quantity + " x " + sku + " to cart #" + cart.getCartNumber());
    }

    private static void showCart(ProductService productService) {
        ShoppingCart cart = findCart();
        System.out.println("Cart #" + cart.getCartNumber() + " - " + cart.getCustomer().fullName());

        if (cart.isEmpty()) {
            System.out.println("The cart is empty.");
            return;
        }

        for (Map.Entry<String, Integer> line : cart.getLines().entrySet()) {
            Product product = productService.findBySku(line.getKey());
            System.out.println(String.format(Locale.US, "%-28s x%-3d %,12.2f EGP",
                    product.getName(), line.getValue(),
                    productService.getPrice(line.getKey(), line.getValue())));
        }
        System.out.println(String.format(Locale.US, "Subtotal: %,.2f EGP", cart.subtotal()));
    }

    private static void checkOut(CheckoutService checkoutService) {
        ShoppingCart cart = findCart();
        Governorate governorate = readGovernorate();

        Order order = checkoutService.placeOrder(cart, governorate);
        printOrder(order);
    }

    private static void printOrder(Order order) {
        System.out.println();
        System.out.println("Order " + order.orderNumber() + " for " + order.customer().fullName()
                + " (" + order.governorate() + ")");
        System.out.println(String.format(Locale.US, "Subtotal: %,.2f EGP", order.subtotal()));
        System.out.println(String.format(Locale.US, "VAT:      %,.2f EGP", order.vat()));
        System.out.println(String.format(Locale.US, "Shipping: %,.2f EGP", order.shippingFee()));
        System.out.println(String.format(Locale.US, "Total:    %,.2f EGP", order.total()));
    }


    private static Governorate readGovernorate() {
        String text = ask("Governorate");
        try {
            return Governorate.valueOf(text.toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new StoreException("Unknown governorate: " + text);
        }
    }
}