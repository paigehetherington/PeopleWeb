import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by vajrayogini on 2/24/16.
 */
public class People {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE IF NOT EXISTS people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip_address VARCHAR)");
    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email, String country, String ipAddress) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES(NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ipAddress);
        stmt.execute();


    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id=?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String firstName = results.getString("people.first_name");
            String lastName = results.getString("people.last_name");
            String email = results.getString("people.email");
            String country = results.getString("people.country");
            String ipAddress = results.getString("people.ip_address");
            return new Person(id, firstName, lastName, email, country, ipAddress);
        }

            return null;

    }

    public static void populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("people.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine(); //skips first line
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            insertPerson(conn, columns[1], columns[2], columns[3], columns[4], columns[5] );

        }
    }

    public static ArrayList<Person> selectPeople(Connection conn) throws SQLException {
        ArrayList<Person> persons = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String firstName = results.getString("people.first_name");
            String lastName = results.getString("people.last_name");
            String email = results.getString("people.email");
            String country = results.getString("people.country");
            String ipAddress = results.getString("people.ip_address");
            Person person = new Person(id, firstName, lastName, email, country, ipAddress);
            persons.add(person);
        }
        return persons;
    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        ArrayList<Person> people = new ArrayList<>();
        readFile(people);

        // ArrayList<Person> firstPeople = new ArrayList<>();

        Spark.externalStaticFileLocation("public");
        //Spark.staticFileLocation("public");

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;
                    if (offset != null) {
                        offsetNum = Integer.valueOf(offset);
                    }


                    ArrayList<Person> twentyPeople = new ArrayList<>(people.subList(offsetNum, Math.min(20, (people.size() - offsetNum)) + offsetNum));
                        //math.min for either 20 or remainder over 1000 if not equal to 20
                    HashMap m = new HashMap();
                    m.put("people", twentyPeople);
                    m.put("next", offsetNum + 20);
                    m.put("previous", offsetNum - 20);
                    boolean showPrevious = false;
                    if (offsetNum >= 20) {
                        showPrevious = true;
                    }
                    m.put("showPrevious", showPrevious);

                    boolean showNext = false;
                    if (offsetNum < people.size() - 20) {
                        showNext = true;
                    }
                    m.put("showNext", showNext);

                    return new ModelAndView(m, "home.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/person",
                ((request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));
                    HashMap m = new HashMap();
                    Person person = people.get(id -1);
                    m.put("person", person);
                    return new ModelAndView(m, "person.html");

                }),
                new MustacheTemplateEngine()
        );

    }
    public static void readFile(ArrayList<Person> people) throws FileNotFoundException {
        File f = new File("people.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine(); //skips first line
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(p);
        }

    }
}

