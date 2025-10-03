package com.project.demo.logic.entity.products;

import com.project.demo.logic.entity.categories.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
}

