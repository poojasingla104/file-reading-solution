# file-reading-solution

Java 8 introduced the CompletableFuture class. Along with the Future interface, it also implemented the CompletionStage interface. This interface defines the contract for an asynchronous computation step that we can combine with other steps.

A CompltableFuture is used for asynchronous programming. Asynchronous programming means writing non-blocking code. It runs a task on a separate thread than the main application thread and notifies the main thread about its progress, completion or failure.
In this way, the main thread does not block or wait for the completion of the task. Other tasks execute in parallel. Parallelism improves the performance of the program.
A CompletableFuture is a class in Java. It belongs to java.util.concurrent package. It
implements CompletionStage and Future interface.

1.A CompletableFuture is executed asynchronously when the method typically ends with the keyword Async
2.By default (when no Executor is specified), asynchronous execution uses the common ForkJoinPool implementation, which uses daemon threads to execute the Runnable task. Note that this is specific to CompletableFuture. Other CompletionStage implementations can override the default behavior.


How to read a Large File Efficiently with Java:

a) The standard way of reading the lines of the file is in memory – both Guava and Apache Commons IO provide a quick way to do just that:
Files.readLines(new File(path), Charsets.UTF_8);
FileUtils.readLines(new File(path));

The problem with this approach is that all the file lines are kept in memory – which will quickly lead to OutOfMemoryError if the File is large enough.

b) Streaming Through the File
we're going to use a java.util.Scanner to run through the contents of the file and retrieve lines serially, one by one.
This solution will iterate through all the lines in the file – allowing for processing of each line – without keeping references to them – and in conclusion, without keeping them in memory

c) Streaming With Apache Commons IO
The same can be achieved using the Commons IO library as well, by using the custom LineIterator provided by the library
Since the entire file is not fully in memory – this will also result in pretty conservative memory consumption numbers: 

So here I am using custom LineIterator for parsing the text file


Setting required before running the code:

a) Download latest from HSQLDB. Extract the .zip file in folder as shown below: 
b) Add HSQLDB Jar file to your project classpath.
c) Run the below steps on DB:

create the setting with sampledb and set the password as ‘SA’ and then run the below commands
java -cp ../lib/hsqldb-jdk8.jar org.hsqldb.server.Server --database.0 file:sampledb/sampledb --dbname.0 sampledb
java -cp hsqldb-jdk8.jar org.hsqldb.util.DatabaseManagerSwing


Run the code in two ways:

a) run the gradle command on terminal that’s gonna run the task ‘execute’
       ./gradlew clean execute

b) run the class FileReadingSolution that have main() method.
