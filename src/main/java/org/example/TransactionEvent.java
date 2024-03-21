package org.example;

public record TransactionEvent (long timestamp, double amount, String userID, String serviceID) {

}
