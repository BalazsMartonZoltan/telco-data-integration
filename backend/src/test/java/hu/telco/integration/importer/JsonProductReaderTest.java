package hu.telco.integration.importer;

import hu.telco.integration.model.PriceType;
import hu.telco.integration.model.ProductSource;
import hu.telco.integration.model.RawProduct;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonProductReaderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonProductReader reader =
            new JsonProductReader(objectMapper);

    @Test
    void shouldReadJsonProducts() throws Exception {
        String json = """
                [
                  {
                    "id": "SKU1001",
                    "name": "Acme widget mini",
                    "netPrice": 10000,
                    "vatRate": 0.27,
                    "quantityAvailable": 14,
                    "manufacturer": "ACME",
                    "ean": "5991234567890",
                    "updatedAt": "2026-10-20T14:30:00Z"
                  }
                ]
                """;

        var input = new ByteArrayInputStream(
                json.getBytes(StandardCharsets.UTF_8)
        );

        List<RawProduct> products = reader.read(input);

        assertEquals(1, products.size());

        var product = products.get(0);

        assertEquals("SKU1001", product.sku());
        assertEquals(new BigDecimal("10000"), product.price());
        assertEquals(new BigDecimal("0.27"), product.vatRate());
        assertEquals(14, product.stock());
        assertEquals(PriceType.NET, product.priceType());
        assertEquals(ProductSource.JSON, product.source());
        assertEquals(
                Instant.parse("2026-10-20T14:30:00Z"),
                product.updatedAt()
        );
    }
}