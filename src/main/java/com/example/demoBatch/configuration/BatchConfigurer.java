package com.example.demoBatch.configuration;

import com.example.demoBatch.entity.ContractHistory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class BatchConfigurer extends DefaultBatchConfigurer {

    @Override
    @Bean
    public JobLauncher getJobLauncher() {
        try {
            SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(getJobRepository());
            jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
            jobLauncher.afterPropertiesSet();
            return jobLauncher;

        } catch (Exception e) {
            //log.error("Can't load SimpleJobLauncher with SimpleAsyncTaskExecutor: {} fallback on default", e);
            return super.getJobLauncher();
        }
    }

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
                      //ItemReader<Contract> itemReader,
                      ItemReader<Map<String, Object>> itemReader,
                      //ItemProcessor<Contract, ContractHistory> itemProcessor,
                      ItemProcessor<Map<String, Object>, ContractHistory> itemProcessor,
                      ItemWriter<ContractHistory> itemWriter, TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("step1")
                //.<Contract, ContractHistory>chunk(1000)
                .<Map<String, Object>, ContractHistory>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .taskExecutor(taskExecutor)
                .throttleLimit(40)
                .build();

    }

    @Bean
    @Qualifier(value = "contextMap")
    // Se puede Crear un mapa de contextos por cada producto disponible
    // Se puede ubicar en otra clase dedicada a verificar la configuraci??n de los productos
    // tal que cuando se ejecute el job se pase como par??metro el nombre del producto
    // en el reader podemos recuperar el contexto completo de dicho producto
    public Map<String, Object> contextMap(ApplicationContext context) {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("ctx1", context);
        return contextMap;
    }

    @Bean
    public ControlDeEntrega getControlEntrega() {
        return new ControlDeEntrega();
    }


    static class ControlDeEntrega{
        private static  AtomicInteger atomicInt = new AtomicInteger(0);
        private int valorMaximoEntregasPorSegundo = 8;
        private LocalDateTime horaInicio;
        private boolean primeravez = true;


        public synchronized void agregarEntrega(){
            int numeroDeEntregas = atomicInt.incrementAndGet();
            if(primeravez){
                horaInicio = LocalDateTime.now();
                primeravez = false;
            } else {
                if ( numeroDeEntregas % valorMaximoEntregasPorSegundo == 0){ //||
                        //horaInicio != null && LocalDateTime.now().isAfter(horaInicio.plusSeconds(1)) ) {
                    try {
                        System.out.println(LocalDateTime.now());
                        System.out.println("SLEEP------->");
                        System.out.println("Entregados------->" + numeroDeEntregas);
                        Thread.sleep(100);
                        horaInicio = LocalDateTime.now();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
