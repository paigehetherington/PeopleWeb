/**
 * Created by vajrayogini on 2/24/16.
 */
public class Person {
    int id;
    String firstName;
    String lastName;
    String email;
    String country;
    String ipAddress;

    @Override
    public String toString() {
        return id + firstName + " " + lastName;
    }

    public Person(int id, String firstName, String lastName, String email, String country, String ipAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.ipAddress = ipAddress;


    }



}

