package hu.telco.integration.config;

import hu.telco.integration.entity.ProductEntity;
import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.repository.ProductRepository;
import hu.telco.integration.service.ProductImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataInitializer implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(ProductDataInitializer.class);

    private final ProductImportService importService;
    private final ProductRepository repository;

    public ProductDataInitializer(
            ProductImportService importService,
            ProductRepository repository) {

        this.importService = importService;
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        var entities = importService.importProducts().stream()
                .map(this::toEntity)
                .toList();

        repository.saveAll(entities);

        log.info("Imported {} product records", entities.size());
    }

    private ProductEntity toEntity(NormalizedProduct product) {
        return new ProductEntity(
                product.sku(),
                product.name(),
                product.manufacturer(),
                product.finalPriceHuf(),
                product.stock(),
                product.ean(),
                product.updatedAt(),
                product.source()
        );
    }
}