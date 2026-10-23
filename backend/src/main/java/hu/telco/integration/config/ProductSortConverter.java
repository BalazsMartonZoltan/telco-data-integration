package hu.telco.integration.config;

import hu.telco.integration.dto.ProductSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ProductSortConverter implements Converter<String, ProductSort> {

    @Override
    public ProductSort convert(String source) {
        return ProductSort.valueOf(
                source.trim().toUpperCase(Locale.ROOT)
        );
    }
}