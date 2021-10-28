Approach: Below are the highlights of the approach used:


Custom LineIterator for large file parsing: For streaming Through the file, java.util.Scanner is used to run through the contents of the file and retrieve lines serially, one by one without keeping them in memory.  This allows for processing of each line without keeping references to them and avoids out-of-memory exception which can happen in the case of a large file. Alternatively, this can be done using Streaming With Apache Commons IO library as well, by using the custom LineIterator.

Asynchronous programming: Asynchronous programming is used for writing non-blocking code. It is implemented using CompltableFuture class introduced in Java 8 which implements the CompletionStage interface.  It runs a task on a separate thread than the main application thread and notifies the main thread about its progress, completion, or failure. In this way, the main thread does not block or wait for the completion of the task. Other tasks execute in parallel. Parallelism improves the performance of the program. 

Use of Hashmap: Hashmap is used for efficiently looking up the individual log and once “finished” is found, the same is removed and moved to DB from Hashmap to keep it light.


High-level Workflow:

Step1: LineIterator reads the file, line by line, and spawns a separate task/fiber using CompltableFuture class.

Step2: Each fiber will run in a separate thread and parse it into JSON format, store it into a separate class which is stored in the Map with event ID as the key.

Step3: While reading each line, Map is checked for the existence of the key, and if present, time is calculated and flagged if more than 4 minutes, and it will be removed from the map and moved to DB. 

The longest-running event is also tracked as needed by the requirement and shared once the parsing is complete.


Setup to run the code:

a) Download the latest from HSQLDB. Extract the .zip file in the folder. 

b) Add HSQLDB Jar file to your project classpath.
 
c) Run the below steps on DB:

   i) create the setting with sampledb and set the password as ‘SA’ and then run the below commands 
   
   ii) java -cp ../lib/hsqldb-jdk8.jar org.hsqldb.server.Server --database.0 file:sampledb/sampledb --dbname.0 sampledb 
   
   iii) java -cp hsqldb-jdk8.jar org.hsqldb.util.DatabaseManagerSwing
   

Run the code in two ways:

a) run the below gradle command on terminal that’s gonna run the task ‘execute’       
 
 ./gradlew clean execute

b) run the class FileRead that have main() method.