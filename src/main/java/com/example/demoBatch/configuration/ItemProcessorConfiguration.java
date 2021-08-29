package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.Contract;
import com.example.demoBatch.entity.ContractHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class ItemProcessorConfiguration {
    private AtomicInteger count = new AtomicInteger();
    @Bean
    public ItemProcessor<Contract, ContractHistory>  itemProcessor(){
        return contract -> {
            //log.info("processing the data "+ contract.getContractId() + " Record No : " + count.incrementAndGet());
            ContractHistory contractHistory = new ContractHistory(
                    contract.getContractId(),
                    contract.getHolderName(),
                    contract.getDuration(),
                    contract.getAmount(),
                    contract.getCreationDate(),
                    contract.getStatus());
            return contractHistory;
        };
    }

    @Bean
    public ItemProcessor<Map<String, Object>, ContractHistory>  itemProcessorMap(){
        return map -> {
            //log.info("processing the data "+ map + " Record No : " + count.incrementAndGet());
            ContractHistory contractHistory = new ContractHistory();
            contractHistory.setContractId(map.get("CONTRACT_ID").toString());
            contractHistory.setDuration((Integer) map.get("DURATION"));
            contractHistory.setCreationDate((Date) map.get("CREATION_DATE"));
            contractHistory.setAmount((Double)map.get("AMOUNT"));
            contractHistory.setStatus(map.get("STATUS").toString());
            contractHistory.setStatus(map.get("HOLDER_NAME").toString());
            return contractHistory;
        };
    }

}
