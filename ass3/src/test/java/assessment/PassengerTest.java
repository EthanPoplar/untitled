package assessment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class PassengerTest
{
    // 1.
    @Test
    @DisplayName("All passenger fields are required")
    public void testAllFieldsPresent()
    {
        Object[][] cases = {
                // firstName, secondName, age, gender,      email,              phoneNumber, passport,  cardNumber,   securityCode
                {   null,      "Doe",      30,  "Male",    "a@b.com",        "0423456789", "P1234567", "4111111111111111", 123 },
                {  "John",      null,      30,  "Male",    "a@b.com",        "0423456789", "P1234567", "4111111111111111", 123 },
                {  "John",     "Doe",       0,  "Male",    "a@b.com",        "0423456789", "P1234567", "4111111111111111", 123 },
                {  "John",     "Doe",      30,   null,     "a@b.com",        "0423456789", "P1234567", "4111111111111111", 123 },
                {  "John",     "Doe",      30,  "Male",     null,            "0423456789", "P1234567", "4111111111111111", 123 },
                {  "John",     "Doe",      30,  "Male",    "a@b.com",          null,      "P1234567", "4111111111111111", 123 },
                {  "John",     "Doe",      30,  "Male",    "a@b.com",        "0423456789",     null,     "4111111111111111", 123 },
                {  "John",     "Doe",      30,  "Male",    "a@b.com",        "0423456789", "P1234567",       null,           123 },
                {  "John",     "Doe",      30,  "Male",    "a@b.com",        "0423456789", "P1234567", "4111111111111111",    0 }
        };

        for (Object[] params : cases) {
            String firstName    = (String) params[0];
            String secondName   = (String) params[1];
            int    age          = (Integer) params[2];
            String gender       = (String) params[3];
            String email        = (String) params[4];
            String phoneNumber  = (String) params[5];
            String passport     = (String) params[6];
            String cardNumber   = (String) params[7];
            int    securityCode = (Integer) params[8];

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Passenger(
                            firstName,
                            secondName,
                            age,
                            gender,
                            email,
                            phoneNumber,
                            passport,
                            cardNumber,
                            securityCode
                    )
            );
            assertEquals("All fields are required", ex.getMessage());
        }
    }

    // 2.
    @Test
    @DisplayName("Check if the phone number is valid")
    public void testPhoneNumberValid()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Passenger("John","Doe",30,"Male","a@b.com","+65 55645645","P1234567", "4111111111111111", 123)
        );
        assertEquals("Invalid phone number", ex.getMessage());
    }

    // 3.
    @Test
    @DisplayName("Save phone number in single format")
    public void testPhoneNumberFormat()
    {
        Passenger pg = new Passenger("John","Doe",30,"Male","a@b.com","0455 645 645","P1234567", "4111111111111111", 123);
        assertEquals("+61 455 645 645", pg.getPhoneNumber());
    }

    // 4.
    @Test
    @DisplayName("Email format check")
    public void emailFormatCheck()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Passenger("John","Doe",30,"Male","a.c@cvc.com","+61 55645645","P1234567", "4111111111111111", 123)
        );
        assertEquals("Invalid email format", ex.getMessage());
    }

    // 5.
    @Test
    @DisplayName("Passport number format check")
    public void passportNumberFormatCheck()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Passenger("John","Doe",30,"Male","ac@cvc.com","+61 55645645","K345678", "4111111111111111", 123)
        );
        assertEquals("Invalid passport number format", ex.getMessage());
    }
}
