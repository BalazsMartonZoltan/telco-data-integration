package hu.telco.integration.reconciliation;

import hu.telco.integration.validation.ValidationResult;

import java.util.List;

public record ReconciledProduct(
        String sku,
        List<ValidationResult> records,
        List<ProductConflict> conflicts
) {
    public boolean isValid() {
        return records.stream()
                .allMatch(ValidationResult::isValid);
    }

    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }
}