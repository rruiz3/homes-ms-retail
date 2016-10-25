package com.tenx.ms.retail.order.domain;

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
    @JoinColumn(name = "order_id")
    private OrderEntity orderId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name="count")
    private Integer count;
}
