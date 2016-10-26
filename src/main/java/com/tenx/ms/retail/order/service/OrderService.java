package com.tenx.ms.retail.order.service;

import com.tenx.ms.commons.util.converter.EntityConverter;
import com.tenx.ms.retail.order.constants.OrderStatus;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.domain.OrderItemEntity;
import com.tenx.ms.retail.order.repository.OrderItemRepository;
import com.tenx.ms.retail.order.repository.OrderRepository;
import com.tenx.ms.retail.order.rest.dto.OrderCreated;
import com.tenx.ms.retail.order.rest.dto.OrderDto;
import com.tenx.ms.retail.order.rest.dto.OrderItemDto;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.rest.dto.StockDto;
import com.tenx.ms.retail.stock.service.StockService;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OrderService {

    private final static EntityConverter<OrderDto, OrderEntity> CONVERTER = new EntityConverter<>(OrderDto.class, OrderEntity.class);

    private final static EntityConverter<OrderItemDto, OrderItemEntity> CONVERTER_ITEM = new EntityConverter<>(OrderItemDto.class, OrderItemEntity.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockSrvc;

    @Transactional
    public OrderCreated createOrder(OrderDto order) {
        Optional<StoreEntity> store = storeRepository.findById(order.getStoreId());
        if (!store.isPresent()) {
            throw new NoSuchElementException("Store not found.");
        }

        OrderEntity orderEntity = CONVERTER.toT2(order);
        orderEntity.setStatus(OrderStatus.ORDERED);
        orderEntity.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        orderEntity.setStore(store.get());

        List<OrderItemDto> backorderedItems = new ArrayList<>();
        List<OrderItemEntity> items = new ArrayList<>();

        for (OrderItemDto orderItem : order.getProducts()) {
            Optional<ProductEntity> productEntity = productRepository.findByStoreIdAndId(store.get().getId(), orderItem.getProductId());
            if (!productEntity.isPresent()) {
                throw new NoSuchElementException("Product not found.");
            }
            StockDto stock = stockSrvc.findStockProductId(order.getStoreId(), orderItem.getProductId());
            if (stock != null && stock.getCount() >= orderItem.getCount()) {
                stock.setCount(stock.getCount() - orderItem.getCount());
                stockSrvc.upsertStock(stock);

                OrderItemEntity item = CONVERTER_ITEM.toT2(orderItem);
                item.setProduct(productEntity.get());
                items.add(item);
            } else {
                backorderedItems.add(orderItem);
            }
        }

        orderEntity.setProducts(items);
        OrderEntity result = orderRepository.save(orderEntity);
        orderItemRepository.save(items);
        return new OrderCreated(result.getOrderId(), result.getStatus(), backorderedItems);
    }
}
