package com.example.store.repository;

import com.example.store.model.dtos.OrderRequestDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCacheRepository extends CrudRepository<OrderRequestDto, Long> {
    List<OrderRequestDto> findAll();
}
