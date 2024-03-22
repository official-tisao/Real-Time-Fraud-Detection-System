package org.FraudDetectionSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class StarterClass {

    public static void main(String[] args) {
        //Test the Fraud detection system without any data input, data are generated randomly on the fly
        //to simulate realife transaction. NB: program sleep after each input for half a second to allow
        // better view of Output. add this characters "//" to beginning of line 13 before uncommenting test type
        simulateTestWithInteractiveRandomTransaction();

        //Generate a fresh testdata by run the method below, you can use this together with method on line 21
        //as it generate fresh data into `data.txt` remove the "//" in line 17 below to run test with method.
        //generateFreshTestData();

        //Test the Fraud detection system with sample data.txt in project root folder
        //remove the "//" in line 21 below to run test with method.
        //simulateTestWithDataFileInWorkDir();

        //Test the Fraud detection system with  your preferred sample data file by specifying full path or
        //bring the data file to this project root folder where `data.txt` exist and specify the filename
        //remove the "//" in line 26 below to run test with method
        //simulateTestWithPreferredDataFile("data.txt");
    }
    public static void simulateTestWithDataFileInWorkDir(){
        DataGenerator datahub = new DataGenerator();
        FraudDetectionSystem fraudDetectionSystem = new FraudDetectionSystem();
        fraudDetectionSystem.updateUserBasedQueue(datahub.jsonToTransEventList(datahub.readFromFile()));
        System.out.println(fraudDetectionSystem.getViolationsReport());
    }
    public static void simulateTestWithPreferredDataFile(String filePath){
        DataGenerator datahub = new DataGenerator();
        FraudDetectionSystem fraudDetectionSystem = new FraudDetectionSystem();
        fraudDetectionSystem.updateUserBasedQueue(datahub.jsonToTransEventList(datahub.readFromFile(filePath)));
        System.out.println(fraudDetectionSystem.getViolationsReport());
    }
    public static void generateFreshTestData(){
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