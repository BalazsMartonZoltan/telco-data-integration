package hu.telco.integration.model;

import java.math.BigDecimal;
import java.time.Instant;

public record NormalizedProduct(
        String sku,
        String name,
        String manufacturer,
        BigDecimal finalPriceHuf,
        Integer stock,
        String ean,
        Instant updatedAt,
        ProductSource source
) {
}