package hu.telco.integration.importer.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JsonProductDto(
        String id,
        String name,
        BigDecimal netPrice,
        BigDecimal vatRate,
        Integer quantityAvailable,
        String manufacturer,
        String ean,
        Instant updatedAt
) {
}