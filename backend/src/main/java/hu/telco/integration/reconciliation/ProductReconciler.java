package hu.telco.integration.reconciliation;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProductReconciler {

    public List<ReconciledProduct> reconcile(
            List<ValidationResult> products) {

        Map<String, List<ValidationResult>> grouped =
                groupProductsBySku(products);

        List<ReconciledProduct> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            String sku = entry.getKey();
            List<ValidationResult> records = entry.getValue();

            if (sku.isBlank()) {
                for (var record : records) {
                    result.add(new ReconciledProduct(
                            null,
                            List.of(record),
                            List.of()
                    ));
                }
            } else {
                result.add(new ReconciledProduct(
                        sku,
                        List.copyOf(records),
                        findConflicts(records)
                ));
            }
        }

        return List.copyOf(result);
    }

    private Map<String, List<ValidationResult>> groupProductsBySku(
            List<ValidationResult> products) {

        return products.stream()
                .collect(Collectors.groupingBy(
                        result -> skuOrEmpty(result.product().sku()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private String skuOrEmpty(String sku) {
        return sku == null || sku.isBlank() ? "" : sku;
    }

    private List<ProductConflict> findConflicts(
            List<ValidationResult> records) {

        List<ProductConflict> conflicts = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            for (int j = i + 1; j < records.size(); j++) {
                compareProducts(
                        records.get(i).product(),
                        records.get(j).product(),
                        conflicts
                );
            }
        }

        return List.copyOf(conflicts);
    }

    private void compareProducts(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        detectDuplicate(first, second, conflicts);
        detectStockMismatch(first, second, conflicts);
        detectPriceMismatch(first, second, conflicts);
        detectNameMismatch(first, second, conflicts);
        detectManufacturerMismatch(first, second, conflicts);
    }

    private void detectDuplicate(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        if (first.source() == second.source()) {
            addConflict(
                    conflicts,
                    ConflictType.DUPLICATE_SKU,
                    first,
                    second,
                    first.sku(),
                    second.sku()
            );
        }
    }

    private void detectStockMismatch(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        if (first.stock() != null
                && second.stock() != null
                && !first.stock().equals(second.stock())) {

            addConflict(
                    conflicts,
                    ConflictType.STOCK_MISMATCH,
                    first,
                    second,
                    first.stock(),
                    second.stock()
            );
        }
    }

    private void detectPriceMismatch(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        BigDecimal firstPrice = first.finalPriceHuf();
        BigDecimal secondPrice = second.finalPriceHuf();

        if (firstPrice != null
                && secondPrice != null
                && firstPrice.compareTo(secondPrice) != 0) {

            addConflict(
                    conflicts,
                    ConflictType.PRICE_MISMATCH,
                    first,
                    second,
                    firstPrice,
                    secondPrice
            );
        }
    }

    private void detectNameMismatch(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        if (hasTextMismatch(first.name(), second.name())) {
            addConflict(
                    conflicts,
                    ConflictType.NAME_MISMATCH,
                    first,
                    second,
                    first.name(),
                    second.name()
            );
        }
    }

    private void detectManufacturerMismatch(
            NormalizedProduct first,
            NormalizedProduct second,
            List<ProductConflict> conflicts) {

        if (hasTextMismatch(
                first.manufacturer(),
                second.manufacturer())) {

            addConflict(
                    conflicts,
                    ConflictType.MANUFACTURER_MISMATCH,
                    first,
                    second,
                    first.manufacturer(),
                    second.manufacturer()
            );
        }
    }

    private boolean hasTextMismatch(String first, String second) {
        return first != null
                && second != null
                && !first.trim().equalsIgnoreCase(second.trim());
    }

    private void addConflict(
            List<ProductConflict> conflicts,
            ConflictType type,
            NormalizedProduct first,
            NormalizedProduct second,
            Object firstValue,
            Object secondValue) {

        conflicts.add(new ProductConflict(
                type,
                first.source(),
                Objects.toString(firstValue, null),
                second.source(),
                Objects.toString(secondValue, null)
        ));
    }
}