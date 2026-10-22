package hu.telco.integration.service;

import hu.telco.integration.dto.ProductRecordResponse;
import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.mapper.ProductResponseMapper;
import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.reconciliation.ProductReconciler;
import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.repository.ProductRepository;
import hu.telco.integration.validation.ProductValidator;
import hu.telco.integration.validation.ValidationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductValidator validator;
    private final ProductReconciler reconciler;

    private final ProductResponseMapper responseMapper;

    public ProductService(
            ProductRepository repository,
            ProductValidator validator,
            ProductReconciler reconciler,
            ProductResponseMapper responseMapper) {
        this.repository = repository;
        this.validator = validator;
        this.reconciler = reconciler;
        this.responseMapper = responseMapper;
    }

    public List<ReconciledProduct> findAll() {

        List<ValidationResult> validated = repository.findAll().stream()
                .map(entity -> new NormalizedProduct(
                        entity.getSku(),
                        entity.getName(),
                        entity.getManufacturer(),
                        entity.getFinalPriceHuf(),
                        entity.getStock(),
                        entity.getEan(),
                        entity.getUpdatedAt(),
                        entity.getSource()
                ))
                .map(validator::validate)
                .toList();

        return reconciler.reconcile(validated);
    }

    public List<ProductResponse> getProducts(
            String filter,
            String sort,
            boolean onlyValid) {

        var products = findAll().stream()
                .filter(product -> !onlyValid || product.isValid())
                .filter(product -> matchesFilter(product, filter))
                .map(responseMapper::toResponse);

        return products
                .sorted(getComparator(sort))
                .toList();
    }

    private boolean matchesFilter(
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

    private boolean contains(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private Comparator<ProductResponse> getComparator(String sort) {

        if (sort == null || sort.isBlank()) {
            return Comparator.comparing(
                    ProductResponse::sku,
                    Comparator.nullsLast(String::compareTo)
            );
        }

        return switch (sort.toLowerCase(Locale.ROOT)) {
            case "name" -> Comparator.comparing(
                    p -> firstRecord(p).name(),
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );

            case "price" -> Comparator.comparing(
                    p -> firstRecord(p).finalPriceHuf(),
                    Comparator.nullsLast(BigDecimal::compareTo)
            );

            case "stock" -> Comparator.comparing(
                    p -> firstRecord(p).stock(),
                    Comparator.nullsLast(Integer::compareTo)
            );

            case "sku" -> Comparator.comparing(
                    ProductResponse::sku,
                    Comparator.nullsLast(String::compareTo)
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported sort field: " + sort
            );
        };
    }

    private ProductRecordResponse firstRecord(ProductResponse product) {
        return product.records().get(0);
    }
}