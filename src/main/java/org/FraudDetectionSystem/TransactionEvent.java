package org.FraudDetectionSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

public record TransactionEvent (long timestamp, double amount, String userID, String serviceID) {
    public String toJSON(){
        try {
            // Serialize record to JSON and return
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            //suppress error
            return null;
        }
    }
    public static TransactionEvent fromJSONString(String json){
        try {
            return new ObjectMapper().readValue(json, TransactionEvent.class);
        }catch (Exception e){
            //suppress error
            return null;
        }
    }
}
