package dbhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * this class is used for creating the db connections
 * and closing it and also for error handling
 *
 * @author Pooja Singla
 */
public class JDBCUtils
{
    private static String jdbcURL = "jdbc:hsqldb:hsql://localhost/sampledb;ifexists=true";
    private static String jdbcUsername = "SA";
    private static String jdbcPassword = "SA";
    private static Connection connection;

    /**
     * this method will create the db connection
     *
     * @return return the db connection val
     */
    public static Connection getConnection()
    {
        try
        {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * thie method is printing the sql exception message
     *
     * @param ex sql exception
     */

    public static void printSQLException(SQLException ex)
    {
        for (Throwable e : ex)
        {
            if (e instanceof SQLException)
            {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null)
                {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    /**
     * this method is going to close the db connection
     *
     * @throws SQLException sql exception
     */
    public static void closeDBConnection() throws SQLException
    {
        try
        {
            // Close connection
            if (connection != null)
            {
                connection.close();

            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }
}

