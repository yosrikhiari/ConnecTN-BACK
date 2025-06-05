package tn.esprit.spring.connectn.Services.MarketPlace;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Product;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.ProductRepository;
import tn.esprit.spring.connectn.Repository.ShopRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public ProductService(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        // Validate discount price
        if (product.getDiscountPrice() > 0 && product.getDiscountPrice() >= product.getPrice()) {
            throw new IllegalArgumentException("Discount price must be less than regular price");
        }

        // Validate stock
        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("Stock must be greater than zero");
        }

        // Fetch the complete shop entity
        Shop shop = shopRepository.findById(product.getShop().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        product.setShop(shop);

        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, Product updatedProduct) {
        // Validate discount price
        if (updatedProduct.getDiscountPrice() > 0 && updatedProduct.getDiscountPrice() >= updatedProduct.getPrice()) {
            throw new IllegalArgumentException("Discount price must be less than regular price");
        }

        // Validate stock
        if (updatedProduct.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        return productRepository.findById(productId)
                .map(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setStock(updatedProduct.getStock());
                    existingProduct.setDiscountPrice(updatedProduct.getDiscountPrice());
                    existingProduct.setBarcode(updatedProduct.getBarcode());
                    existingProduct.setCategory(updatedProduct.getCategory());
                    if (updatedProduct.getImageUrl() != null) {
                        existingProduct.setImageUrl(updatedProduct.getImageUrl());
                    }
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    // Add pagination methods
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProductsByShop(Long shopId, Pageable pageable) {
        return productRepository.findByShopId(shopId, pageable);
    }

    public Page<Product> getDiscountedProducts(Pageable pageable) {
        return productRepository.findByDiscountPriceGreaterThan(0, pageable);
    }

    public Page<Product> getProductsByCategory(Product.ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    public List<Product> getProductsByShop(Long shopId) {
        return productRepository.findByShopId(shopId);
    }
}
