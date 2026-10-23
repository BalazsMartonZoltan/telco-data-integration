package hu.telco.integration.service;

import hu.telco.integration.importer.CsvProductReader;
import hu.telco.integration.importer.JsonProductReader;
import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.model.RawProduct;
import hu.telco.integration.normalization.ProductNormalizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportService {

    private static final String CSV_RESOURCE =
            "data/incompatible_feeds_products.csv";

    private static final String JSON_RESOURCE =
            "data/incompatible_feeds_products.json";

    private final CsvProductReader csvReader;
    private final JsonProductReader jsonReader;
    private final ProductNormalizer normalizer;

    public ProductImportService(
            CsvProductReader csvReader,
            JsonProductReader jsonReader,
            ProductNormalizer normalizer) {

        this.csvReader = csvReader;
        this.jsonReader = jsonReader;
        this.normalizer = normalizer;
    }

    public List<NormalizedProduct> importProducts() throws IOException {
        return readProducts().stream()
                .map(normalizer::normalize)
                .toList();
    }

    private List<RawProduct> readProducts() throws IOException {
        List<RawProduct> products = new ArrayList<>();

        try (var input = new ClassPathResource(CSV_RESOURCE).getInputStream()) {
            products.addAll(csvReader.read(input));
        }

        try (var input = new ClassPathResource(JSON_RESOURCE).getInputStream()) {
            products.addAll(jsonReader.read(input));
        }

        return products;
    }
}