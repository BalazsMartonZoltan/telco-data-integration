package hu.telco.integration.service;

import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.dto.ProductSort;
import hu.telco.integration.entity.ProductEntity;
import hu.telco.integration.mapper.ProductResponseMapper;
import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.reconciliation.ProductReconciler;
import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.repository.ProductRepository;
import hu.telco.integration.validation.ProductValidator;
import hu.telco.integration.validation.ValidationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductValidator validator;
    private final ProductReconciler reconciler;
    private final ProductResponseMapper responseMapper;
    private final ProductQueryService queryService;

    public ProductService(
            ProductRepository repository,
            ProductValidator validator,
            ProductReconciler reconciler,
            ProductResponseMapper responseMapper,
            ProductQueryService queryService) {

        this.repository = repository;
        this.validator = validator;
        this.reconciler = reconciler;
        this.responseMapper = responseMapper;
        this.queryService = queryService;
    }

    @Transactional(readOnly = true)
    public List<ReconciledProduct> findAll() {
        List<ValidationResult> validated = repository.findAll().stream()
                .map(this::toNormalizedProduct)
                .map(validator::validate)
                .toList();

        return reconciler.reconcile(validated);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(
            String filter,
            ProductSort sort,
            boolean onlyValid) {

        return findAll().stream()
                .filter(product -> !onlyValid || product.isValid())
                .filter(product -> queryService.matchesFilter(product, filter))
                .map(responseMapper::toResponse)
                .sorted(queryService.getComparator(sort))
                .toList();
    }

    private NormalizedProduct toNormalizedProduct(ProductEntity entity) {
        return new NormalizedProduct(
                entity.getSku(),
                entity.getName(),
                entity.getManufacturer(),
                entity.getFinalPriceHuf(),
                entity.getStock(),
                entity.getEan(),
                entity.getUpdatedAt(),
                entity.getSource()
        );
    }
}