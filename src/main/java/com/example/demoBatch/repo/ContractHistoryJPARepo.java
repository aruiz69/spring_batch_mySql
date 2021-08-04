package com.example.demoBatch.repo;

import com.example.demoBatch.entity.Contract;
import com.example.demoBatch.entity.ContractHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractHistoryJPARepo extends JpaRepository<ContractHistory, String> {
}
