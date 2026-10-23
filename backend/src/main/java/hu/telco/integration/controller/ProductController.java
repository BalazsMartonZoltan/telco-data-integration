package hu.telco.integration.controller;

import hu.telco.integration.dto.ProductQuery;
import hu.telco.integration.dto.ProductResponse;
import hu.telco.integration.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get reconciled products")
    public List<ProductResponse> getProducts(
            @ParameterObject @ModelAttribute ProductQuery query) {

        return productService.getProducts(
                query.filter(),
                query.sort(),
                query.onlyValid()
        );
    }
}