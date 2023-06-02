package com.bruno.dscommerce.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bruno.dscommerce.Entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
