import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by vajrayogini on 3/2/16.
 */
public class PeopleTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        People.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE people");
        conn.close();
    }

    @Test
    public void testPerson() throws SQLException {
        Connection conn = startConnection();
        People.insertPerson(conn, "Paige", "H", "h@gmail.com", "France", "55");
        Person person = People.selectPerson(conn, 1);
        endConnection(conn);
        assertTrue(person != null);
    }

    @Test
    public void testPeople() throws SQLException {
        Connection conn = startConnection();
        People.insertPerson(conn, "Paige", "H", "herbmama@gmail.com", "US", "4");
        People.insertPerson(conn, "Bob", "L", "so@gmail.com", "France", "5");
        ArrayList<Person> persons = People.selectPeople(conn);
        endConnection(conn);
        assertTrue(persons.size() == 2);


    }
}