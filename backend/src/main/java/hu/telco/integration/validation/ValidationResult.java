package hu.telco.integration.validation;

import hu.telco.integration.model.NormalizedProduct;

import java.util.List;

public record ValidationResult(
        NormalizedProduct product,
        List<ValidationError> errors
) {
    public boolean isValid() {
        return errors.isEmpty();
    }
}