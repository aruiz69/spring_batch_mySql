package com.example.demoBatch.controller;

import com.example.demoBatch.entity.Contract;
import com.example.demoBatch.entity.ContractHistory;
import com.example.demoBatch.repo.ContractHistoryJPARepo;
import com.example.demoBatch.repo.ContractJPARepo;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
public class BatchController {
    @Autowired
    private ContractJPARepo contractJPARepo;
    @Autowired
    private ContractHistoryJPARepo contractHistoryJPARepo;

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;


    @GetMapping("/insert")
    public String saveDummyData(){
        List<Contract> contractList = new ArrayList<>();
        for(int i= 0; i < 10 ; i++){
            Contract contract = new Contract();
            contract.setHolderName("name-"+ i);
            contract.setDuration(new Random().nextInt());
            contract.setAmount(new Random().nextInt(500000));
            Date date = new Date();
            date.setDate(new Random().nextInt(30));
            date.setMonth(new Random().nextInt(12));
            date.setYear(new Random().nextInt(2020));
            contract.setCreationDate(date);
            contract.setStatus("InProgress");
            contractList.add(contract);

        }
        contractJPARepo.saveAll(contractList);
        ContractHistory contract = new ContractHistory();
        contract.setHolderName("name-"+ 1);
        contract.setDuration(new Random().nextInt());
        contract.setAmount(new Random().nextInt(500000));
        Date date = new Date();
        date.setDate(new Random().nextInt(30));
        date.setMonth(new Random().nextInt(12));
        date.setYear(new Random().nextInt(2020));
        contract.setCreationDate(date);
        contract.setStatus("test");

        contractHistoryJPARepo.save(contract);
        return "saved successfully";
    }
    @GetMapping("/start-batch")
    @SneakyThrows
    public String startBatch(){
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("data1", "data1")
                .toJobParameters();

        JobParameters jobParameters2 = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("data1", "data2")
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
        saveDummyData();
        jobLauncher.run(job, jobParameters2);
        return "batch started...";
    }

}
