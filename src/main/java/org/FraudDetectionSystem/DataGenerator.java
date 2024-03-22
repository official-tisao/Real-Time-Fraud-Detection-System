
package org.FraudDetectionSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataGenerator {
    private String sampleData;

    LinkedList<String> doFixed = new LinkedList<>();

    public void setSequenceInterupt(int sequenceInterupt) {
        this.sequenceInterupt = sequenceInterupt;
    }

    private int sequenceInterupt;
    public String getSampleData() {
        return sampleData;
    }
    public void writeToFile(){
        writeToFile(sampleData);
    }
    public void writeToFile(String content){
        String filePath = "data.txt";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("could not write file: " + e.getMessage());
        }
    }

    public String readFromFile(){
        return readFromFile("data.txt");
    }
    public String readFromFile(String filePath){
        try {
            Path pathObj = Paths.get(filePath);
            return Files.readString(pathObj);
        } catch (IOException e) {
            System.err.println("could not read: " + e.getMessage());
            return null;
        }
    }

    public DataGenerator(){
        sampleData = generateStringTestData();
    }

    public DataGenerator(int sampleDataLength) {
        sampleData = generateStringTestData(sampleDataLength);
    }

    public TransactionEvent generateSingleTestData(){
        sequenceInterupt++;
        double fixedAmount=0.0; Long fixedTime=null;
        String fixUser=null;int ii=0;
        double amount =sequenceInterupt%30==0?(1000 + new Random().nextInt(500000)/100.0)*10:100 + new Random().nextInt(500000)/100.0;
        if((sequenceInterupt%50==0)){
            fixedTime = Math.round(System.currentTimeMillis()/1000.0);
            fixedAmount=100 + new Random().nextInt(50000)/100.0;
            String fixedService="service" + (char) ('A' + new Random().nextInt(10));
            fixUser="user" + new Random().nextInt(50);
            doFixed.add(String.valueOf(fixedTime));
            doFixed.add(String.valueOf(fixedAmount));
            doFixed.add(fixUser);
            doFixed.add(fixedService);
            doFixed.add("service" + (char) ('A' + new Random().nextInt(10)));
            doFixed.add(fixedService);
        }

        long currentMil = System.currentTimeMillis()/1000;
        TransactionEvent transactionEvent = new TransactionEvent((!doFixed.isEmpty()? Long.parseLong(doFixed.get(0))+doFixed.size() :currentMil + new Random().nextInt(25*3600)),
                (!doFixed.isEmpty()? Double.parseDouble(doFixed.get(1)) :amount),
                (!doFixed.isEmpty()?doFixed.get(2):"user" + new Random().nextInt(50)),
                (!doFixed.isEmpty()?doFixed.pollLast():("service" + (char) ('A' + new Random().nextInt(10)))));
        if(!doFixed.isEmpty()){
            if(doFixed.size()==3)doFixed.clear();
        }
        return transactionEvent;
    }
    public List<TransactionEvent> generateTestData(int noOfTestData){
        long currentMil = System.currentTimeMillis()/1000;
        List<TransactionEvent> list = new ArrayList<>();
        for(int i = 0; i<noOfTestData; i++){
            list.add(generateSingleTestData());
        }
        return list;
    }
    public List<TransactionEvent> generateTestData(){
        return generateTestData(200+new Random().nextInt(500));
    }
    public String generateStringTestData(int noOfTestData){
        long currentMil = System.currentTimeMillis()/1000;
        String jsonList  = "[ ";
        for(int i = 0; i<noOfTestData; i++){
            jsonList +="\n" + generateSingleTestData().toJSON()+", ";
        }
        jsonList += "]";
        return jsonList.replace(", ]", " ]");
    }
    public String generateStringTestData(){
        return generateStringTestData(200+new Random().nextInt(500));
    }
    public String listTransEventToJSON(List<TransactionEvent> transactionEventList){
        try {
            return new ObjectMapper().writeValueAsString(transactionEventList);
        }catch (Exception e){
            //suppress error
            return null;
        }
    }
    public List<TransactionEvent>  jsonToTransEventList(String jsonArray){
        try {
            TransactionEvent[] eventsArray = new ObjectMapper().readValue(jsonArray, TransactionEvent[].class);
            return Arrays.asList(eventsArray);
        }catch (Exception e){
            //suppress error
            return null;
        }
    }



}
