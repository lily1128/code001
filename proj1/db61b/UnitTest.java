package db61b;

import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

/**
 * The suite of all JUnit tests for the qirkat package.
 *
 * @author P. N. Hilfinger
 */
public class UnitTest {

    public static void main(String[] ignored) {
        /* textui.runClasses(); */
    }

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    @Test
    public void testAdd() {
        String[] name = new String[]{"Name", "Nationality"};
        Table t = new Table(name);
        t.add(new String[]{"Iordache", "Romania"});
        t.add(new String[]{"Komova", "Russia"});
    }

    @Test
    public void testSelect() {
        String[] name = new String[]{"Name", "Year", "Contest"};
        Table t = new Table(name);
        t.add(new String[]{"Iordache", "2012", "Olympics"});
        t.add(new String[]{"Iordache", "2013", "World Championships"});
        t.add(new String[]{"Iordache", "2014", "World Championships"});
        t.add(new String[]{"Iordache", "2015", "World Championships"});
        t.add(new String[]{"Komova", "2011", "World Championships"});
        t.add(new String[]{"Komova", "2012", "Olympics"});
        t.add(new String[]{"Komova", "2015", "World Championships"});
        List<String> x = new ArrayList<>();
        List<Condition> y = new ArrayList<>();
        x.add("Name");
        x.add("Year");
        y.add(new Condition(new Column("Year", t), "=", "2012"));
        t.select(x, y);
    }


}
