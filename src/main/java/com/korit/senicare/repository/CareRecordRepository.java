package com.korit.senicare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.korit.senicare.entity.CareRecordEntity;

import jakarta.transaction.Transactional;
import java.util.List;


@Repository
public interface CareRecordRepository extends JpaRepository<CareRecordEntity, Integer> {

    List<CareRecordEntity> findByCustomerNumberOrderByRecordNumberDesc(Integer customerNumber);
    
    @Transactional // 삭제 뒤 커밋과 같은 과정이 필수적임. '@Transactional'이 이 같은 역할
    void deleteByCustomerNumber(Integer customerNumber);
}
