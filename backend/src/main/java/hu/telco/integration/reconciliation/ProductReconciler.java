package hu.telco.integration.reconciliation;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.validation.ValidationResult;
import org.jspecify.annotations.NonNull;
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

        Map<String, List<ValidationResult>> grouped = groupProductsBySku(products);

        List<ReconciledProduct> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            List<ValidationResult> records = entry.getValue();

            if (entry.getKey().isBlank()) {
                for (ValidationResult record : records) {
                    result.add(new ReconciledProduct(
                            null,
                            List.of(record),
                            List.of()
                    ));
                }
                continue;
            }

            result.add(new ReconciledProduct(
                    entry.getKey(),
                    List.copyOf(records),
                    findConflicts(records)
            ));
        }

        return result;
    }

    private Map<String, List<ValidationResult>> groupProductsBySku(List<ValidationResult> products) {
        Map<String, List<ValidationResult>> grouped =
                products.stream()
                        .collect(Collectors.groupingBy(
                                result -> {
                                    String sku = result.product().sku();
                                    return sku == null || sku.isBlank()
                                            ? ""
                                            : sku;
                                },
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
        return grouped;
    }

    private List<ProductConflict> findConflicts(
            List<ValidationResult> records) {

        List<ProductConflict> conflicts = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            for (int j = i + 1; j < records.size(); j++) {

                NormalizedProduct first = records.get(i).product();
                NormalizedProduct second = records.get(j).product();

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

                if (first.finalPriceHuf() != null
                        && second.finalPriceHuf() != null
                        && first.finalPriceHuf().compareTo(
                        second.finalPriceHuf()) != 0) {

                    addConflict(
                            conflicts,
                            ConflictType.PRICE_MISMATCH,
                            first,
                            second,
                            first.finalPriceHuf(),
                            second.finalPriceHuf()
                    );
                }

                if (!sameText(first.name(), second.name())) {
                    addConflict(
                            conflicts,
                            ConflictType.NAME_MISMATCH,
                            first,
                            second,
                            first.name(),
                            second.name()
                    );
                }

                if (!sameText(
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
        }

        return List.copyOf(conflicts);
    }

    private boolean sameText(String first, String second) {
        if (first == null || second == null) {
            return true;
        }

        return first.trim().equalsIgnoreCase(second.trim());
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