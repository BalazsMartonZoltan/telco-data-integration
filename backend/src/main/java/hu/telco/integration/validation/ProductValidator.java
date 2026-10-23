package hu.telco.integration.validation;

import hu.telco.integration.model.NormalizedProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductValidator {

    public ValidationResult validate(NormalizedProduct product) {
        List<ValidationError> errors = new ArrayList<>();

        if (isBlank(product.sku())) {
            errors.add(ValidationError.MISSING_SKU);
        }

        if (isBlank(product.name())) {
            errors.add(ValidationError.MISSING_NAME);
        }

        if (product.finalPriceHuf() == null) {
            errors.add(ValidationError.MISSING_PRICE);
        } else if (product.finalPriceHuf()
                .compareTo(BigDecimal.ZERO) < 0) {
            errors.add(ValidationError.NEGATIVE_PRICE);
        }

        if (product.stock() == null) {
            errors.add(ValidationError.MISSING_STOCK);
        } else if (product.stock() < 0) {
            errors.add(ValidationError.NEGATIVE_STOCK);
        }

        if (isBlank(product.manufacturer())) {
            errors.add(ValidationError.MISSING_MANUFACTURER);
        }

        return new ValidationResult(
                product,
                List.copyOf(errors)
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}