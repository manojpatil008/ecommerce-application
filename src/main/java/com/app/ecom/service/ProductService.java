package com.app.ecom.service;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;
import com.app.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        return mapProductToProductResponse(productRepository.save(mapProductRequestToProduct(product, productRequest)));
    }


    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapProductToProductResponse)
                .collect(Collectors.toList());
    }


    public List<ProductResponse> getAllActiveProducts(){
        return productRepository.findByActiveTrue().stream()
                .map(this::mapProductToProductResponse)
                .collect(Collectors.toList());
    }


    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapProductToProductResponse);
    }


    public Optional<ProductResponse> updateProduct(ProductRequest updatedProductRequest, Long id) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    mapProductRequestToProduct(existingProduct, updatedProductRequest);
                    Product updatedProduct = productRepository.save(existingProduct);
                    return mapProductToProductResponse(updatedProduct);
                });
    }


    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    private Product mapProductRequestToProduct(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        return product;
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(String.valueOf(product.getId()));
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setCategory(product.getCategory());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setActive(product.getActive());
        return productResponse;
    }


    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapProductToProductResponse)
                .collect(Collectors.toList());
    }
}
