package hu.telco.integration.repository;

import hu.telco.integration.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<ProductEntity, Long> {
}