package hu.telco.integration.importer;

import hu.telco.integration.model.PriceType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CsvProductReaderTest {

    private final CsvProductReader reader = new CsvProductReader();

    @Test
    void shouldReadCsvProducts() throws Exception {
        String csv = """
                sku,product_name,gross_price_huf,stock_qty,brand
                P-1001,ACME Widget Mini,12 700,15,ACME
                P-1002,FixIt Tool,25400,,FixIt
                """;

        var input = new ByteArrayInputStream(
                csv.getBytes(StandardCharsets.UTF_8)
        );

        var products = reader.read(input);

        assertEquals(2, products.size());

        var first = products.get(0);

        assertEquals("P-1001", first.sku());
        assertEquals("ACME Widget Mini", first.name());
        assertEquals(new BigDecimal("12700"), first.price());
        assertEquals(15, first.stock());
        assertEquals(PriceType.GROSS, first.priceType());

        assertNull(products.get(1).stock());
    }
}