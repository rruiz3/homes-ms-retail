package com.tenx.ms.retail.stock.domain;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.store.domain.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @Column(name = "product_id", nullable = false, insertable = false, updatable = false)
    @NotNull
    private Long productId;

    @OneToOne(targetEntity = ProductEntity.class)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "product_id")
    private ProductEntity product;

    @Column(name = "store_id", insertable = false, updatable = false)
    private Long storeId;

    @ManyToOne(targetEntity = StoreEntity.class)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(name = "count", nullable = false)
    @NotNull
    private Long count;
}
