package hu.telco.integration.controller;

import hu.telco.integration.reconciliation.ReconciledProduct;
import hu.telco.integration.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ReconciledProduct> getProducts() {
        return productService.findAll();
    }
}