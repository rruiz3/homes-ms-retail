package com.tenx.ms.retail.product.domain;

import com.tenx.ms.retail.store.domain.StoreEntity;
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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    @NotNull
    @ManyToOne(targetEntity = StoreEntity.class)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "sku", length = 10)
    private String sku;

    @Column(name = "price", precision = 20, scale = 2)
    private BigDecimal price;
}
