package hu.telco.integration.service;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.reconciliation.ProductReconciler;
import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.repository.ProductRepository;
import hu.telco.integration.validation.ProductValidator;
import hu.telco.integration.validation.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductValidator validator;
    private final ProductReconciler reconciler;

    public ProductService(
            ProductRepository repository,
            ProductValidator validator,
            ProductReconciler reconciler) {
        this.repository = repository;
        this.validator = validator;
        this.reconciler = reconciler;
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
}