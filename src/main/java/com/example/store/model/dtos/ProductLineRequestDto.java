package com.example.store.model.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductLineRequestDto extends AbstractProduct implements Serializable {

    @Min(0)
    private Integer quantity;
}
