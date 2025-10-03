package com.project.demo.rest.products;
import com.project.demo.logic.entity.categories.Categories;
import com.project.demo.logic.entity.categories.CategoriesRepository;
import com.project.demo.logic.entity.products.Products;
import com.project.demo.logic.entity.products.ProductsRepository;

import com.project.demo.logic.entity.categories.Categories;
import com.project.demo.logic.entity.categories.CategoriesRepository;
import com.project.demo.logic.entity.products.Products;
import com.project.demo.logic.entity.products.ProductsRepository;
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
@RequestMapping("/products")
public class ProductsRestController {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProductsRepository productsRepository;

    // GET ALL - Listar productos con paginación
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Products> productsPage = productsRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Products retrieved successfully",
                productsPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    // GET BY ID - Obtener un producto específico
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Integer productId, HttpServletRequest request) {
        Optional<Products> product = productsRepository.findById(productId);

        if (product.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        return new GlobalResponseHandler().handleResponse(
                "Product retrieved successfully",
                product.get(),
                HttpStatus.OK,
                request
        );
    }

    // POST - Crear producto
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestBody Products product,
            @RequestParam Integer categoryId,
            HttpServletRequest request) {

        Optional<Categories> optionalCategory = categoriesRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.BAD_REQUEST,
                    request
            );
        }

        product.setCategory(optionalCategory.get());
        productsRepository.save(product);

        return new GlobalResponseHandler().handleResponse(
                "Product created successfully",
                product,
                HttpStatus.CREATED,
                request
        );
    }

    // PUT - Actualizar producto
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer productId,
            @RequestBody Products product,
            @RequestParam Integer categoryId,
            HttpServletRequest request) {

        Optional<Products> foundProduct = productsRepository.findById(productId);

        if (foundProduct.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Optional<Categories> optionalCategory = categoriesRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Category id " + categoryId + " not found",
                    HttpStatus.BAD_REQUEST,
                    request
            );
        }

        Products existingProduct = foundProduct.get();
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setCategory(optionalCategory.get());

        productsRepository.save(existingProduct);

        return new GlobalResponseHandler().handleResponse(
                "Product updated successfully",
                existingProduct,
                HttpStatus.OK,
                request
        );
    }

    // DELETE - Eliminar producto
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId, HttpServletRequest request) {
        Optional<Products> foundProduct = productsRepository.findById(productId);

        if (foundProduct.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        productsRepository.deleteById(productId);

        return new GlobalResponseHandler().handleResponse(
                "Product deleted successfully",
                foundProduct.get(),
                HttpStatus.OK,
                request
        );
    }
}