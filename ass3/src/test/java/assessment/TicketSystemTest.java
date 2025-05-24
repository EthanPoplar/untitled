package assessment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketSystemTest {

    private TicketSystem ticketSystem;
    private AutoCloseable closeable;

    @Mock
    private Passenger mockPassenger;

    @Mock
    private Ticket mockTicket;

    @Mock
    private Flight mockFlight;

    @Mock
    private Airplane mockAirplane;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // For simulating user input
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        ticketSystem = new TicketSystem();

        // Set up mocks for instance variables in TicketSystem
        ticketSystem.passenger = mockPassenger;
        ticketSystem.ticket = mockTicket;
        ticketSystem.flight = mockFlight;

        // Redirect System.out for testing output
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restore original streams
        System.setOut(originalOut);
        System.setIn(originalIn);

        // Clean up mocks
        closeable.close();

        // Clear captured output
        outContent.reset();
    }

    /**
     * Tests for Requirement 1: Valid city names must be enforced when choosing a ticket
     */

    @Test
    @DisplayName("Valid city names should be accepted")
    void testChooseTicket_ValidCityNames() throws Exception {
        // Arrange
        String validCity1 = "Melbourne";
        String validCity2 = "Sydney";

        // Use spy to avoid calling the actual buyTicket method
        TicketSystem spySystem = spy(ticketSystem);
        doNothing().when(spySystem).buyTicket(anyInt());

        try (MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {
            // Mock FlightCollection to return a flight for valid cities
            when(mockFlight.getAirplane()).thenReturn(mockAirplane);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(validCity1, validCity2))
                    .thenReturn(mockFlight);

            // Set up input simulation for scanner
            String userInput = "1\n"; // Just the ticket ID is needed now
            System.setIn(new ByteArrayInputStream(userInput.getBytes()));
            spySystem.setScanner(new Scanner(System.in));

            // Mock TicketCollection getAllTickets to avoid NullPointerException
            try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {
                mockedTicketCollection.when(TicketCollection::getAllTickets).thenAnswer(invocation -> null);

                // Act - should not throw exception
                assertDoesNotThrow(() -> spySystem.chooseTicket(validCity1, validCity2));

                // Verify FlightCollection was called with valid city names
                mockedFlightCollection.verify(() -> FlightCollection.getFlightInfo(validCity1, validCity2));

                // Verify buyTicket was called with ticket ID 1
                verify(spySystem).buyTicket(1);
            }
        }
    }

    @Test
    @DisplayName("Empty city names should throw IllegalArgumentException")
    void testChooseTicket_EmptyCityNames() {
        // Test null city names
        Exception exception1 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket(null, "Sydney"));
        assertEquals("City names cannot be empty", exception1.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("Melbourne", null));
        assertEquals("City names cannot be empty", exception2.getMessage());

        // Test empty string city names
        Exception exception3 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("", "Sydney"));
        assertEquals("City names cannot be empty", exception3.getMessage());

        Exception exception4 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("Melbourne", "   "));
        assertEquals("City names cannot be empty", exception4.getMessage());
    }

    @Test
    @DisplayName("Invalid city names (with numbers/symbols) should throw IllegalArgumentException")
    void testChooseTicket_InvalidCityNames() {
        Exception exception1 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("Melbourne123", "Sydney"));
        assertEquals("City names must only contain letters and spaces", exception1.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("Melbourne", "Syd@ney"));
        assertEquals("City names must only contain letters and spaces", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class,
                () -> ticketSystem.chooseTicket("Mel-bourne", "Sydney"));
        assertEquals("City names must only contain letters and spaces", exception3.getMessage());
    }

    /**
     * Tests for Requirement 2: If a passenger chooses an already booked ticket it should display an error message
     */

    @Test
    @DisplayName("Choosing an already booked ticket should display error message")
    void testBuyTicket_AlreadyBooked() throws Exception {
        // Arrange
        int bookedTicketId = 123;

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {
            // Create a mock ticket that is already booked (status = true)
            Ticket bookedTicket = mock(Ticket.class);
            when(bookedTicket.ticketStatus()).thenReturn(true);
            Flight mockFlightLocal = mock(Flight.class);
            when(bookedTicket.getFlight()).thenReturn(mockFlightLocal);

            // Configure TicketCollection to return the booked ticket
            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(bookedTicketId))
                    .thenReturn(bookedTicket);

            assertThrows(IllegalStateException.class, () -> ticketSystem.buyTicket(bookedTicketId));
        }
    }

    @Test
    @DisplayName("Ticket not found should print error message")
    void testBuyTicket_TicketNotFound() throws Exception {
        // Arrange
        int nonExistentTicketId = 999;

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {
            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(nonExistentTicketId))
                    .thenReturn(null);

            // Act
            ticketSystem.buyTicket(nonExistentTicketId);

            // Assert
            assertTrue(outContent.toString().contains("This ticket does not exist."));
        }
    }

    @Test
    @DisplayName("Ticket with no flight should print error message")
    void testBuyTicket_NoAssociatedFlight() throws Exception {
        // Arrange
        int ticketId = 100;

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {
            Ticket ticketWithNoFlight = mock(Ticket.class);
            when(ticketWithNoFlight.ticketStatus()).thenReturn(false);
            when(ticketWithNoFlight.getFlight()).thenReturn(null);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(ticketId))
                    .thenReturn(ticketWithNoFlight);

            // Act
            ticketSystem.buyTicket(ticketId);

            // Assert
            assertTrue(outContent.toString().contains("This ticket has no associated flight."));
        }
    }

    /**
     * Tests for Requirement 3: Comprehensive validation for passenger, flight, and ticket information
     */

    @Test
    @DisplayName("No seats available should throw IllegalStateException")
    void testBuyTicket_NoSeatsAvailable() throws Exception {
        // Arrange
        int ticketId = 100;

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class);
             MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {

            // Create mock objects
            Ticket availableTicket = mock(Ticket.class);
            Flight flight = mock(Flight.class);
            Airplane airplane = mock(Airplane.class);

            // Set up ticket
            when(availableTicket.ticketStatus()).thenReturn(false);
            when(availableTicket.getFlight()).thenReturn(flight);
            when(availableTicket.getClassVip()).thenReturn(false); // Economy ticket

            // Set up flight
            when(flight.getFlightID()).thenReturn(1);
            when(flight.getAirplane()).thenReturn(airplane);

            // Set up airplane with no economy seats
            when(airplane.getEconomySitsNumber()).thenReturn(0);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(ticketId))
                    .thenReturn(availableTicket);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(1))
                    .thenReturn(flight);

            // Act & Assert
            Exception exception = assertThrows(IllegalStateException.class,
                    () -> ticketSystem.buyTicket(ticketId));
            assertEquals("No economy class seats available.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Valid passenger information should be accepted")
    void testBuyTicket_ValidPassengerInfo() throws Exception {
        // Arrange
        int ticketId = 100;
        String validInput = "John\nDoe\n30\nMale\njohn@example.com\n+61 412345678\nP1234567\n1\n4111111111111111\n123\n";

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class);
             MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {

            // Create mock objects
            Ticket availableTicket = mock(Ticket.class);
            Flight flight = mock(Flight.class);
            Airplane airplane = mock(Airplane.class);

            // Set up ticket
            when(availableTicket.ticketStatus()).thenReturn(false);
            when(availableTicket.getFlight()).thenReturn(flight);
            when(availableTicket.getClassVip()).thenReturn(false);
            when(availableTicket.getPrice()).thenReturn(1000);

            // Set up flight
            when(flight.getFlightID()).thenReturn(1);
            when(flight.getAirplane()).thenReturn(airplane);

            // Set up airplane with available seats
            when(airplane.getEconomySitsNumber()).thenReturn(50);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(ticketId))
                    .thenReturn(availableTicket);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(1))
                    .thenReturn(flight);

            // Set up scanner with valid input
            ticketSystem.setScanner(new Scanner(validInput));

            // Act
            assertDoesNotThrow(() -> ticketSystem.buyTicket(ticketId));

            // Verify ticket was marked as booked
            verify(availableTicket).setTicketStatus(true);

            // Verify seat count was decremented
            verify(airplane).setEconomySitsNumber(49);
        }
    }

    /**
     * Tests for Requirement 4: Correct price information must be displayed
     */

    @Test
    @DisplayName("Price should be displayed after discounts and taxes")
    void testBuyTicket_PriceDisplay() throws Exception {
        // Arrange
        int ticketId = 100;
        int originalPrice = 1000;
        int finalPrice = 1120; // After service tax

        String validInput = "John\nDoe\n30\nMale\njohn@example.com\n+61 412345678\nP1234567\n1\n4111111111111111\n123\n";

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class);
             MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {

            // Create mock objects
            Ticket availableTicket = mock(Ticket.class);
            Flight flight = mock(Flight.class);
            Airplane airplane = mock(Airplane.class);

            // Set up ticket
            when(availableTicket.ticketStatus()).thenReturn(false);
            when(availableTicket.getFlight()).thenReturn(flight);
            when(availableTicket.getClassVip()).thenReturn(false);
            when(availableTicket.getPrice()).thenReturn(originalPrice).thenReturn(finalPrice);

            // Set up flight
            when(flight.getFlightID()).thenReturn(1);
            when(flight.getAirplane()).thenReturn(airplane);

            // Set up airplane
            when(airplane.getEconomySitsNumber()).thenReturn(50);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(ticketId))
                    .thenReturn(availableTicket);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(1))
                    .thenReturn(flight);

            ticketSystem.setScanner(new Scanner(validInput));

            // Act
            ticketSystem.buyTicket(ticketId);

            // Assert - check that price is displayed
            String output = outContent.toString();
            assertTrue(output.contains("Your bill: $" + finalPrice));
        }
    }

    /**
     * Tests for transfer flight booking
     */

    @Test
    @DisplayName("Transfer flight booking with valid tickets")
    void testBuyTicket_TransferFlight() throws Exception {
        // Arrange
        int firstTicketId = 100;
        int secondTicketId = 200;

        String validInput = "John\nDoe\n30\nMale\njohn@example.com\n+61 412345678\nP1234567\n1\n4111111111111111\n123\n";

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class);
             MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {

            // Create mock objects for first ticket
            Ticket firstTicket = mock(Ticket.class);
            Flight firstFlight = mock(Flight.class);
            Airplane firstAirplane = mock(Airplane.class);

            // Create mock objects for second ticket
            Ticket secondTicket = mock(Ticket.class);
            Flight secondFlight = mock(Flight.class);
            Airplane secondAirplane = mock(Airplane.class);

            // Set up first ticket
            when(firstTicket.ticketStatus()).thenReturn(false);
            when(firstTicket.getFlight()).thenReturn(firstFlight);
            when(firstTicket.getClassVip()).thenReturn(false);
            when(firstTicket.getPrice()).thenReturn(500);

            // Set up second ticket
            when(secondTicket.ticketStatus()).thenReturn(false);
            when(secondTicket.getFlight()).thenReturn(secondFlight);
            when(secondTicket.getClassVip()).thenReturn(true); // Business class
            when(secondTicket.getPrice()).thenReturn(800);

            // Set up flights
            when(firstFlight.getFlightID()).thenReturn(1);
            when(firstFlight.getAirplane()).thenReturn(firstAirplane);
            when(secondFlight.getFlightID()).thenReturn(2);
            when(secondFlight.getAirplane()).thenReturn(secondAirplane);

            // Set up airplanes
            when(firstAirplane.getEconomySitsNumber()).thenReturn(50);
            when(secondAirplane.getBusinessSitsNumber()).thenReturn(10);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(firstTicketId))
                    .thenReturn(firstTicket);
            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(secondTicketId))
                    .thenReturn(secondTicket);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(1))
                    .thenReturn(firstFlight);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(2))
                    .thenReturn(secondFlight);

            ticketSystem.setScanner(new Scanner(validInput));

            // Act
            ticketSystem.buyTicket(firstTicketId, secondTicketId);

            // Assert
            verify(firstTicket).setTicketStatus(true);
            verify(secondTicket).setTicketStatus(true);
            verify(firstAirplane).setEconomySitsNumber(49);
            verify(secondAirplane).setBusinessSitsNumber(9);

            String output = outContent.toString();
            assertTrue(output.contains("Your total bill: $1300")); // 500 + 800
        }
    }

    @Test
    @DisplayName("Transfer flight booking with one ticket already booked")
    void testBuyTicket_TransferFlight_OneAlreadyBooked() throws Exception {
        // Arrange
        int firstTicketId = 100;
        int secondTicketId = 200;

        try (MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {
            // Create mock objects
            Ticket firstTicket = mock(Ticket.class);
            Ticket secondTicket = mock(Ticket.class);
            Flight mockFlightLocal = mock(Flight.class);

            // First ticket is available
            when(firstTicket.ticketStatus()).thenReturn(false);
            when(firstTicket.getFlight()).thenReturn(mockFlightLocal);

            // Second ticket is already booked
            when(secondTicket.ticketStatus()).thenReturn(true);
            when(secondTicket.getFlight()).thenReturn(mockFlightLocal);

            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(firstTicketId))
                    .thenReturn(firstTicket);
            mockedTicketCollection.when(() -> TicketCollection.getTicketInfo(secondTicketId))
                    .thenReturn(secondTicket);

            // Act & Assert
            Exception exception = assertThrows(IllegalStateException.class,
                    () -> ticketSystem.buyTicket(firstTicketId, secondTicketId));
            assertEquals("One or both tickets are already booked.", exception.getMessage());
        }
    }

    /**
     * Tests for showTicket method
     */

    @Test
    @DisplayName("Show ticket displays correct information")
    void testShowTicket() {
        // Arrange
        Flight mockFlightLocal = mock(Flight.class);
        when(mockFlightLocal.getDepartFrom()).thenReturn("Melbourne");
        when(mockFlightLocal.getDepartTo()).thenReturn("Sydney");

        // Set up ticket with flight information
        Ticket mockTicketLocal = mock(Ticket.class);
        when(mockTicketLocal.toString()).thenReturn("Mock ticket details");

        // Create a field named 'flight' in the mock ticket
        mockTicketLocal.flight = mockFlightLocal;

        // Set the ticket in the ticketSystem
        ticketSystem.ticket = mockTicketLocal;

        // Act
        ticketSystem.showTicket();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("You have bought a ticket for flight Melbourne - Sydney"));
        assertTrue(output.contains("Mock ticket details"));
    }

    @Test
    @DisplayName("Show ticket handles null flight information gracefully")
    void testShowTicket_NullFlight() {
        // Arrange - create a new mock ticket with null flight
        Ticket mockTicketLocal = mock(Ticket.class);
        mockTicketLocal.flight = null;
        when(mockTicketLocal.toString()).thenReturn("Mock ticket details");

        // Set the ticket in the ticketSystem
        ticketSystem.ticket = mockTicketLocal;

        // Act
        ticketSystem.showTicket();

        // Assert
        assertTrue(outContent.toString().contains("No flight details available"));
    }

    @Test
    @DisplayName("Show ticket handles null ticket gracefully")
    void testShowTicket_NullTicket() {
        // Arrange
        ticketSystem.ticket = null;

        // Act
        ticketSystem.showTicket();

        // Assert
        assertTrue(outContent.toString().contains("No flight details available"));
    }

    /**
     * Integration tests for chooseTicket with transfer flights
     */

    @Test
    @DisplayName("Choose ticket finds valid transfer route")
    void testChooseTicket_ValidTransferRoute() throws Exception {
        // Arrange
        String city1 = "Melbourne";
        String city2 = "Perth";
        String transferCity = "Sydney";

        TicketSystem spySystem = spy(ticketSystem);

        try (MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class);
             MockedStatic<TicketCollection> mockedTicketCollection = mockStatic(TicketCollection.class)) {

            // No direct flight
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(city1, city2))
                    .thenReturn(null);

            // Create flights for transfer route
            Flight flight1 = mock(Flight.class); // Melbourne to Sydney
            Flight flight2 = mock(Flight.class); // Sydney to Perth

            when(flight1.getFlightID()).thenReturn(1);
            when(flight2.getFlightID()).thenReturn(2);
            when(flight2.getDepartFrom()).thenReturn(transferCity);
            when(flight2.getDepartTo()).thenReturn(city2);

            // Create available tickets
            Ticket ticket1 = mock(Ticket.class);
            Ticket ticket2 = mock(Ticket.class);

            when(ticket1.getTicket_id()).thenReturn(100);
            when(ticket1.getFlight()).thenReturn(flight1);
            when(ticket1.ticketStatus()).thenReturn(false);

            when(ticket2.getTicket_id()).thenReturn(200);
            when(ticket2.getFlight()).thenReturn(flight2);
            when(ticket2.ticketStatus()).thenReturn(false);

            ArrayList<Flight> allFlights = new ArrayList<>();
            allFlights.add(flight1);
            allFlights.add(flight2);

            ArrayList<Ticket> allTickets = new ArrayList<>();
            allTickets.add(ticket1);
            allTickets.add(ticket2);

            mockedFlightCollection.when(FlightCollection::getFlights).thenReturn(allFlights);
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(city1, transferCity))
                    .thenReturn(flight1);
            mockedTicketCollection.when(TicketCollection::getTickets).thenReturn(allTickets);

            // Set up scanner to confirm booking
            String userInput = "1\n"; // Confirm transfer booking
            spySystem.setScanner(new Scanner(userInput));

            doNothing().when(spySystem).buyTicket(anyInt(), anyInt());

            // Act
            assertDoesNotThrow(() -> spySystem.chooseTicket(city1, city2));

            // Assert
            verify(spySystem).buyTicket(100, 200);
            String output = outContent.toString();
            assertTrue(output.contains("Transfer route found"));
            assertTrue(output.contains(transferCity));
        }
    }

    @Test
    @DisplayName("Choose ticket with no possible route throws exception")
    void testChooseTicket_NoRoute() throws Exception {
        // Arrange
        String city1 = "Melbourne";
        String city2 = "London"; // No route possible

        try (MockedStatic<FlightCollection> mockedFlightCollection = mockStatic(FlightCollection.class)) {
            // No direct flight
            mockedFlightCollection.when(() -> FlightCollection.getFlightInfo(city1, city2))
                    .thenReturn(null);

            // No flights to destination
            mockedFlightCollection.when(FlightCollection::getFlights)
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            Exception exception = assertThrows(IllegalStateException.class,
                    () -> ticketSystem.chooseTicket(city1, city2));
            assertEquals("No possible route found", exception.getMessage());
        }
    }
}