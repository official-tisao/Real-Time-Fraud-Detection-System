package org.FraudDetectionSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class FraudDetectionSystem {
    /*
    * Process a stream of transactions represented as a series of events.
    * Each event contains a timestamp, transaction amount, user ID, and service ID.
    Identify fraudulent patterns, such as:
    * A user conducting transactions in more than 3 distinct services within a 5-minute window.
    * Transactions that are 5x above the user's average transaction amount in the last 24 hours.
    * A sequence of transactions indicating "ping-pong" activity (transactions bouncing back and forth
        between two services) within 10 minutes.
    * Flag users involved in suspicious activities and generate an alert that summarizes the suspicious behavior.
    * Implement a mechanism to handle out-of-order events, considering network latencies in a distributed system.
    * */
    private final int hoursOfAverageTransactions = 24;

    private int NO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION = 1;

    public void setNO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION(int NO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION) {
        this.NO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION = NO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION;
    }

    public void setCurrentTimestamp(long currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    //This is the timestamp for the detection to start
    private long currentTimestamp = Math.round(System.currentTimeMillis()/1000);
    TreeMap<Long, Double> transactionEventQueue = new TreeMap<>();
    HashMap<String, TreeMap<Long, TransactionEvent>> userTransactionEvents = new HashMap<String, TreeMap<Long, TransactionEvent>>();
    HashMap<String, TreeSet<String>> fraudAlert = new HashMap<>();
    public HashMap<String, TreeSet<String>> getViolations(){
        return fraudAlert;
    }
    public String getViolationsReport(){
        String report=null;
        for (String violations : fraudAlert.keySet()) {
            TreeSet<String> users = fraudAlert.get(violations);
            String currentReport="";
            String lastUser=null;
            for (String user : users) {
                lastUser =user;
                currentReport += user + ", ";
            }
            currentReport = currentReport.indexOf(", " + lastUser + ", ") > -1 ? currentReport.replace(", " + lastUser + ", "," and " + lastUser +" ") : currentReport.replace(lastUser + ",",lastUser);

            if(FraudViolationType.valueOf(violations).equals(FraudViolationType.ThreeOrMoreServiceUsage)){
                currentReport += "flagged for conducting transactions in more than 3 distinct services within last 5-minute.";
            } else if (FraudViolationType.valueOf(violations).equals(FraudViolationType.UsualAverageTransaction)) {
                currentReport += "flagged for conducting transactions significantly higher than average amounts in last 24-hours.";
            }else{
                currentReport += "flagged for conducting transactions in Ping Pong pattern within last 10-minute.";
            }
            report = report==null ? currentReport :report+ "\n"+currentReport;
        }
        return report;
    }
    public String interactiveViolationsReport(FraudViolationType fraudViolationType, String user){
        if(fraudViolationType.equals(FraudViolationType.ThreeOrMoreServiceUsage)){
            return user + " flagged for conducting transactions in more than 3 distinct services within last 5-minute.";
        } else if (fraudViolationType.equals(FraudViolationType.UsualAverageTransaction)) {
            return user +  " flagged for conducting transactions significantly higher than average amounts within last 24-hours.";
        }else{
            return user +  " flagged for conducting transactions in Ping Pong pattern within last 10-minute.";
        }
    }
    public void detect24HoursAverageViolation(TransactionEvent transactionEvent){
        long sometimeAgoTimestamp = currentTimestamp - (hoursOfAverageTransactions*3600);
        if(!transactionEventQueue.isEmpty() && (transactionEventQueue.firstKey() < sometimeAgoTimestamp)){
            transactionEventQueue.pollFirstEntry();
        }
        //average is only calculate when transaction event in Queue is more than 2
        if(transactionEventQueue.size()>NO_OF_TRANSACTIONS_BEFORE_AVERAGE_VIOLATION){

            double current24hoursAvg = (transactionEventQueue.values().stream().mapToDouble(Double::doubleValue).sum()/transactionEventQueue.size());
            if(transactionEvent.amount() >= current24hoursAvg*5){
                triggerViolation(transactionEvent.userID(), FraudViolationType.UsualAverageTransaction);
            }
        }
        transactionEventQueue.put(transactionEvent.timestamp(), transactionEvent.amount());
    }

    public Map<String, TreeMap<Long, TransactionEvent>> getUserTransactionEvents() {
        return userTransactionEvents;
    }

    public void detect5Min3ServiceViolation(TransactionEvent transactionEvent){
        long _5MinAgoTimestamp = currentTimestamp - (5*60);
        HashSet<String> uniqueService = new HashSet<>();
        //this line get only transaction within 5min range and loop over them
        for ( Map.Entry<Long, TransactionEvent> et :  userTransactionEvents.get(transactionEvent.userID()).tailMap(_5MinAgoTimestamp).entrySet()){
            uniqueService.add(et.getValue().serviceID());
            //this trigger stop if it user hit the stop service, so does not have to go through all transaction
            if(uniqueService.size() == 3){
                triggerViolation(transactionEvent.userID(), FraudViolationType.ThreeOrMoreServiceUsage);
                return;
            }
        }

    }
    public void detectPingPong(String userID) {
        //this line ensure that transaction list is at least up 3
        if (userTransactionEvents.get(userID).size() < 3) return;
        List<TransactionEvent> userTransactionList=new ArrayList<>(userTransactionEvents.get(userID).values());
        int userTransactionSize =userTransactionList.size();
        for (int i = 0; i <  userTransactionSize - 2; i++) {
            if(i+2 <= userTransactionSize-1 && (userTransactionList.get(i).serviceID() != userTransactionList.get(i+1).serviceID()
                    && userTransactionList.get(i).amount() == userTransactionList.get(i+1).amount())
                    && (userTransactionList.get(i).serviceID() == userTransactionList.get(i+2).serviceID()
                    && userTransactionList.get(i).amount() == userTransactionList.get(i+2).amount())){
                //this trigger stop if it user hit the stop service, so does not have to go through all transaction
                triggerViolation(userID, FraudViolationType.PingPong);
                return;
            }else{
                List<String> midServiceID =new ArrayList<>();
                for (int j = i+1; j < userTransactionSize-1; j++) {
                    if( !midServiceID.isEmpty()
                            && (userTransactionList.get(i).serviceID() == userTransactionList.get(j).serviceID()
                            && userTransactionList.get(i).amount() == userTransactionList.get(j+1).amount())){
                        //this trigger stop if it user hit the stop service, so does not have to go through all transaction
                        triggerViolation(userID, FraudViolationType.PingPong);
                        return;
                    }
                    //This line match the 1st condition where transaction bounce from source
                    if((userTransactionList.get(i).serviceID() != userTransactionList.get(j).serviceID()
                            && userTransactionList.get(i).amount() == userTransactionList.get(j).amount())){
                        midServiceID.add(userTransactionList.get(j).serviceID());
                    }
                }
            }
        }
    }
    private void triggerViolation(String userID, FraudViolationType fraudViolationType){

        TreeSet<String> violations = fraudAlert.getOrDefault(fraudViolationType.toString(), new TreeSet<>());
        violations.add(userID);
        fraudAlert.put(fraudViolationType.toString(), violations);
        System.out.println(interactiveViolationsReport(fraudViolationType, userID));
        return;
    }
    public void updateUserBasedQueue(TransactionEvent transactionEvent){

        System.out.println("");
        System.out.println(transactionEvent);
        setCurrentTimestamp(System.currentTimeMillis()/1000);
        long _10MinAgoTimestamp = currentTimestamp - (10*60);
        long _5MinAgoTimestamp = currentTimestamp - (5*60);
        long _24HoursAgoTimestamp = currentTimestamp - (24*3600);
        //if transaction event is older than 10 min, do not queue for fraud detection
        if(transactionEvent.timestamp() >= _10MinAgoTimestamp){
            //TreeMap ensure that irrespective of transaction entry time, it will be sorted based on timestamp
            TreeMap<Long, TransactionEvent> userTransactions = userTransactionEvents.getOrDefault(transactionEvent.userID(), new TreeMap<>());
            userTransactions.put(transactionEvent.timestamp(), transactionEvent);

            //if transaction event is older than 10 min remove it from user based queue
            if(userTransactions.firstEntry().getKey() < _10MinAgoTimestamp){
                userTransactions.pollFirstEntry();
            }

            userTransactionEvents.put(transactionEvent.userID(), userTransactions);

            //Attempt to detect ThreeOrMoreService Violation
            if (transactionEvent.timestamp() >= _5MinAgoTimestamp){ detect5Min3ServiceViolation(transactionEvent);}
            //Attempt to detect Ping Pong Violation
            detectPingPong(transactionEvent.userID());
        }

        if(transactionEvent.timestamp() >= _24HoursAgoTimestamp){
            //Attempt to detect 24 hours Average transaction Violation
            detect24HoursAverageViolation(transactionEvent);
        }


    }

    public void updateUserBasedQueue(List<TransactionEvent> transactionEvents){
        for (TransactionEvent transactionEvent:
             transactionEvents) {
            updateUserBasedQueue(transactionEvent);
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
