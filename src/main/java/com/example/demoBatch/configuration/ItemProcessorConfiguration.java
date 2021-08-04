package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.Contract;
import com.example.demoBatch.entity.ContractHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class ItemProcessorConfiguration {
    private AtomicInteger count = new AtomicInteger();
    @Bean
    public ItemProcessor<Contract, ContractHistory>  itemProcessor(){
        return new ItemProcessor<Contract, ContractHistory>() {
            @Override
            public ContractHistory process(Contract contract) throws Exception {
                log.info("processing the data "+ contract.getContractId() + " Record No : " + count.incrementAndGet());
                ContractHistory contractHistory = new ContractHistory(
                        contract.getContractId(),
                        contract.getHolderName(),
                        contract.getDuration(),
                        contract.getAmount(),
                        contract.getCreationDate(),
                        contract.getStatus());




                return contractHistory;
            }
        };
    }
}
