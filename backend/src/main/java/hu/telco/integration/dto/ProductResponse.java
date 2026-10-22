package hu.telco.integration.dto;

import hu.telco.integration.reconciliation.ProductConflict;

import java.util.List;

public record ProductResponse(
        String sku,
        boolean valid,
        boolean hasConflicts,
        List<ProductRecordResponse> records,
        List<ProductConflict> conflicts
) {
}