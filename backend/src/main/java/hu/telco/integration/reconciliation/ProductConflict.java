package hu.telco.integration.reconciliation;

import hu.telco.integration.model.ProductSource;

public record ProductConflict(
        ConflictType type,
        ProductSource firstSource,
        String firstValue,
        ProductSource secondSource,
        String secondValue
) {
}