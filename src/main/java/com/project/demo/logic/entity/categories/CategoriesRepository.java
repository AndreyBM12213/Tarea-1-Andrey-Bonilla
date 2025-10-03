package com.project.demo.logic.entity.categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
}

