package org.FraudDetectionSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class StarterClass {

    public static void main(String[] args) {
        simulateTestWithInteractiveRandomTransaction();
    }
    public void simulateTestWithDataFileInWorkDir(){
        //new DataGenerator().
    }
    public void generateFreshTestData(){
        System.out.println("Generating Random Test Data and storing to path: data.txt");
        new DataGenerator().writeToFile();
    }
    public static void simulateTestWithInteractiveRandomTransaction(){
        TreeMap<Long, TransactionEvent> el = new TreeMap<Long, TransactionEvent>();
        FraudDetectionSystem fraudDetectionSystem = new FraudDetectionSystem();
        DataGenerator data = new DataGenerator();
        try {
            for (int i =0; i<1000; i++) {
                Thread.sleep(500);
                fraudDetectionSystem.updateUserBasedQueue(data.generateSingleTestData());
                System.out.println("");
            }
        } catch (Exception e){
            //thread error
        }
        System.out.println(fraudDetectionSystem.getViolationsReport());
    }
}