package com.example.store.service;

import com.example.store.model.dtos.OrderRequestDto;
import com.example.store.model.dtos.ProductLineRequestDto;
import com.example.store.model.dtos.ProductResponseDto;

import java.util.List;

public interface ProductService {

    void loadProducts(List<ProductLineRequestDto> productLineRequestDto);

    void processOrder(OrderRequestDto orderRequestDto);

    List<ProductResponseDto> checkQuantity();
}
