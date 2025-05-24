package assessment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonTest
{
    // 1.
    @Test
    @DisplayName("All fields are required for person")
    public void testAllFieldsPresent()
    {
        Object[][] cases = {
                {null, "Rhoton", 23, "Male"},
                {"James", null, 23, "Male"},
                {"James", "Rhoton", 0, "Male"},
                {"James", "Rhoton", 23, null}
        };

        for (Object[] params : cases) {
            String fname = (String) params[0];
            String lname = (String) params[1];
            int age = (Integer) params[2];
            String gender = (String) params[3];

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Person(fname,lname,age,gender){}
            );
            assertEquals("All fields are required", ex.getMessage());
        }
    }

    // 2.
    @Test
    @DisplayName("Test gender outside field gives error")
    public void testSetGender()
    {
        Person p = new Person() {};
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.setGender("M")
        );
        assertEquals("Gender field options must be provided", ex.getMessage());
    }

    @Test
    @DisplayName("Test gender outside field gives error")
    public void testGenderWhileCreation()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Person("John","Law",32,"NA"){}
        );
        assertEquals("Gender field options must be provided", ex.getMessage());
    }

    // 3.
    @Test
    @DisplayName("Test the first letter of first name in creation")
    public void testFirstNameInCreation()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Person("1John","Law",32,"Male"){}
        );
        assertEquals("First name must start with letter", ex.getMessage());
    }

    @Test
    @DisplayName("Test the first letter of second name in creation")
    public void testSecondNameInCreation()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Person("John","@Law",32,"Male"){}
        );
        assertEquals("Second name must start with letter", ex.getMessage());
    }

    @Test
    @DisplayName("Test the first letter in setter of first name")
    public void testSetFirstName()
    {
        Person p = new Person() {};
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.setFirstName("@john")
        );
        assertEquals("First name must start with letter", ex.getMessage());
    }

    @Test
    @DisplayName("Test the first letter in setter of second name")
    public void testSetSecondName()
    {
        Person p = new Person() {};
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.setSecondName("@Law")
        );
        assertEquals("Second name must start with letter", ex.getMessage());
    }
}
