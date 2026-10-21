package hu.telco.integration.normalization;

import hu.telco.integration.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductNormalizerTest {

    private final ProductNormalizer normalizer =
            new ProductNormalizer();

    @Test
    void shouldNormalizeCsvProduct() {
        RawProduct raw = new RawProduct(
                "P-1001",
                "ACME Widget Mini",
                "ACME",
                new BigDecimal("12700"),
                PriceType.GROSS,
                null,
                15,
                null,
                null,
                ProductSource.CSV
        );

        var result = normalizer.normalize(raw);

        assertEquals("1001", result.sku());
        assertEquals(
                0,
                new BigDecimal("12700")
                        .compareTo(result.finalPriceHuf())
        );
        assertEquals(15, result.stock());
    }

    @Test
    void shouldConvertNetPriceToGross() {
        RawProduct raw = new RawProduct(
                "SKU1001",
                "Acme widget mini",
                "ACME",
                new BigDecimal("10000"),
                PriceType.NET,
                new BigDecimal("0.27"),
                14,
                null,
                null,
                ProductSource.JSON
        );

        var result = normalizer.normalize(raw);

        assertEquals("1001", result.sku());
        assertEquals(
                0,
                new BigDecimal("12700")
                        .compareTo(result.finalPriceHuf())
        );
        assertEquals(14, result.stock());
    }

    @Test
    void shouldPreserveUnknownSkuFormat() {
        RawProduct raw = new RawProduct(
                "SKU-XYZ",
                "Unknown product",
                null,
                null,
                PriceType.NET,
                null,
                null,
                null,
                null,
                ProductSource.JSON
        );

        var result = normalizer.normalize(raw);

        assertEquals("SKU-XYZ", result.sku());
        assertNull(result.finalPriceHuf());
    }
}