package com.example.demoBatch.repo;

import com.example.demoBatch.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractJPARepo extends JpaRepository<Contract, String> {
}
