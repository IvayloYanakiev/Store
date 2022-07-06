package com.example.store.controller;

import com.example.store.model.dtos.ProductLineRequestDto;
import com.example.store.model.dtos.ProductResponseDto;
import com.example.store.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //checks which products are requested and how many are not in stock
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> checkUnavailableQuantity() {
        return ResponseEntity.ok(productService.checkQuantity());
    }

    //load new products in the store
    @PostMapping
    public ResponseEntity<String> loadProducts(@RequestBody List<ProductLineRequestDto> productLineRequestDto) {
        productService.loadProducts(productLineRequestDto);
        return ResponseEntity.ok().body("Products successfully loaded!");
    }
}
