package org.route;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuditLog {

    @Before("execution(* org.route.CheckoutService.placeOrder(..))")
    public void audit(JoinPoint joinPoint) {
        ShoppingCart cart = (ShoppingCart) joinPoint.getArgs()[0];
        Governorate governorate = (Governorate) joinPoint.getArgs()[1];

        System.out.println("[AUDIT] Placing order for cart #" + cart.getCartNumber()
                + " to " + governorate);
    }
}