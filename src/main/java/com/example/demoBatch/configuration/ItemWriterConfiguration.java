package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.ContractHistory;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class ItemWriterConfiguration {
    @Bean
    public ItemWriter<ContractHistory> itemWriter(NamedParameterJdbcTemplate jdbcTemplate,
                                                  BatchConfigurer.ControlDeEntrega controlDeEntrega){
        final String INSERT_QUERY ="INSERT INTO contract_history (contract_id, amount, creation_date, duration," +
                "holder_name, status) VALUES ( :contractId, :amount, :creationDate, :duration, :holderName, 'EFFECTIVE')";

        val itemWriter = new JdbcBatchItemWriter<ContractHistory>() {
            @Override
            public void write(List<? extends ContractHistory> items) throws Exception {
                controlDeEntrega.agregarEntrega();
                super.write(items);
                log.info("item processed -" + items.size());
                delete(items.stream().map(ContractHistory::getContractId).collect(Collectors.toList()), jdbcTemplate);
            }
        };
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.setJdbcTemplate(jdbcTemplate);
        itemWriter.setAssertUpdates(false);


        return itemWriter;
    }

    public void delete(final List<String> contractIdList, NamedParameterJdbcTemplate template){
        final String DELETE_QUERY = "DELETE FROM contract WHERE contract_id IN (:contractId)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("contractId", contractIdList);
        template.update(DELETE_QUERY, parameterSource);
    }


}
