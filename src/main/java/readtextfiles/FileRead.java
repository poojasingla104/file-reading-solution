package readtextfiles;

import dbhandling.HSqlDBCRUDs;
import dbhandling.JDBCUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static readtextfiles.StoreEventDetails.returnLongestEventName;
import static readtextfiles.StoreEventDetails.returnMax;

/**
 * class which provides the mechanism for reading the text file and storing the event details into DB
 *
 * @author Pooja Singla
 */
public class FileRead
{
    private static final Logger logger = LoggerFactory.getLogger(FileRead.class);

    private static int resultsToWaitFor = 0;

    private static final String dropTableSQL = "drop table eventDetails IF EXISTS";

    private static final String createTableSQL =
            "create table IF NOT EXISTS eventDetails (\r\n" + "  eventId  varchar(25),\r\n"
                    + "  eventDuration varchar(20),\r\n" + "  type varchar(20),\r\n" + "  host varchar(10),\r\n"
                    + "  alert boolean \r\n" + "  );";
    private static final String query = "select eventId, eventDuration, type, host, alert FROM eventDetails";

    public static void main(String[] args) throws SQLException
    {
        HSqlDBCRUDs sql = new HSqlDBCRUDs();

        //drop the existing db table
        sql.dropTable(dropTableSQL);
        //create the event table

        //create table
        sql.createTable(createTableSQL);

        logger.info("Start Parsing the text file");

        File f = new File(Common.getPathToTargetFile(args));
        if (!f.exists() || !f.canRead())
        {
            logger.info("Problem reading file {} ", f);
        }

        int lineCount = 0;
        Instant lineRead = Instant.now();

        try
        {
            LineIterator it = FileUtils.lineIterator(f, "UTF-8");
            while (it.hasNext())
            {
                String line = it.nextLine();
                if (!line.isEmpty())
                {
                    lineCount++;
                    if (lineCount < 10)
                    {
                        // print some of the data so we can see what it looks like - most editors fail to display the file
                        logger.info("line {} printout: {}", (lineCount), line);
                    }

                    incrementLock();
                    if (lineCount % 1000000 == 0)
                    {
                        logger.debug("reading line {} ,fibers running: {} ", lineCount, (resultsToWaitFor - 1));
                        // optimization: as it is possible for I/O to supply new fibers faster than can be completed,
                        // which chokes and stalls the JVM, we pause the line reading if the fiber count is too high.
                        while (resultsToWaitFor > 9999)
                        {
                            try
                            {
                                logger.info("waiting for fiber count to drop, now at: {}", resultsToWaitFor);
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }

                        }
                    }
                    // optimization: reading a line and doing anything with the content is decoupled from each other
                    // and asynchronous. We use Fibers, from the CompletableFuture API
                    CompletableFuture.runAsync(new LineRunnable(line, lineCount)).thenAcceptAsync(
                            whenDone -> decrementLock()).join();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        Instant lineReadDone = Instant.now();
        long millis = Duration.between(lineRead, lineReadDone).toMillis();
        logger.info("{} lines read and supplied in {} milliseconds\n", lineCount, millis);

        // now keep querying our lock until it indicates that all fibers have finished
        int waitCount = 0;
        while (resultsToWaitFor > 0)
        {
            try
            {
                waitCount++;
                if (waitCount % 10 == 0)
                {
                    // give some user feedback
                    logger.info("fibers not done yet: {} ", resultsToWaitFor);
                }
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Instant parsingDone = Instant.now();
        millis = Duration.between(lineReadDone, parsingDone).toMillis();
        logger.info("fibers kept parsing async for (approx) {} milliseconds longer\n", millis);
        logger.info("The longest event is '{}' with the time duration {}", returnLongestEventName(), returnMax());

        //read the table records
        sql.readTable(query);

        //close db connection
        JDBCUtils.closeDBConnection();
    }

    // we need to prevent race conditions on our locking variable
    static synchronized void incrementLock()
    {
        resultsToWaitFor++;
    }

    static synchronized void decrementLock()
    {
        resultsToWaitFor--;
    }

    private static class LineRunnable implements Runnable
    {
        final String input;
        final int index;

        LineRunnable(String line, int indexOfLine)
        {
            input = line;
            index = indexOfLine - 1;
        }

        @Override
        public void run()
        {
            try
            {
                new StoreEventDetails().storeEventDetails(input);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}