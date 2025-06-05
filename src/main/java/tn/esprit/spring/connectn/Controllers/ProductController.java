package tn.esprit.spring.connectn.Controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ProductDto;
import tn.esprit.spring.connectn.Entities.Product;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Services.MarketPlace.ProductService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;
    private final Cloudinary cloudinary;

    public ProductController(ProductService productService, Cloudinary cloudinary) {
        this.productService = productService;
        this.cloudinary = cloudinary;
    }
    // Get all products with shop name
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getAllProducts(PageRequest.of(page, size));
        Page<ProductDto> dtos = products.map(ProductDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/discounted")
    public ResponseEntity<Page<Product>> getDiscountedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getDiscountedProducts(PageRequest.of(page, size));
        products.forEach(this::enrichProductData);
        return ResponseEntity.ok(products);
    }
    private void enrichProductData(Product product) {
        if (product.getShop() != null) {
            product.setShopName(product.getShop().getName());
        }
        product.setSalePercentage(product.getSalePercentage());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Product.ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getProductsByCategory(category, PageRequest.of(page, size));
        products.forEach(this::enrichProductData);
        return ResponseEntity.ok(products);
    }
    // Get products by shop ID
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductDto>> getProductsByShop(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShop(shopId);
        List<ProductDto> dtos = products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
 // Create product with image
 @PostMapping(consumes = "multipart/form-data")
 public ResponseEntity<ProductDto> addProduct(
         @RequestParam("shopId") Long shopId,
         @RequestParam("name") String name,
         @RequestParam("price") Double price,
         @RequestParam("description") String description,
         @RequestParam("stock") int stock,
         @RequestParam("discountPrice") int discountPrice,
         @RequestParam("barcode") String barcode,
         @RequestParam("category") Product.ProductCategory category,
         @RequestParam("image") MultipartFile image
 ) {
     try {
         // Upload the image to Cloudinary
         Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
         String imageUrl = (String) uploadResult.get("secure_url");

         Product product = new Product();
         product.setName(name);
         product.setPrice(price);
         product.setDescription(description);
         product.setStock(stock);
         product.setDiscountPrice(discountPrice);
         product.setBarcode(barcode);
         product.setCategory(category);
         product.setImageUrl(imageUrl);

         // Set shop by ID
         Shop shop = new Shop();
         shop.setId(shopId);
         product.setShop(shop);

         Product savedProduct = productService.addProduct(product);
         return ResponseEntity.ok(ProductDto.fromEntity(savedProduct));

     } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(500).build();
     }
 }

    // Update product (with optional image update)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("stock") int stock,
            @RequestParam("discountPrice") int discountPrice,
            @RequestParam("barcode") String barcode,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Product existingProduct = productService.getProductById(id);

            if (image != null && !image.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                existingProduct.setImageUrl(imageUrl);
            }

            existingProduct.setName(name);
            existingProduct.setPrice(price);
            existingProduct.setDescription(description);
            existingProduct.setStock(stock);
            existingProduct.setDiscountPrice(discountPrice);
            existingProduct.setBarcode(barcode);

            Product updatedProduct = productService.updateProduct(id, existingProduct);

            // Convert to DTO before returning
            ProductDto productDto = ProductDto.fromEntity(updatedProduct);
            return ResponseEntity.ok(productDto);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Get products by shop ID


    @GetMapping("/product")
    public ResponseEntity<ProductDto> getProductById(@RequestParam Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductDto.fromEntity(product));
    }

}
