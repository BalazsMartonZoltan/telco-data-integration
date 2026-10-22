package hu.telco.integration.controller;

import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "sku") String sort,
            @RequestParam(defaultValue = "false") boolean onlyValid) {

        return productService.getProducts(filter, sort, onlyValid);
    }
}