package hu.telco.integration.service;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.model.ProductSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductImportServiceTest {

    @Autowired
    private ProductImportService importService;

    @Test
    void shouldImportNormalizedProducts() throws IOException {
        List<NormalizedProduct> products = importService.importProducts();

        assertFalse(products.isEmpty());

        List<NormalizedProduct> product1001 = products.stream()
                .filter(product -> "1001".equals(product.sku()))
                .toList();

        assertEquals(2, product1001.size());

        assertTrue(product1001.stream()
                .anyMatch(product -> product.source() == ProductSource.CSV));

        assertTrue(product1001.stream()
                .anyMatch(product -> product.source() == ProductSource.JSON));
    }

    @Test
    void shouldCalculateGrossPriceForJsonProducts() throws IOException {
        List<NormalizedProduct> products = importService.importProducts();

        NormalizedProduct jsonProduct = products.stream()
                .filter(product -> "1001".equals(product.sku()))
                .filter(product -> product.source() == ProductSource.JSON)
                .findFirst()
                .orElseThrow();

        assertEquals(
                0,
                new BigDecimal("12700")
                        .compareTo(jsonProduct.finalPriceHuf())
        );
    }
}