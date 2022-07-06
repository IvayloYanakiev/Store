package com.example.store.model.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractProduct {
    @NotNull
    private Long productId;
}
