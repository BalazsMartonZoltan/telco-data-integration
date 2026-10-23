package hu.telco.integration.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductQuery(

        @Parameter(description = "Search by SKU, name or manufacturer")
        String filter,

        @Parameter(description = "Sort products by the selected field")
        @Schema(defaultValue = "SKU")
        ProductSort sort,

        @Parameter(description = "Return only valid products")
        Boolean onlyValid

) {
    public ProductQuery {
        sort = sort == null ? ProductSort.SKU : sort;
        onlyValid = onlyValid == null ? false : onlyValid;
    }
}