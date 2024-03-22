# Real-Time-Fraud-Detection-System
Real-Time-Fraud-Detection-System

To run this prototype, you need JDK 17, Maven 3.9.x (x could be any build number)

The out of order events are handle by Stacking each transaction in a Treelike structure
with HashMap as top level using each userID as key and using TreeMap data structure 
as store for each user transactions accross all service 
`HashMap<String, TreeMap<Long, TransactionEvent>> userTransactionEvents` 
TreeMap keeps each data entry sorted based on provided TreeMap key provided, 
Timestamp are used as the TreeMap key to maintain transaction order as they 
are performed by the user irrespective of the time they got to the Fraud
Detection sys
