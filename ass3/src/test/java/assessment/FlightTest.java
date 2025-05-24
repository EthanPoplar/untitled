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

/**
 * Unit tests for validating Flight and Airplane initialization and functionality {@link Flight}.
 */
public class FlightTest {

    /**
     * Test for verifying proper initialization of a Flight object.
     */
    @Test
    @DisplayName("Test Flight Initialization with All Attributes")
    public void testFlightInitialization() {
        // Create an Airplane instance for the flight
        Airplane airplane = new Airplane(1, "Boeing 747", 10, 60, 8);

        // Create timestamps for flight departure and arrival
        Timestamp dateFrom = new Timestamp(Calendar.getInstance().getTimeInMillis());
        Timestamp dateTo = new Timestamp(dateFrom.getTime() + 3600000); // flight duration of 1 hour

        // Create a Flight instance with sample data
        Flight flight = new Flight(100, "New York", "Los Angeles", "AA101", "American Airlines", dateFrom, dateTo, airplane);

        // Verify that the Flight instance has been correctly initialized
        assertEquals(100, flight.getFlightID());
        assertEquals("New York", flight.getDepartTo());
        assertEquals("Los Angeles", flight.getDepartFrom());
        assertEquals("AA101", flight.getCode());
        assertEquals("American Airlines", flight.getCompany());
        assertEquals(dateFrom, flight.getDateFrom());
        assertEquals(dateTo, flight.getDateTo());
        assertEquals(airplane, flight.getAirplane());
    }

    /**
     * Test for verifying the string representation of a Flight object.
     */
    @Test
    @DisplayName("Test Flight toString Method for Correct Output")
    public void testFlightToString() {
        // Create an Airplane instance for the flight
        Airplane airplane = new Airplane(2, "Airbus A380", 12, 58, 10);

        // Define fixed timestamps for consistency
        Timestamp dateFrom = Timestamp.valueOf("2025-03-21 10:00:00");
        Timestamp dateTo = Timestamp.valueOf("2025-03-21 14:00:00");

        // Create a Flight instance with sample data
        Flight flight = new Flight(201, "London", "Paris", "BA202", "British Airways", dateFrom, dateTo, airplane);

        // Get the string representation of the Flight instance
        String result = flight.toString();

        // Check that the string contains key pieces of information from both Flight and Airplane
        assertTrue(result.contains("Airplane"));
        assertTrue(result.contains("London"));
        assertTrue(result.contains("Paris"));
        assertTrue(result.contains("BA202"));
        assertTrue(result.contains("British Airways"));
    }

    //Assessment 2 : Test cases
    // 1.
    @Test
    @DisplayName("All fields are required")
    public void testAllVariablesPresent()
    {
        Airplane mockAirplane = mock(Airplane.class);
        Timestamp tsFrom = Timestamp.valueOf("2025-04-21 12:00:00");
        Timestamp tsTo   = Timestamp.valueOf("2025-04-21 14:00:00");

        Object[][] cases = {
                {  0, "MEL",    "SYD", "FL001", "AirCo", tsFrom, tsTo, mockAirplane },
                {     1, null,    "SYD", "FL001", "AirCo", tsFrom, tsTo, mockAirplane },
                {     1, "MEL",    null, "FL001", "AirCo", tsFrom, tsTo, mockAirplane },
                {     1, "MEL",   "SYD",    null, "AirCo", tsFrom, tsTo, mockAirplane },
                {     1, "MEL",   "SYD", "FL001", null, tsFrom, tsTo, mockAirplane },
                {     1, "MEL",   "SYD", "FL001", "AirCo", null, tsTo, mockAirplane },
                {     1, "MEL",   "SYD", "FL001", "AirCo", tsFrom, null, mockAirplane },
                {     1, "MEL",   "SYD", "FL001", "AirCo", tsFrom, tsTo, null }
        };

        for (Object[] params : cases) {
            int flightID = (Integer) params[0];
            String to = (String) params[1];
            String from = (String) params[2];
            String code = (String) params[3];
            String company = (String) params[4];
            Timestamp dateStr = (Timestamp) params[5];
            Timestamp timeStr = (Timestamp) params[6];
            Airplane plane = (Airplane) params[7];

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Flight(flightID, to, from, code, company, dateStr, timeStr, plane), "The flight inilialization fails for " + Arrays.toString(cases)
            );
            assertEquals("All fields are required", ex.getMessage());
        }
    }


    // 2.

    // Invalid Format
    @Test
    @DisplayName("Date format must be in YYYY-MM-DD format")
    public void testInvalidDateFormat()
    {
        Airplane mockAirplane = mock(Airplane.class);
        Timestamp badTime = mock(Timestamp.class);

        when(badTime.toString()).thenReturn("21-4-2025 12:00:00");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Flight(
                        1, "MEL", "SYD", "FL001", "AirCo",
                        badTime, badTime, mockAirplane
                )
        );
        assertEquals("Date must be in YYYY-MM-DD format", ex.getMessage());
    }

//    // Valid format
//    @Test
//    @DisplayName("Date format must be in YYYY-MM-DD format")
//    public void testValidDateFormat()
//    {
//        Airplane mockAirplane = mock(Airplane.class);
//        Flight mockFlight = mock(Flight.class);
//        Timestamp goodDate = mock(Timestamp.class);
//
//        when(goodDate.toString()).thenReturn("21-04-2025 12:00:00");
//
//        when(mockFlight.getFlightID()).thenReturn(1);
//        when(mockFlight.getDepartTo()).thenReturn("MEL");
//        when(mockFlight.getDepartTo()).thenReturn("SYD");
//        when(mockFlight.getCode()).thenReturn("FL001");
//        when(mockFlight.getCompany()).thenReturn("AirCo");
//        when(mockFlight.getDateFrom()).thenReturn(goodDate);
//        when(mockFlight.getDateTo()).thenReturn(goodDate);
//        when(mockFlight.getAirplane()).thenReturn(mockAirplane);
//
//        assertEquals(mockFlight, ex.getMessage());
//    }
//

    // 3.
    @Test
    @DisplayName("Date format must be in YYYY-MM-DD format")
    public void testTimeFormat()
    {
        Airplane airPlane = mock(Airplane.class);

        // Mock a Timestamp whose toString() has a malformed time
        Timestamp badTime = mock(Timestamp.class);
        when(badTime.toString()).thenReturn("2025-04-21 1:2:3");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Flight(1, "MEL", "SYD", "FL001", "AirCo", badTime, badTime, airPlane
                )
        );
        assertEquals("Time must be in HH:MM:SS format", ex.getMessage());
    }

    // 4.
    @Test
    @DisplayName("Check if same flight is present in the system")
    public void sameFlightInSystem()
    {

        Airplane mockAirplane = mock(Airplane.class);

        Timestamp dateFrom = Timestamp.valueOf("2025-03-21 10:40:20");
        Timestamp dateTo = Timestamp.valueOf("2025-03-21 14:20:30");

        ArrayList <Flight> flightArray1 = new ArrayList<>();
        flightArray1.add(new Flight(200, "London", "Paris", "BA202", "British Airways", dateFrom, dateTo, mockAirplane));
        FlightCollection.addFlights(flightArray1);

        ArrayList <Flight> flightArray2 = new ArrayList<>();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> flightArray2.add(new Flight(200, "London", "Paris", "BA202", "British Airways", dateFrom, dateTo, mockAirplane))
        );
        assertEquals("Flight Already in the system", ex.getMessage());
    }
}
