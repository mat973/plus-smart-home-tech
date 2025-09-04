package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;

    @Transactional
    public void createProduct(@Valid NewProductInWarehouseRequest request) {
        repository.findById(request.getProductId())
                .ifPresent(p -> {
                    throw new SpecifiedProductAlreadyInWarehouseException(
                            "Товар уже создан", "Товар с id " + request.getProductId() + " уже создан");
                });

        repository.save(mapper.toEntity(request));
    }

    @Transactional
    public BookedProductsDto checkProductState(@Valid ShoppingCartDto cartDto) {
    }

    @Transactional
    public void addQuantityProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден",
                        "Товар с Id" + request.getProductId() + " не найден"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
    }

    public AddressDto getCurrentWarehouseAddress() {
    }
}
