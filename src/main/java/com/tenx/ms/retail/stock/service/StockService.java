package com.tenx.ms.retail.stock.service;


import com.tenx.ms.commons.util.converter.EntityConverter;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.stock.rest.dto.StockDto;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StockService {

    private final static EntityConverter<StockDto, StockEntity> CONVERTER = new EntityConverter<>(StockDto.class, StockEntity.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void upsertStock(StockDto stock) {
        Optional<StoreEntity> store = storeRepository.findById(stock.getStoreId());
        if (store.isPresent()) {
            Optional<ProductEntity> product = productRepository.findByStoreIdAndId(stock.getStoreId(), stock.getProductId());
            if (product.isPresent()) {
                StockEntity entity = CONVERTER.toT2(stock);
                entity.setStore(new StoreEntity(store.get().getId(), store.get().getName()));
                entity.setProduct(new ProductEntity(product.get().getId(), product.get().getStore(), product.get().getName(), product.get().getDescription(), product.get().getSku(), product.get().getPrice()));
                stockRepository.save(entity);
            } else {
                throw new NoSuchElementException("Product not found.");
            }
        } else {
            throw new NoSuchElementException("Store not found.");
        }
    }

    public StockDto findStockProductId(Long storeId, Long productId) {
        Optional<StockEntity> stock = stockRepository.findStockByProductId(productId);
        if (stock.isPresent()) {
            return new StockDto(stock.get().getProductId(), stock.get().getStoreId(), stock.get().getCount());
        } else {
            throw new NoSuchElementException("Stock not found");
        }
    }
}
