package hu.telco.integration.service;

import hu.telco.integration.importer.CsvProductReader;
import hu.telco.integration.importer.JsonProductReader;
import hu.telco.integration.model.RawProduct;
import hu.telco.integration.normalization.ProductNormalizer;
import hu.telco.integration.reconciliation.ProductReconciler;
import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.validation.ProductValidator;
import hu.telco.integration.validation.ValidationResult;
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
    private final ProductValidator validator;
    private final ProductReconciler reconciler;

    public ProductImportService(
            CsvProductReader csvReader,
            JsonProductReader jsonReader,
            ProductNormalizer normalizer,
            ProductValidator validator,
            ProductReconciler reconciler) {

        this.csvReader = csvReader;
        this.jsonReader = jsonReader;
        this.normalizer = normalizer;
        this.validator = validator;
        this.reconciler = reconciler;
    }

    public List<ReconciledProduct> importProducts() throws IOException {
        List<RawProduct> rawProducts = readProducts();

        List<ValidationResult> validatedProducts = rawProducts.stream()
                .map(normalizer::normalize)
                .map(validator::validate)
                .toList();

        return reconciler.reconcile(validatedProducts);
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