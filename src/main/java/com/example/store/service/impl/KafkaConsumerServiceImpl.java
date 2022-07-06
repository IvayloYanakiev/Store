package com.example.store.service.impl;

import com.example.store.model.dtos.OrderRequestDto;
import com.example.store.service.KafkaConsumerService;
import com.example.store.service.ProductService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public final class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final ProductService productService;

    public KafkaConsumerServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    @KafkaListener(topics = "ordersTopic", groupId = "group_id")
    public void consume(String message) {
        //change to logger
        System.out.println("Consumed message" + message);
        productService.processOrder(new Gson().fromJson(message, OrderRequestDto.class));
    }
}
