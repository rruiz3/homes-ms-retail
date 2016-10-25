package com.tenx.ms.retail.store.service;

import com.tenx.ms.commons.util.converter.EntityConverter;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import com.tenx.ms.retail.store.rest.dto.StoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;
    private final static EntityConverter<StoreDto, StoreEntity> CONVERTER = new EntityConverter<>(StoreDto.class, StoreEntity.class);

    @Transactional
    public Long createStore(StoreDto store) {
        StoreEntity entity = CONVERTER.toT2(store);
        repository.save(entity);
        return entity.getId();
    }

    public List<StoreDto> findAllStores() {
        return repository.findAll().stream().map(CONVERTER::toT1).collect(Collectors.toList());
    }

    public StoreDto findStoreById(Long storeId) {
        Optional<StoreEntity> store = repository.findById(storeId);
        if (store.isPresent()) {
            return new StoreDto(store.get().getId(), store.get().getName());
        } else {
            throw new NoSuchElementException("Store not found.");
        }
    }

    public void deleteStore(Long storeId) {
        if (repository.exists(storeId)) {
            repository.delete(storeId);
        } else {
            throw new NoSuchElementException();
        }
    }
}
