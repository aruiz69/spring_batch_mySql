package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.Contract;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ItemReaderConfiguration {

    @Bean
    @StepScope
    public ItemReader<Contract> itemReader(@Value("#{jobParameters['data1']}") String data1, DataSource dataSource) {
        System.out.println("Obtenemos un parametro insertado en el JobParameters para escribirlo=====> " + data1);
        JdbcPagingItemReader<Contract> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setPageSize(1000);

        PagingQueryProvider queryProvider = createQuery();
        jdbcPagingItemReader.setQueryProvider(queryProvider);
        jdbcPagingItemReader.setRowMapper(new BeanPropertyRowMapper<>(Contract.class));
        return jdbcPagingItemReader;
    }

    @Bean
    @StepScope
    public ItemReader<Map<String, Object>> itemReaderMap(@Value("#{jobParameters['data1']}") String data1,
                                                         DataSource dataSource) {

        System.out.println("Obtenemos un parametro insertado en el JobParameters para escribirlo" + data1);
        JdbcPagingItemReader<Map<String, Object>> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setPageSize(1000);

        PagingQueryProvider queryProvider = createQuery();
        jdbcPagingItemReader.setQueryProvider(queryProvider);

        jdbcPagingItemReader.setRowMapper((rs, i) -> {
            Map<String, Object> mapRet = new HashMap<>();
            while (rs.next()) {
                //Colocar columna en base configuración de las columnas
                mapRet.put(rs.getString("string1"), rs.getString("string2"));
            }
            return mapRet;
        });
        return jdbcPagingItemReader;
    }

    private PagingQueryProvider createQuery() {
        MySqlPagingQueryProvider query = new MySqlPagingQueryProvider();
        query.setSelectClause("Select *");
        query.setFromClause("from contract");
        query.setSortKeys(sortByCreationDate());
        return query;
    }

    private Map<String, Order> sortByCreationDate() {
        Map<String, Order> stringOrderMap = new HashMap<>();
        stringOrderMap.put("CREATION_DATE", Order.ASCENDING);
        return stringOrderMap;
    }

}
