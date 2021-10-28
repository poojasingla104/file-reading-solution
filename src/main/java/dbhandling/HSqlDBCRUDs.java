package dbhandling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * the class is created for handling
 * CRUD (Create, Retrieve, Update and Delete)
 * operations with the HSQLDB database
 *
 * @author Pooja Singla
 */
public class HSqlDBCRUDs
{
    private static final Connection connection = JDBCUtils.getConnection();

    /**
     * method is used for creating the db table
     *
     * @param createTableSQL create table query
     * @throws SQLException sql exception
     */
    public void createTable(String createTableSQL) throws SQLException
    {
        System.out.println(createTableSQL);
        try
        {
            //create a statement using connection object
            Statement statement = connection.createStatement();

            // execute the query or update query
            statement.execute(createTableSQL);
        }
        catch (SQLException e)
        {
            // print SQL exception information
            JDBCUtils.printSQLException(e);
        }
    }

    /**
     * method is used for reading the db table
     *
     * @param query read query
     * @throws SQLException sql exception
     */
    public void readTable(String query) throws SQLException
    {
        try
        {
            //create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            //execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("\nDB Results: ");

            //process the ResultSet object.
            while (rs.next())
            {
                String eventId = rs.getString("eventId");
                String eventDuration = rs.getString("eventDuration");
                String type = rs.getString("type");
                String host = rs.getString("host");
                boolean alert = rs.getBoolean("alert");
                System.out.println(eventId + ", " + eventDuration + ", " + type + ", " + host + ", " + alert);
            }
        }
        catch (SQLException e)
        {
            JDBCUtils.printSQLException(e);
        }
    }

    /**
     * method is used for inserting the records in the db table
     *
     * @param insertTableSQL insert query
     * @param eventId        event id
     * @param eventDuration  event duration
     * @param type           event type
     * @param host           host
     * @param alert          alert
     * @throws SQLException sql exception
     */
    public void insertRecord(String insertTableSQL, String eventId, String eventDuration, String type, String host,
                                    boolean alert) throws SQLException
    {
        System.out.println(insertTableSQL);
        try
        {
            //create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
            preparedStatement.setString(1, eventId);
            preparedStatement.setString(2, eventDuration);
            preparedStatement.setString(3, type);
            preparedStatement.setString(4, host);
            preparedStatement.setBoolean(5, alert);

            System.out.println(preparedStatement);
            //Execute the query or update query
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            // print SQL exception information
            JDBCUtils.printSQLException(e);
        }
    }

    /**
     * method is used for deleting the records from the db table
     *
     * @param deleteTableSQL delete query
     * @throws SQLException sql exception
     */
    public void deleteRecord(String deleteTableSQL) throws SQLException
    {
        System.out.println(deleteTableSQL);
        try
        {
            //create a statement using connection object
            Statement statement = connection.createStatement();

            //execute the query or update query
            statement.execute(deleteTableSQL);
            System.out.println("record is deleted.");
        }
        catch (SQLException e)
        {
            // print SQL exception information
            JDBCUtils.printSQLException(e);
        }
    }

    /**
     * method is used for dropping the db table
     *
     * @param dropTableSQL drop table query
     * @throws SQLException sql exception
     */
    public void dropTable(String dropTableSQL) throws SQLException
    {
        System.out.println(dropTableSQL);
        try
        {
            //create a statement using connection object
            Statement statement = connection.createStatement();
            //execute the query or update query
            statement.execute(dropTableSQL);
        }
        catch (SQLException e)
        {
            // print SQL exception information
            JDBCUtils.printSQLException(e);
        }
    }
}