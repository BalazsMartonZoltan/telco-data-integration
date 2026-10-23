package hu.telco.integration.service;

import hu.telco.integration.dto.ProductRecordResponse;
import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class ProductQueryService {

    public boolean matchesFilter(
            ReconciledProduct product,
            String filter) {

        if (filter == null || filter.isBlank()) {
            return true;
        }

        String query = filter.trim().toLowerCase(Locale.ROOT);

        return contains(product.sku(), query)
                || product.records().stream()
                .map(ValidationResult::product)
                .anyMatch(record ->
                        contains(record.name(), query)
                                || contains(record.manufacturer(), query)
                                || contains(record.sku(), query));
    }

    public Comparator<ProductResponse> comparatorFor(String sort) {
        String field = sort == null || sort.isBlank()
                ? "sku"
                : sort.toLowerCase(Locale.ROOT);

        return switch (field) {
            case "sku" -> Comparator.comparing(
                    ProductResponse::sku,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );

            case "name" -> Comparator.comparing(
                    product -> firstRecord(product).name(),
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );

            case "price" -> Comparator.comparing(
                    product -> firstRecord(product).finalPriceHuf(),
                    Comparator.nullsLast(BigDecimal::compareTo)
            );

            case "stock" -> Comparator.comparing(
                    product -> firstRecord(product).stock(),
                    Comparator.nullsLast(Integer::compareTo)
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported sort field: " + sort
            );
        };
    }

    private boolean contains(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private ProductRecordResponse firstRecord(ProductResponse product) {
        return product.records().get(0);
    }
}