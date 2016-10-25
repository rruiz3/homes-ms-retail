package com.tenx.ms.retail.stock.repository;

import com.tenx.ms.retail.stock.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findStockByProductId(Long productId);
}
