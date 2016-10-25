package com.tenx.ms.retail.product.service;

import com.tenx.ms.commons.util.converter.EntityConverter;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.product.rest.dto.ProductDto;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreRepository storeRepository;
    private final static EntityConverter<ProductDto, ProductEntity> CONVERTER = new EntityConverter<>(ProductDto.class, ProductEntity.class);

    @Transactional
    public Long createProduct(ProductDto product) {
        Optional<StoreEntity> store = storeRepository.findById(product.getStoreId());
        if (store.isPresent()) {
            ProductEntity entity = CONVERTER.toT2(product);
            entity.setStore(store.get());
            return productRepository.save(entity).getId();
        } else {
            throw new NoSuchElementException("Store not found.");
        }
    }

    public List<ProductDto> findAllProductsInStore(Long storeId) {
        if (validateStoreExists(storeId)) {
            return productRepository.findProductsByStoreId(storeId).stream().map(CONVERTER::toT1).collect(Collectors.toList());
        } else {
            throw new NoSuchElementException("Store not found.");
        }
    }

    public ProductDto findProductInStore(Long storeId, Long productId) {
        if (validateStoreExists(storeId)) {
            Optional<ProductEntity> product = productRepository.findByStoreIdAndId(storeId, productId);
            System.out.println(product.isPresent());
            if (product.isPresent()) {
                return new ProductDto(product.get().getId(), product.get().getStore().getId(), product.get().getName(), product.get().getDescription(), product.get().getSku(), product.get().getPrice());
            } else {
                throw new NoSuchElementException("Product not found.");
            }
        } else {
            throw new NoSuchElementException("Store not found.");
        }
    }

    public void deleteProduct(Long productId) {
        if (productRepository.exists(productId)) {
            productRepository.delete(productId);
        } else {
            throw new NoSuchElementException();
        }
    }

    private boolean validateStoreExists(Long storeId) {
        Optional<StoreEntity> store = storeRepository.findById(storeId);
        return store.isPresent();
    }
}
