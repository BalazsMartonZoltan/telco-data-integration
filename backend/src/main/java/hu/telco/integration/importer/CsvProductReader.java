package hu.telco.integration.importer;

import hu.telco.integration.model.PriceType;
import hu.telco.integration.model.ProductSource;
import hu.telco.integration.model.RawProduct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProductReader {

    public List<RawProduct> read(InputStream input) throws IOException {
        List<RawProduct> products = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .get()
                    .parse(reader);

            for (CSVRecord record : parser) {
                products.add(new RawProduct(
                        nullable(record.get("sku")),
                        nullable(record.get("product_name")),
                        nullable(record.get("brand")),
                        parsePrice(record.get("gross_price_huf")),
                        PriceType.GROSS,
                        null,
                        parseStock(record.get("stock_qty")),
                        null,
                        null,
                        ProductSource.CSV
                ));
            }
        }

        return products;
    }

    private String nullable(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private BigDecimal parsePrice(String value) {
        String cleaned = nullable(value);

        if (cleaned == null) {
            return null;
        }

        return new BigDecimal(cleaned.replace(" ", ""));
    }

    private Integer parseStock(String value) {
        String cleaned = nullable(value);

        return cleaned == null
                ? null
                : Integer.valueOf(cleaned);
    }
}
