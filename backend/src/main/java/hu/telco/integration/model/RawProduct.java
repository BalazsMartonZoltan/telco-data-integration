package hu.telco.integration.model;

import java.math.BigDecimal;
import java.time.Instant;

public record RawProduct(
        String sku,
        String name,
        String manufacturer,
        BigDecimal price,
        PriceType  priceType,
        BigDecimal vatRate,
        Integer stock,
        String ean,
        Instant updateAt,
        ProductSource source
) {
}
