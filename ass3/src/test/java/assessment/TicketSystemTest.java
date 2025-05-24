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
}