package hu.telco.integration.reconciliation;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.model.ProductSource;
import hu.telco.integration.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductReconcilerTest {

    private final ProductReconciler reconciler =
            new ProductReconciler();

    @Test
    void shouldDetectStockConflictWithoutInvalidatingProduct() {

        var csv = new NormalizedProduct(
                "1001",
                "ACME Widget Mini",
                "ACME",
                new BigDecimal("12700"),
                15,
                null,
                null,
                ProductSource.CSV
        );

        var json = new NormalizedProduct(
                "1001",
                "Acme widget mini",
                "ACME",
                new BigDecimal("12700"),
                14,
                null,
                null,
                ProductSource.JSON
        );

        var result = reconciler.reconcile(List.of(
                new ValidationResult(csv, List.of()),
                new ValidationResult(json, List.of())
        ));

        assertEquals(1, result.size());

        ReconciledProduct product = result.get(0);

        assertEquals("1001", product.sku());
        assertEquals(2, product.records().size());

        assertTrue(product.isValid());
        assertTrue(product.hasConflicts());

        assertEquals(1, product.conflicts().size());
        assertEquals(
                ConflictType.STOCK_MISMATCH,
                product.conflicts().get(0).type()
        );
    }

    @Test
    void shouldKeepRecordsWithoutSkuSeparate() {

        var first = new NormalizedProduct(
                null, "Product A", "ACME",
                BigDecimal.TEN, 5,
                null, null, ProductSource.CSV
        );

        var second = new NormalizedProduct(
                null, "Product B", "ACME",
                BigDecimal.TEN, 3,
                null, null, ProductSource.JSON
        );

        var result = reconciler.reconcile(List.of(
                new ValidationResult(first, List.of()),
                new ValidationResult(second, List.of())
        ));

        assertEquals(2, result.size());
    }
}