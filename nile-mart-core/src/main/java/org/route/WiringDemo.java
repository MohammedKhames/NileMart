package org.route;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;

public class WiringDemo {

    public static void main(String[] args) {
        BigDecimal subtotal = new BigDecimal("4350");

        // 1) Manual wiring using the factory
        ShippingQuoteService fromFactory = ObjectFactory.createShippingQuoteService();
        System.out.println("Factory quote: " + fromFactory.quote(Governorate.GIZA, subtotal));

        // 2) Wiring from beans.xml
        try (var context = new ClassPathXmlApplicationContext("beans.xml")) {
            ShippingQuoteService fromXml = context.getBean(ShippingQuoteService.class);
            System.out.println("XML quote: " + fromXml.quote(Governorate.GIZA, subtotal));
        }
    }
}