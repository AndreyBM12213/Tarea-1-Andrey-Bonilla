package com.project.demo.rest.categories;

import com.project.demo.logic.entity.categories.Categories;
import com.project.demo.logic.entity.categories.CategoriesRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoriesRestController {

    @Autowired
    private CategoriesRepository categoriesRepository;

    // GET ALL - Listar categorías con paginación
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Categories> categoriesPage = categoriesRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Categories retrieved successfully",
                categoriesPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    // GET BY ID - Obtener una categoría específica
    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Integer categoryId, HttpServletRequest request) {
        Optional<Categories> category = categoriesRepository.findById(categoryId);

        if (category.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        return new GlobalResponseHandler().handleResponse(
                "Category retrieved successfully",
                category.get(),
                HttpStatus.OK,
                request
        );
    }

    // POST - Crear categoría
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Categories category, HttpServletRequest request) {
        categoriesRepository.save(category);

        return new GlobalResponseHandler().handleResponse(
                "Category created successfully",
                category,
                HttpStatus.CREATED,
                request
        );
    }

    // PUT - Actualizar categoría
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody Categories category,
            HttpServletRequest request) {

        Optional<Categories> foundCategory = categoriesRepository.findById(categoryId);

        if (foundCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Categories existingCategory = foundCategory.get();
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        categoriesRepository.save(existingCategory);

        return new GlobalResponseHandler().handleResponse(
                "Category updated successfully",
                existingCategory,
                HttpStatus.OK,
                request
        );
    }

    // DELETE - Eliminar categoría
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryId, HttpServletRequest request) {
        Optional<Categories> foundCategory = categoriesRepository.findById(categoryId);

        if (foundCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        categoriesRepository.deleteById(categoryId);

        return new GlobalResponseHandler().handleResponse(
                "Category deleted successfully",
                foundCategory.get(),
                HttpStatus.OK,
                request
        );
    }
}