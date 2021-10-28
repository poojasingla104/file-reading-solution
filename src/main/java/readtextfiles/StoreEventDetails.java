package readtextfiles;

import dbhandling.HSqlDBCRUDs;
import jsonparsing.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is for handling and storing the event details
 * into db
 *
 * @author Pooja Singla
 */
public class StoreEventDetails
{
    private static final Logger logger = LoggerFactory.getLogger(StoreEventDetails.class);

    private EventDetailsClass eventDetails = new EventDetailsClass();
    private static ConcurrentHashMap<String, EventDetailsClass> eventDetailsMap = new ConcurrentHashMap<>();
    private static Map<String, EventDetailsClass> timeStamps = new HashMap();
    private static long maxDiff = -1;
    private static String longestEvent;
    private static final String insertTableSQL =
            "INSERT INTO eventDetails" + "  (eventId, eventDuration, type, host, alert) VALUES " + " (?, ?, ?, ?, ?);";

    /**
     * Method to store the event details into map from text json
     */
    public void storeEventDetails(String json) throws IOException, SQLException
    {
        EventDetailsClass response = SerializationUtil.convertJsonToPOJO(json, EventDetailsClass.class);
        if (response.getId() != null)
        {
            eventDetails.setId(response.getId());
            eventDetails.setState(response.getState());
            eventDetails.setType(response.getType());
            eventDetails.setHost(response.getHost());
            eventDetails.setTimestamp(response.getTimestamp());
        }

        //store the event details into map for further processing
        storeIntoMapAndDB();
        logger.debug("map contains {} ", eventDetailsMap);
    }

    /**
     * store the event details into map for further processing
     * and also store the event details into DB
     *
     * @throws SQLException
     */
    private void storeIntoMapAndDB() throws SQLException
    {
        long diff = 0;
        EventDetailsClass val = eventDetailsMap.getOrDefault(eventDetails.getId(),
                new EventDetailsClass("1", null, null, null, 0));

        if (val.getId().equals("1"))
        {
            eventDetailsMap.put(eventDetails.getId(),
                    new EventDetailsClass(eventDetails.getId(), eventDetails.getState(), eventDetails.getType(),
                                                 eventDetails.getHost(), eventDetails.getTimestamp()));
        }
        else if (val.getState().equalsIgnoreCase(eventDetails.getState()))
        {
            eventDetailsMap.put(eventDetails.getId(),
                    new EventDetailsClass(eventDetails.getId(), eventDetails.getState(), eventDetails.getType(),
                                                 eventDetails.getHost(), eventDetails.getTimestamp()));
        }
        else
        {
            timeStamps.put(val.getId(), new EventDetailsClass(val.getId(), val.getState(), val.getType(), val.getHost(),
                                                                     val.getTimestamp()));
            eventDetailsMap.put(eventDetails.getId(),
                    new EventDetailsClass(eventDetails.getId(), eventDetails.getState(), eventDetails.getType(),
                                                 eventDetails.getHost(), eventDetails.getTimestamp()));
            diff = calTimeStampsDiff(timeStamps, eventDetailsMap);

            //calculate the max difference in timestamps
            calMax(diff);

            //insert records in db
            insertEventsInDB(diff);

            //clean all the object
            cleanup();
        }
    }

    /**
     * helping method for calculating the max difference in timestamps
     *
     * @param diff
     */
    private void calMax(long diff)
    {
        if (diff > maxDiff)
        {
            longestEvent = eventDetails.getId();
            maxDiff = diff;
        }
    }

    /**
     * this method remove the map unnecessary rows or data.
     */
    public void cleanup()
    {
        timeStamps.remove(eventDetails.getId());
        eventDetailsMap.remove(eventDetails.getId());
        eventDetails = null;
    }

    /**
     * helper method for inserting the event records into db
     *
     * @param diff - if diff is greater than 4, alert will be set with true and otherwise
     * @throws SQLException sql exception
     */
    private void insertEventsInDB(long diff) throws SQLException
    {
        if (diff > 4)
        {
            new HSqlDBCRUDs().insertRecord(insertTableSQL, this.eventDetails.getId(), String.valueOf(diff),
                    this.eventDetails.getType(), this.eventDetails.getHost(), true);
        }
        else
        {
            new HSqlDBCRUDs().insertRecord(insertTableSQL, this.eventDetails.getId(), String.valueOf(diff),
                    this.eventDetails.getType(), this.eventDetails.getHost(), false);
        }
    }

    /**
     * helper method is used for calculating the timestamp difference
     *
     * @param map1 map1
     * @param map2 map2
     * @return
     */
    private long calTimeStampsDiff(Map<String, EventDetailsClass> map1, Map<String, EventDetailsClass> map2)
    {
        long smallTS = 0;
        long bigllTS = 0;
        for (Map.Entry<String, EventDetailsClass> entry : map1.entrySet())
        {

            // Check if the current key exists in the 2nd map
            if (map2.containsKey(entry.getKey()))
            {
                if (entry.getValue().getState().equalsIgnoreCase("STARTED"))
                {
                    smallTS = entry.getValue().getTimestamp();
                }
                else if (entry.getValue().getState().equalsIgnoreCase("FINISHED"))
                {
                    bigllTS = entry.getValue().getTimestamp();
                }

                if (map2.get(entry.getKey()).getState().equalsIgnoreCase("STARTED"))
                {
                    smallTS = map2.get(entry.getKey()).getTimestamp();
                }
                else if (map2.get(entry.getKey()).getState().equalsIgnoreCase("FINISHED"))
                {
                    bigllTS = map2.get(entry.getKey()).getTimestamp();
                }
            }
        }
        return bigllTS - smallTS;
    }

    public static long returnMax()
    {
        return maxDiff;
    }

    public static String returnLongestEventName()
    {
        return longestEvent;
    }
}