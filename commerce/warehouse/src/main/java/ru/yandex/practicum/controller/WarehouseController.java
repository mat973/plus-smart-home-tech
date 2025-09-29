package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.feign.client.WarehouseClient;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController implements WarehouseClient {
    private final WarehouseService service;

    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.createProduct(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductState(@RequestBody @Valid ShoppingCartDto cartDto) {
        return service.checkProductState(cartDto);
    }

    @PostMapping("/add")
    public void addQuantityProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        service.addQuantityProductToWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getCurrentWarehouseAddress() {
        return service.getCurrentWarehouseAddress();
    }

    @PostMapping("/shipped")
    public void shippedProductToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        service.shippedProductToDelivery(request);
    }

    @PostMapping("/return")
    public void returnedProduct(@RequestBody Map<UUID, Long> returnedProducts) {
        service.returnedProduct(returnedProducts);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        try {
            log.info("Передать товары в доставку {}", deliveryRequest);
            service.shippedToDelivery(deliveryRequest);
        } catch (Exception e) {
            log.error("Ошибка передачи товаров в доставку.");
            throw e;
        }
    }
}
