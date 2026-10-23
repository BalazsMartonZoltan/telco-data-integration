package hu.telco.integration.dto;

import hu.telco.integration.model.ProductSource;
import hu.telco.integration.validation.ValidationError;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductRecordResponse(
        String sku,
        String name,
        String manufacturer,
        BigDecimal finalPriceHuf,
        Integer stock,
        String ean,
        Instant updatedAt,
        ProductSource source,
        boolean valid,
        List<ValidationError> errors
) {
}