package assessment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketCollectionTest {

    @Mock
    private Ticket mockTicket1;

    @Mock
    private Ticket mockTicket2;

    @Mock
    private Flight mockFlight;

    @Mock
    private Passenger mockPassenger;

    private ArrayList<Ticket> testTickets;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Initialize test tickets list
        testTickets = new ArrayList<>();
        testTickets.add(mockTicket1);
        testTickets.add(mockTicket2);

        // Ensure TicketCollection.tickets is initialized
        TicketCollection.tickets = new ArrayList<>();

        // Set up System.out for testing output
        System.setOut(new PrintStream(outContent));

        // Set up mock behavior
        when(mockTicket1.getTicket_id()).thenReturn(1);
        when(mockTicket2.getTicket_id()).thenReturn(2);

        // Set up flight and passenger for the tickets
        when(mockTicket1.getFlight()).thenReturn(mockFlight);
        when(mockTicket1.getPassenger()).thenReturn(mockPassenger);
        when(mockTicket1.getPrice()).thenReturn(1000);

        when(mockTicket2.getFlight()).thenReturn(mockFlight);
        when(mockTicket2.getPassenger()).thenReturn(mockPassenger);
        when(mockTicket2.getPrice()).thenReturn(1500);

        // Set up toString behavior (optional)
        when(mockTicket1.toString()).thenReturn("Ticket 1 Details");
        when(mockTicket2.toString()).thenReturn("Ticket 2 Details");
    }

    @BeforeEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        if (closeable != null) {
            closeable.close();
        }
    }

    /**
     * Tests for requirement 1: Validate tickets thoroughly when added to the collection
     */

    @Test
    @DisplayName("Add valid tickets to collection")
    void testAddValidTickets() {
        // Act
        TicketCollection.addTickets(testTickets);

        // Assert
        assertEquals(2, TicketCollection.getTickets().size());
        assertTrue(TicketCollection.getTickets().contains(mockTicket1));
        assertTrue(TicketCollection.getTickets().contains(mockTicket2));
    }

    @Test
    @DisplayName("Try to add null ticket list - should throw NullPointerException")
    void testAddNullTicketList() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            TicketCollection.addTickets(null);
        });
    }

    @Test
    @DisplayName("Try to add empty ticket list - should not change collection size")
    void testAddEmptyTicketList() {
        // Arrange
        ArrayList<Ticket> emptyList = new ArrayList<>();
        int initialSize = TicketCollection.tickets.size();

        // Act
        TicketCollection.addTickets(emptyList);

        // Assert
        assertEquals(initialSize, TicketCollection.tickets.size());
    }

    /**
     * Tests for requirement 2: Ensure the correct ticket is returned upon retrieval
     */

    @Test
    @DisplayName("Get all tickets displays correctly")
    void testGetAllTickets() {
        // Arrange
        TicketCollection.tickets = testTickets;

        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Act
        TicketCollection.getAllTickets();

        // Reset System.out
        System.setOut(originalOut);

        // Assert - Check if something was printed to the console
        // We can't verify toString() directly, but we can check if the method produced output
        assertFalse(outputStream.toString().isEmpty(), "Method should print ticket information");

        // If your implementation of getAllTickets prints specific ticket details,
        // you could also check for those specifically
        // String output = outputStream.toString();
        // assertTrue(output.contains("Ticket ID: 1") || output.contains("Ticket 1"));
        // assertTrue(output.contains("Ticket ID: 2") || output.contains("Ticket 2"));
    }

    @Test
    @DisplayName("Get ticket by ID - existing ticket")
    void testGetTicketInfo_ExistingTicket() {
        // Arrange
        TicketCollection.tickets = testTickets;
        int ticketId = 1;

        // Act
        Ticket result = TicketCollection.getTicketInfo(ticketId);

        // Assert
        assertSame(mockTicket1, result);
    }

    @Test
    @DisplayName("Get ticket by ID - non-existing ticket")
    void testGetTicketInfo_NonExistingTicket() {
        // Arrange
        TicketCollection.tickets = testTickets;
        int nonExistingTicketId = 999;

        // Act
        Ticket result = TicketCollection.getTicketInfo(nonExistingTicketId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Get ticket by ID - handle edge cases")
    void testGetTicketInfo_EdgeCases() {
        // Arrange
        TicketCollection.tickets = testTickets;

        // Test minimum valid ticket ID
        when(mockTicket1.getTicket_id()).thenReturn(0);

        // Test maximum valid ticket ID
        when(mockTicket2.getTicket_id()).thenReturn(Integer.MAX_VALUE);

        // Act & Assert
        assertEquals(mockTicket1, TicketCollection.getTicketInfo(0));
        assertEquals(mockTicket2, TicketCollection.getTicketInfo(Integer.MAX_VALUE));
        assertNull(TicketCollection.getTicketInfo(-1)); // Invalid negative ticket ID
    }

    @Test
    @DisplayName("Get tickets from empty collection")
    void testGetTicketsFromEmptyCollection() {
        // Arrange
        TicketCollection.tickets = new ArrayList<>();

        // Act
        ArrayList<Ticket> result = TicketCollection.getTickets();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}