package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.Contract;
import com.example.demoBatch.entity.ContractHistory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class BatchConfigurer extends DefaultBatchConfigurer {

    @Bean
    public Job startBatch(JobBuilderFactory jobBuilderFactory, Step step) {
         return jobBuilderFactory.get("contractEffective")
                 .start(step)
                 .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory,
                          ItemReader<Contract> itemReader,
                          ItemProcessor<Contract, ContractHistory> itemProcessor,
                          ItemWriter<ContractHistory> itemWriter, TaskExecutor taskExecutor){
        return stepBuilderFactory.get("step1")
                .<Contract, ContractHistory>chunk(1000)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .taskExecutor(taskExecutor)
                .throttleLimit(10)
                .build();

    }
}
