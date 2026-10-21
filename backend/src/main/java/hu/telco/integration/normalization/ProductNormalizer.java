package hu.telco.integration.normalization;

import hu.telco.integration.model.NormalizedProduct;
import hu.telco.integration.model.PriceType;
import hu.telco.integration.model.RawProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductNormalizer {

    private static final BigDecimal DEFAULT_VAT_RATE =
            new BigDecimal("0.27");

    public NormalizedProduct normalize(RawProduct raw) {
        return new NormalizedProduct(
                normalizeSku(raw.sku()),
                trimToNull(raw.name()),
                trimToNull(raw.manufacturer()),
                calculateFinalPrice(raw),
                raw.stock(),
                trimToNull(raw.ean()),
                raw.updatedAt(),
                raw.source()
        );
    }

    private String normalizeSku(String sku) {
        String value = trimToNull(sku);

        if (value == null) {
            return null;
        }

        if (value.matches("(?i)P-\\d+")) {
            return value.substring(2);
        }

        if (value.matches("(?i)SKU\\d+")) {
            return value.substring(3);
        }

        return value;
    }

    private BigDecimal calculateFinalPrice(RawProduct raw) {
        if (raw.price() == null) {
            return null;
        }

        if (raw.priceType() == PriceType.GROSS) {
            return raw.price();
        }

        if (raw.priceType() == PriceType.NET) {
            BigDecimal vatRate = raw.vatRate() != null
                    ? raw.vatRate()
                    : DEFAULT_VAT_RATE;

            return raw.price()
                    .multiply(BigDecimal.ONE.add(vatRate))
                    .setScale(0, RoundingMode.HALF_UP);
        }

        return null;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}