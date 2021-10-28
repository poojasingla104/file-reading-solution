
import dbhandling.HSqlDBCRUDs;
import dbhandling.JDBCUtils;
import org.junit.Test;

import java.sql.SQLException;

/**
 * unit test cases for checking utility functions on db handling
 */
public class DbUnitTestCases
{
    private static final String createTableSQL =
            "create table IF NOT EXISTS eventDetails (\r\n" + "  eventId  varchar(25),\r\n"
                    + "  eventDuration varchar(20),\r\n" + "  type varchar(20),\r\n" + "  host varchar(10),\r\n"
                    + "  alert boolean \r\n" + "  );";

    private static final String insertTableSQL =
            "INSERT INTO eventDetails" + "  (eventId, eventDuration, type, host, alert) VALUES " + " (?, ?, ?, ?, ?);";

    private static final String query = "select eventId, eventDuration, type, host, alert FROM eventDetails";

    private static final String deleteTableSQL = "delete from eventDetails";
    private static final String dropTableSQL = "drop table eventDetails";

    @Test
    public void testH2DB() throws SQLException
    {
        new HSqlDBCRUDs().dropTable(dropTableSQL);
        new HSqlDBCRUDs().createTable(createTableSQL);
        new HSqlDBCRUDs().insertRecord(insertTableSQL, "scsmbstgrb", "3ms", "APPLICATION_LOG", "12345", true);
        new HSqlDBCRUDs().readTable(query);
        new HSqlDBCRUDs().deleteRecord(deleteTableSQL);
        JDBCUtils.closeDBConnection();
    }
}