package hu.telco.integration.entity;

import hu.telco.integration.model.ProductSource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;
    private String manufacturer;

    @Column(precision = 19, scale = 2)
    private BigDecimal finalPriceHuf;

    private Integer stock;
    private String ean;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private ProductSource source;

    protected ProductEntity() {
    }

    public ProductEntity(
            String sku,
            String name,
            String manufacturer,
            BigDecimal finalPriceHuf,
            Integer stock,
            String ean,
            Instant updatedAt,
            ProductSource source) {

        this.sku = sku;
        this.name = name;
        this.manufacturer = manufacturer;
        this.finalPriceHuf = finalPriceHuf;
        this.stock = stock;
        this.ean = ean;
        this.updatedAt = updatedAt;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public BigDecimal getFinalPriceHuf() {
        return finalPriceHuf;
    }

    public Integer getStock() {
        return stock;
    }

    public String getEan() {
        return ean;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ProductSource getSource() {
        return source;
    }
}