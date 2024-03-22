package org.FraudDetectionSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class StarterClass {

    public static void main(String[] args) {
        List<TransactionEvent> testData = new ArrayList<>();
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000), 100.0, "user1", "serviceA"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+1, 100.0, "user7", "serviceA"));
        testData.add( new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+11, 101.0, "user2", "serviceB"));
        testData.add( new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+20, 115.0, "user3", "serviceC"));
        testData.add( new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+21, 100.0, "user1", "serviceC"));
        testData.add( new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+22, 100.0, "user7", "serviceC"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+30, 130.0, "user4", "serviceA"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+40, 50.0, "user5", "serviceB"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+50, 110.0, "user6", "serviceC"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+51, 100.0, "user1", "serviceA"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+55, 100.0, "user7", "serviceA"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+41, 1500.0, "user5", "serviceA"));
        testData.add(new TransactionEvent(Math.round(System.currentTimeMillis()/1000)+43, 1500.0, "user5", "serviceC"));

        TreeMap<Long, TransactionEvent> el = new TreeMap<Long, TransactionEvent>();
        FraudDetectionSystem fraudDetectionSystem = new FraudDetectionSystem();

        for (TransactionEvent transEvent: testData) {
            fraudDetectionSystem.updateUserBasedQueue(transEvent);
            System.out.println("");
        }
        System.out.println(fraudDetectionSystem.getViolations());
        System.out.println(fraudDetectionSystem.getViolationsReport());
    }
}