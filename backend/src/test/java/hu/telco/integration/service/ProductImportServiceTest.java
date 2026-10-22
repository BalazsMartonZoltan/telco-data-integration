package hu.telco.integration.service;

import hu.telco.integration.reconciliation.ConflictType;
import hu.telco.integration.reconciliation.ReconciledProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductImportServiceTest {

    @Autowired
    private ProductImportService importService;

    @Test
    void shouldImportAndReconcileProducts() throws Exception {

        List<ReconciledProduct> products = importService.importProducts();

        assertFalse(products.isEmpty());

        ReconciledProduct product1001 = products.stream()
                .filter(p -> "1001".equals(p.sku()))
                .findFirst()
                .orElseThrow();

        assertEquals(2, product1001.records().size());

        assertTrue(product1001.isValid());

        assertTrue(product1001.hasConflicts());

        assertTrue(product1001.conflicts().stream()
                .anyMatch(conflict ->
                        conflict.type() == ConflictType.STOCK_MISMATCH
                ));
    }
}