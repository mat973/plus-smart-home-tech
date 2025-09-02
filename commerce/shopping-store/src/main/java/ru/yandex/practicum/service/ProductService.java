package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.producte.ProductCategory;
import ru.yandex.practicum.dto.producte.ProductDto;
import ru.yandex.practicum.dto.producte.SetProductQuantityStateRequest;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductNotFoundException;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public List<ProductDto> getProductPagaeble(ProductCategory category, Pageable pageable) {
        Page<Product> products = repository.findByCategory(category, pageable);
        return products.map(mapper::toProductDto).stream().toList();
    }

    @Transactional
    public ProductDto createProduct(@Valid ProductDto productDto) {
        return  mapper.toProductDto(repository.save(mapper.toProduct(productDto)));

    }

    @Transactional
    public ProductDto updateProduct(@Valid ProductDto productDto) {
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productDto.getProductId() + " не найдено",
                        "Product not found", HttpStatus.NOT_FOUND.toString(), new RuntimeException("Underlying cause")));
        mapper.updateEntityFromDto(productDto, product);
        return mapper.toProductDto(product);
    }

    @Transactional
    public boolean removeProduct(UUID productId) {
        if (!repository.existsById(productId)) {
            throw new ProductNotFoundException("Продукта с id" + productId + " не найдено",
                    "Product not found", HttpStatus.NOT_FOUND.toString(), new RuntimeException("Underlying cause"));
        }
        repository.deleteById(productId);
        return true;
    }

    @Transactional
    public boolean changeQuantityState(@Valid SetProductQuantityStateRequest productDto) {
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productDto.getProductId() + " не найдено",
                        "Product not found", HttpStatus.NOT_FOUND.toString(), new RuntimeException("Underlying cause")));
        product.setQuantityState(productDto.getQuantityState());
        return true;
    }

    public ProductDto getProductById(UUID productId) {
        return mapper.toProductDto(repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productId + " не найдено",
                "Product not found", HttpStatus.NOT_FOUND.toString(), new RuntimeException("Underlying cause"))));
    }
}
