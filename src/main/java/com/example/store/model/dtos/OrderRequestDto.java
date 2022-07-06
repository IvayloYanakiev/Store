package com.example.store.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@RedisHash("Order")
public class OrderRequestDto implements Serializable {

    private Long id;

    @JsonProperty("productLines")
    private List<ProductLineRequestDto> productLines;
}
