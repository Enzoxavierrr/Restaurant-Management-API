package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByMenuId(Long menuId);

    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.menu.id = :menuId")
    List<Product> findActiveByMenuId(@Param("menuId") Long menuId);
}
