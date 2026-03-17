package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
