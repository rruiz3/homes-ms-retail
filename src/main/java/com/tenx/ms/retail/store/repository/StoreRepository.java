package com.tenx.ms.retail.store.repository;

import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long>{

    Optional<StoreEntity> findById(Long id);
}
