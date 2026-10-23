package hu.telco.integration.config;

import hu.telco.integration.dto.ProductSort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductSortConverterTest {

    private final ProductSortConverter converter =
            new ProductSortConverter();

    @Test
    void shouldConvertSortCaseInsensitively() {
        assertEquals(ProductSort.PRICE, converter.convert("price"));
        assertEquals(ProductSort.PRICE, converter.convert("PRICE"));
        assertEquals(ProductSort.PRICE, converter.convert("Price"));
    }

    @Test
    void shouldRejectUnsupportedSort() {
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert("unknown")
        );
    }
}