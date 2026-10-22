package hu.telco.integration.mapper;

import hu.telco.integration.dto.ProductRecordResponse;
import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.reconciliation.ReconciledProduct;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper {

    public ProductResponse toResponse(ReconciledProduct product) {

        var records = product.records().stream()
                .map(result -> {
                    var p = result.product();

                    return new ProductRecordResponse(
                            p.sku(),
                            p.name(),
                            p.manufacturer(),
                            p.finalPriceHuf(),
                            p.stock(),
                            p.ean(),
                            p.updatedAt(),
                            p.source(),
                            result.isValid(),
                            result.errors()
                    );
                })
                .toList();

        return new ProductResponse(
                product.sku(),
                product.isValid(),
                product.hasConflicts(),
                records,
                product.conflicts()
        );
    }
}