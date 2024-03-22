# Real-Time-Fraud-Detection-System
Real-Time-Fraud-Detection-System
Program Requirement
Step 1: Install Intellij Idea Community or Any preferred Java Id, Java JDK 17,
        Apache Maven 3.9.x (x could be any build number)

Step 2: Open the StarterClass.java in /project-folder/src/main/java/org/FraudDetectionSyste

Step 3: Read instruction on line 10-12, 15-16, 19-20, 23-25 of StarterClass.java 
        located above to understand how to test the solution

The out of order events are handle by Stacking each transaction in a Treelike structure
with HashMap as top level using each userID as key and using TreeMap data structure 
as store for each user transactions accross all service 
`HashMap<String, TreeMap<Long, TransactionEvent>> userTransactionEvents` 
TreeMap keeps each data entry sorted based on provided TreeMap key provided, 
Timestamp are used as the TreeMap key to maintain transaction order as they 
are performed by the user irrespective of the time they got to the Fraud
Detection system, To allow better view of how the algorithm work the program
is develop to sleep