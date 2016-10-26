package com.tenx.ms.retail.order.domain;

import com.tenx.ms.retail.product.domain.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(targetEntity = OrderEntity.class)
    @JoinColumn(name = "order_id", insertable = false, updatable = false, nullable = false)
    private OrderEntity orderId;

    @OneToOne
    @JoinColumn(name = "product_id", updatable = false)
    private ProductEntity product;

    @Column(name="count")
    private Long count;
}
