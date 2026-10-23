package hu.telco.integration.config;

import hu.telco.integration.entity.ProductEntity;
import hu.telco.integration.repository.ProductRepository;
import hu.telco.integration.service.ProductImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataInitializer implements CommandLineRunner {

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

        var reconciledProducts = importService.importProducts();

        var entities = reconciledProducts.stream()
                .flatMap(product -> product.records().stream())
                .map(result -> result.product())
                .map(product -> new ProductEntity(
                        product.sku(),
                        product.name(),
                        product.manufacturer(),
                        product.finalPriceHuf(),
                        product.stock(),
                        product.ean(),
                        product.updatedAt(),
                        product.source()
                ))
                .toList();

        repository.saveAll(entities);

        System.out.println(
                "Imported " + entities.size() + " product records"
        );
    }
}