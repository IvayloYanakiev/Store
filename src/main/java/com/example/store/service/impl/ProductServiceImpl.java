package com.example.store.service.impl;

import com.example.store.model.dtos.OrderRequestDto;
import com.example.store.model.dtos.ProductLineRequestDto;
import com.example.store.model.dtos.ProductResponseDto;
import com.example.store.model.entities.ProductEntity;
import com.example.store.repository.OrderCacheRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final OrderCacheRepository orderCacheRepository;

    public ProductServiceImpl(ProductRepository productRepository, OrderCacheRepository orderCacheRepository) {
        this.productRepository = productRepository;
        this.orderCacheRepository = orderCacheRepository;
    }

    @Override
    public void loadProducts(List<ProductLineRequestDto> productLineRequestList) {
        Map<Long, Integer> requestProducts = transformProductDtosToMap(productLineRequestList);
        List<Long> requestProductsIds = new ArrayList<>(requestProducts.keySet());

        List<ProductEntity> productEntities = productRepository.findAllById(requestProductsIds);

        //adds the new quantity
        productEntities.forEach(p -> p.setQuantity(p.getQuantity() + requestProducts.get(p.getId())));

        productRepository.saveAll(productEntities);

        checkUnfinishedOrders();
    }

    private void checkUnfinishedOrders() {
        List<OrderRequestDto> unfinishedOrders = orderCacheRepository.findAll();
        Set<Long> unfinishedProductIds = unfinishedOrders.stream()
                .flatMap(orderRequestDto -> orderRequestDto.getProductLines().stream())
                .map(ProductLineRequestDto::getProductId)
                .collect(Collectors.toSet());
        //get order products that are not finished
        Map<Long, ProductEntity> existingProducts = transformProductEntitiesToMap(unfinishedProductIds);
        processUnfinishedOrders(unfinishedOrders, existingProducts);
    }

    private void processUnfinishedOrders(List<OrderRequestDto> unfinishedOrders, Map<Long, ProductEntity> existingProducts) {
        unfinishedOrders
                .forEach(unfinishedOrder -> {
                    List<ProductLineRequestDto> unfinishedOrderProducts = unfinishedOrder.getProductLines();
                    List<ProductEntity> productsToBeProcessed = new LinkedList<>();
                    AtomicBoolean checker = new AtomicBoolean(true);
                    unfinishedOrderProducts.forEach(
                            product -> processUnfinishedProduct(existingProducts, productsToBeProcessed, checker, product)
                    );
                    if (checker.get()) {
                        saveUnfinishedProducts(unfinishedOrder, productsToBeProcessed);
                        //send email to user that the order is finished succesfully
                    }
                });
    }

    private void processUnfinishedProduct(Map<Long, ProductEntity> existingProducts, List<ProductEntity> productsToBeProcessed, AtomicBoolean checker, ProductLineRequestDto product) {
        ProductEntity entity = existingProducts.get(product.getProductId());
        Optional.ofNullable(entity)
                .filter(e -> e.getQuantity() >= product.getQuantity())
                .ifPresentOrElse(e -> {
                    ProductEntity copy = entity.clone();
                    copy.setQuantity(copy.getQuantity() - product.getQuantity());
                    productsToBeProcessed.add(copy);
                }, () -> checker.set(false));
    }

    @Override
    public List<ProductResponseDto> checkQuantity() {

        return null;
    }

    public void processOrder(OrderRequestDto orderRequestDto) {
        Map<Long, Integer> requestProducts = transformProductDtosToMap(orderRequestDto.getProductLines());
        List<Long> requestProductsIds = new ArrayList<>(requestProducts.keySet());

        List<ProductEntity> deepCopyProducts = makeDeepCopyProducts(requestProductsIds);
        AtomicBoolean checker = new AtomicBoolean(true);
        deepCopyProducts.forEach(productEntity -> {
                    Integer requestQuantity = requestProducts.get(productEntity.getId());
                    if (requestQuantity > productEntity.getQuantity()) {
                        checker.set(false);
                    } else {
                        productEntity.setQuantity(productEntity.getQuantity() - requestQuantity);
                    }
                }

        );

        if (checker.get()) {
            productRepository.saveAll(deepCopyProducts);
        } else {
            //put order in redis
            System.out.println("added element to cache: " + orderRequestDto);
            orderCacheRepository.save(orderRequestDto);
        }
    }

    private List<ProductEntity> makeDeepCopyProducts(List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(ProductEntity::clone)
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> transformProductDtosToMap(List<ProductLineRequestDto> orderRequestDto) {
        return orderRequestDto.stream()
                .collect(Collectors
                        .toMap(
                                ProductLineRequestDto::getProductId,
                                ProductLineRequestDto::getQuantity
                        ));
    }

    private Map<Long, ProductEntity> transformProductEntitiesToMap(Set<Long> unfinishedProductIds) {
        return productRepository.findAllById(unfinishedProductIds).stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
    }

    private void saveUnfinishedProducts(OrderRequestDto unfinishedOrder, List<ProductEntity> productsToBeProcessed) {
        productRepository.saveAll(productsToBeProcessed);
        orderCacheRepository.delete(unfinishedOrder);
    }
}
