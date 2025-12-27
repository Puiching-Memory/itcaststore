package cn.itcast.itcaststore.controller;

import cn.itcast.itcaststore.domain.Product;
import cn.itcast.itcaststore.repository.ProductRepository;
import cn.itcast.itcaststore.util.ResponseResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseResult<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        
        if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.searchProducts(keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory(category, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        
        return ResponseResult.success(products);
    }

    @GetMapping("/{id}")
    public ResponseResult<Product> getProduct(@PathVariable String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return ResponseResult.success(product);
    }

    @GetMapping("/hot")
    public ResponseResult<List<Product>> getHotProducts() {
        List<Product> products = productRepository.findTop10ByOrderByPnumDesc();
        return ResponseResult.success(products);
    }

    @PostMapping
    public ResponseResult<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = new Product();
        product.setId("product-" + UUID.randomUUID().toString());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setPnum(request.getPnum());
        product.setImgurl(request.getImgurl());
        product.setDescription(request.getDescription());
        
        Product savedProduct = productRepository.save(product);
        return ResponseResult.success(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseResult<Product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setPnum(request.getPnum());
        if (request.getImgurl() != null) {
            product.setImgurl(request.getImgurl());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        Product updatedProduct = productRepository.save(product);
        return ResponseResult.success(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteProduct(@PathVariable String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        productRepository.delete(product);
        return ResponseResult.success(null);
    }

    // 请求DTO类
    public static class ProductRequest {
        @NotBlank(message = "商品名称不能为空")
        @Size(max = 40, message = "商品名称长度不能超过40个字符")
        private String name;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格必须大于0")
        @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
        private BigDecimal price;

        @Size(max = 40, message = "分类长度不能超过40个字符")
        private String category;

        @NotNull(message = "库存数量不能为空")
        @Min(value = 0, message = "库存数量不能为负数")
        private Integer pnum;

        @Size(max = 255, message = "图片URL长度不能超过255个字符")
        private String imgurl;

        @Size(max = 1000, message = "描述长度不能超过1000个字符")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getPnum() {
            return pnum;
        }

        public void setPnum(Integer pnum) {
            this.pnum = pnum;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
