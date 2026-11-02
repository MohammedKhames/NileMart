package org.route;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingQuoteService {

    private final ShippingCalculator zoneCalculator;
    private final ShippingCalculator freeCalculator;
    private final BigDecimal freeShippingThreshold;

    public ShippingQuoteService(ShippingCalculator zoneCalculator,
                                @Qualifier("freeShipping") ShippingCalculator freeCalculator,
                                @Value("${store.free-shipping-threshold}") BigDecimal freeShippingThreshold) {
        this.zoneCalculator = zoneCalculator;
        this.freeCalculator = freeCalculator;
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public BigDecimal quote(Governorate governorate, BigDecimal subtotal) {
        boolean reachedThreshold = subtotal.compareTo(freeShippingThreshold) >= 0;
        boolean zoneEligible = governorate.getZone().isFreeShippingEligible();

        if (reachedThreshold && zoneEligible) {
            return freeCalculator.calculate(governorate);
        }
        return zoneCalculator.calculate(governorate);
    }
}