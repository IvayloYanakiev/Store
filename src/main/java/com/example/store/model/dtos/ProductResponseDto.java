package com.example.store.model.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductResponseDto extends AbstractProduct {
    @Min(0)
    private Long outOfStockQuantity;
}
