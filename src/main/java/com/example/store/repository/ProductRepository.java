package com.example.store.repository;

import com.example.store.model.entities.ProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
    List<ProductEntity> findAll();

    @Override
    List<ProductEntity> findAllById(Iterable<Long> ids);
}
