package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.SupplierEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Integer>, JpaSpecificationExecutor<SupplierEntity> {
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Integer id);
    boolean existsByTaxCode(String taxCode);
    boolean existsByTaxCodeAndIdNot(String taxCode, Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SupplierEntity> findTopByOrderByIdDesc();
}
