package hu.telco.integration.importer;

import hu.telco.integration.importer.dto.JsonProductDto;
import hu.telco.integration.model.PriceType;
import hu.telco.integration.model.ProductSource;
import hu.telco.integration.model.RawProduct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class JsonProductReader {

    private final ObjectMapper objectMapper;

    public JsonProductReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<RawProduct> read(InputStream input)
            throws IOException {

        List<JsonProductDto> products = objectMapper.readValue(
                input,
                new TypeReference<>() {}
        );

        return products.stream()
                .map(this::toRawProduct)
                .toList();
    }

    private RawProduct toRawProduct(JsonProductDto dto) {
        return new RawProduct(
                dto.id(),
                dto.name(),
                dto.manufacturer(),
                dto.netPrice(),
                PriceType.NET,
                dto.vatRate(),
                dto.quantityAvailable(),
                dto.ean(),
                dto.updatedAt(),
                ProductSource.JSON
        );
    }
}