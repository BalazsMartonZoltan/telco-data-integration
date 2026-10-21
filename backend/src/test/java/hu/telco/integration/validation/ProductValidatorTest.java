package hu.telco.integration.validation;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.model.ProductSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {

    private final ProductValidator validator =
            new ProductValidator();

    @Test
    void shouldAcceptValidProduct() {
        var product = new NormalizedProduct(
                "1001",
                "ACME Widget Mini",
                "ACME",
                new BigDecimal("12700"),
                15,
                null,
                null,
                ProductSource.CSV
        );

        var result = validator.validate(product);

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void shouldDetectMissingFields() {
        var product = new NormalizedProduct(
                null,
                "Unknown product",
                null,
                null,
                null,
                null,
                null,
                ProductSource.JSON
        );

        var result = validator.validate(product);

        assertFalse(result.isValid());
        assertTrue(result.errors().contains(
                ValidationError.MISSING_SKU
        ));
        assertTrue(result.errors().contains(
                ValidationError.MISSING_PRICE
        ));
        assertTrue(result.errors().contains(
                ValidationError.MISSING_STOCK
        ));
        assertTrue(result.errors().contains(
                ValidationError.MISSING_MANUFACTURER
        ));
    }

    @Test
    void shouldDetectNegativeValues() {
        var product = new NormalizedProduct(
                "1002",
                "Test product",
                "ACME",
                new BigDecimal("-100"),
                -5,
                null,
                null,
                ProductSource.JSON
        );

        var result = validator.validate(product);

        assertEquals(2, result.errors().size());
        assertTrue(result.errors().contains(
                ValidationError.NEGATIVE_PRICE
        ));
        assertTrue(result.errors().contains(
                ValidationError.NEGATIVE_STOCK
        ));
    }
}