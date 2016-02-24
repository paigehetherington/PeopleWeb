import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by vajrayogini on 2/24/16.
 */
public class People {

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Person> people = new ArrayList<>();
        readFile(people);

        // ArrayList<Person> firstPeople = new ArrayList<>();


        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;
                    if (offset != null) {
                        offsetNum = Integer.valueOf(offset);
                    }


                    ArrayList<Person> firstPeople = new ArrayList<>(people.subList(offsetNum, 20 + offsetNum));

                    HashMap m = new HashMap();
                    m.put("people", firstPeople);
                    m.put("number", offsetNum + 20);
                    m.put("previous", offsetNum - 20);
                    boolean showPrevious = false;
                    if (offsetNum >= 20) {
                        showPrevious = true;
                    }
                    m.put("showPrevious", showPrevious);

                    boolean showNext = false;
                    if (offsetNum < 980) {
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
        scanner.nextLine();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(p);
        }

    }
}//end class

