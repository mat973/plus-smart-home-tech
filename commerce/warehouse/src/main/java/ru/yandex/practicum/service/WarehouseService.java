package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.feign.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(isolation = Isolation.READ_COMMITTED)
public class WarehouseService {

    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;
    private final BookingRepository bookingRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Transactional
    public void createProduct(@Valid NewProductInWarehouseRequest request) {
        repository.findById(request.getProductId())
                .ifPresent(p -> {
                    throw new SpecifiedProductAlreadyInWarehouseException(
                            "Товар уже создан",
                            "Товар с id " + request.getProductId() + " уже создан"
                    );
                });
        WarehouseProduct product = mapper.toEntity(request);
        repository.save(product);
        updateProductQuantityInShoppingStore(product);
    }


    @Transactional(readOnly = true)
    public BookedProductsDto checkProductState(ShoppingCartDto cartDto) {
        Map<UUID, Long> products = cartDto.getProducts();
        List<WarehouseProduct> warehouseProducts = repository.findAllById(products.keySet());

        if (warehouseProducts.size() != products.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не все товары найдены на складе",
                    "Некоторые товары отсутствуют в БД склада"
            );
        }

        for (WarehouseProduct p : warehouseProducts) {
            long requiredQty = products.get(p.getProductId());
            if (p.getQuantity() < requiredQty) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара",
                        "Товара с id " + p.getProductId() + " доступно " + p.getQuantity()
                );
            }
        }

        return getBookedProducts(warehouseProducts, products);
    }


    @Transactional
    public void addQuantityProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товар не найден",
                        "Товар с Id " + request.getProductId() + " не найден"
                ));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        updateProductQuantityInShoppingStore(product);
    }


    @Transactional
    public void shippedProductToDelivery(ShippedToDeliveryRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException(
                        "Booking не найден для orderId=" + request.getOrderId()
                ));
        booking.setDeliveryId(request.getDeliveryId());
        log.info("Заказ {} отправлен в доставку {}", request.getOrderId(), request.getDeliveryId());
    }


    @Transactional
    public void returnedProduct(Map<UUID, Long> returnedProducts) {
        List<WarehouseProduct> products = repository.findAllById(returnedProducts.keySet());
        for (WarehouseProduct p : products) {
            Long addQty = returnedProducts.get(p.getProductId());
            if (addQty != null && addQty > 0) {
                p.setQuantity(p.getQuantity() + addQty);
                updateProductQuantityInShoppingStore(p);
            }
        }
    }


    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {

        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setProducts(request.getProducts());
        cartDto.setShoppingCartId(request.getOrderId());


        BookedProductsDto booked = checkProductState(cartDto);


        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            "Товар не найден",
                            "Товар с id " + entry.getKey() + " отсутствует"
                    ));
            product.setQuantity(product.getQuantity() - entry.getValue());
            updateProductQuantityInShoppingStore(product);
        }


        Booking newBooking = Booking.builder()
                .shoppingCartId(request.getOrderId())
                .products(request.getProducts())
                .deliveryWeight(booked.getDeliveryWight())
                .deliveryVolume(booked.getDeliveryVolume())
                .fragile(booked.getFragile())
                .build();
        bookingRepository.save(newBooking);

        return booked;
    }


    public AddressDto getCurrentWarehouseAddress() {
        AddressDto dto = new AddressDto();
        dto.setCountry(CURRENT_ADDRESS);
        dto.setCity(CURRENT_ADDRESS);
        dto.setStreet(CURRENT_ADDRESS);
        dto.setHouse(CURRENT_ADDRESS);
        dto.setFlat(CURRENT_ADDRESS);
        return dto;
    }


    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> products, Map<UUID, Long> quantities) {
        double totalWeight = products.stream()
                .mapToDouble(p -> p.getWeight() * quantities.get(p.getProductId()))
                .sum();
        double totalVolume = products.stream()
                .mapToDouble(p -> p.getWidth() * p.getHeight() * p.getDepth() * quantities.get(p.getProductId()))
                .sum();
        boolean hasFragile = products.stream().anyMatch(WarehouseProduct::isFragile);

        return BookedProductsDto.builder()
                .deliveryWight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long qty = product.getQuantity();

        if (qty == 0) quantityState = QuantityState.ENDED;
        else if (qty < 10) quantityState = QuantityState.ENOUGH;
        else if (qty < 100) quantityState = QuantityState.FEW;
        else quantityState = QuantityState.MANY;

        shoppingStoreClient.changeQuantityState(productId, quantityState);
    }


    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        Booking booking = bookingRepository.findByOrderId(deliveryRequest.getOrderId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе.", "Ошибка"));
        booking.setDeliveryId(deliveryRequest.getDeliveryId());
    }

}
