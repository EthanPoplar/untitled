package assessment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the FlightCollection class {@link FlightCollection}.
 */
public class FlightCollectionTest
{
    private Flight flightOne;
    private Flight flightTwo;
    private Flight flightThree;
    private Flight flightFour;

    /**
     * Sets up test data before each test execution.
     */
    @BeforeEach
    @DisplayName("Initialize test data for flights and FlightCollection")
    public void setUp()
    {
        // Initialize an empty flight collection
        FlightCollection.flights = new ArrayList<>();

        // Create airplanes with valid seat configurations (must be between 7-70 and divisible by 7)
        Airplane airplaneOne = new Airplane(1001, "Airbus 3800", 7, 42, 15);
        Airplane airplaneTwo = new Airplane(1010, "Boeing 3800", 14, 35, 15);

        // Adding details for first flight
        Timestamp departureTimeOne = Timestamp.valueOf("2020-01-01 10:00:00");
        Timestamp arrivalTimeOne = Timestamp.valueOf("2020-01-01 13:00:00");
        flightOne = new Flight(101, "Melbourne", "Sydney", "AI234", "Quantas", departureTimeOne, arrivalTimeOne, airplaneOne);

        // Adding details for second flight
        Timestamp departureTimeTwo = Timestamp.valueOf("2020-01-02 10:00:00");
        Timestamp arrivalTimeTwo = Timestamp.valueOf("2020-01-02 16:00:00");
        flightTwo = new Flight(102, "Brisbane", "Melbourne", "BO134", "Quantas", departureTimeTwo, arrivalTimeTwo, airplaneTwo);

        // Adding details for third flight
        Timestamp departureTimeThree = Timestamp.valueOf("2020-02-04 10:00:00");
        Timestamp arrivalTimeThree = Timestamp.valueOf("2020-02-04 18:00:00");
        flightThree = new Flight(103, "Melbourne", "Perth", "BO777", "Quantas", departureTimeThree, arrivalTimeThree, airplaneTwo);

        // Adding details for fourth flight
        Timestamp departureTimeFour = Timestamp.valueOf("2020-05-10 10:00:00");
        Timestamp arrivalTimeFour = Timestamp.valueOf("2020-05-10 19:00:00");
        flightFour = new Flight(104, "Melbourne", "Darwin", "AI200", "Quantas", departureTimeFour, arrivalTimeFour, airplaneOne);

        // Add flights to collection
        FlightCollection.flights.add(flightOne);
        FlightCollection.flights.add(flightTwo);
        FlightCollection.flights.add(flightThree);
        FlightCollection.flights.add(flightFour);
    }

    /**
     * Test to verify flight retrieval using flight ID.
     */
    @Test
    @DisplayName("Retrieve flight by Flight ID")
    public void testGetFlightInfoWithFlightID()
    {
        Flight actualFlight = FlightCollection.getFlightInfo(104);
        assertEquals(flightFour, actualFlight);
    }

    /**
     * Test to verify flight retrieval using departure and arrival cities.
     */
    @Test
    @DisplayName("Retrieve flight by Departure and Arrival Cities")
    public void testGetFlightInfoWithTwoCities()
    {
        Flight actualFlight = FlightCollection.getFlightInfo("Melbourne", "Sydney");
        System.out.println(actualFlight + " " + flightFour);
        assertEquals(flightOne, actualFlight);
    }

    /**
     * Test to verify flight retrieval using a single city - departure city.
     * Note: The getFlightInfo(String city) method finds flights WHERE depart_from = city
     */
    @Test
    @DisplayName("Retrieve flight by Single City (Departure City)")
    public void testGetFLightInfoWithOneCity()
    {
        Flight actualFlight = FlightCollection.getFlightInfo("Melbourne");
        assertEquals(flightFour, actualFlight);
    }
}