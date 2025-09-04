package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.CartItemId;
import ru.yandex.practicum.repository.CartRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository repository;
    private final ShoppingCartMapper mapper;

    @Transactional
    public ShoppingCartDto getCart(String username) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseGet(() -> createNewCartInTransaction(username));
        return mapper.toDto(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cart createNewCartInTransaction(String username) {
        return repository.save(
                Cart.builder()
                        .items(new HashSet<>())
                        .username(username)
                        .build()
        );
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> newProduct) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseGet(() -> createNewCartInTransaction(username));
        if (!cart.isActive()) {
            throw new CartDeactivatedException("Корзина деактивирована", "Корзина для пользователя " + username + " недоступна");
        }
        Map<UUID, CartItem> existingItemsMap = cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        Function.identity()
                ));

        newProduct.forEach((productId, quantity) -> {
            if (existingItemsMap.containsKey(productId)) {
                CartItem existingItem = existingItemsMap.get(productId);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                CartItem newItem = CartItem.builder()
                        .id(new CartItemId(cart.getCartId(), productId))
                        .cart(cart)
                        .quantity(quantity)
                        .build();
                cart.getItems().add(newItem);
            }
        });

        Cart savedCart = repository.save(cart);
        return mapper.toDto(savedCart);
    }

    @Transactional
    public void deleteCart(String username) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));
        cart.setActive(false);
    }

    @Transactional
    public void deleteProductFromCart(String username, List<UUID> productIds) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));

        if (!cart.isActive()) {
            throw new CartDeactivatedException("Корзина деактивирована", "Корзина для пользователя " + username + " недоступна");
        }

        List<CartItem> toRemove = cart.getItems().stream()
                .filter(item -> productIds.contains(item.getId().getProductId()))
                .toList();

        if (toRemove.size() != productIds.size()) {
            throw new NoProductsInShoppingCartException("Некоторые товары не найдены", "В корзине отсутствует часть указанных товаров");
        }

        toRemove.forEach(cart.getItems()::remove);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));

        if (!cart.isActive()) {
            throw new CartDeactivatedException("Корзина деактивирована", "Корзина для пользователя " + username + " недоступна");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Товар не найден",
                        "Товара с Id " + request.getProductId() + " не найдено в корзине"
                ));
        cartItem.setQuantity(request.getNewQuantity());
        return mapper.toDto(cart);
    }
}
