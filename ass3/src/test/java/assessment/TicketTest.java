package assessment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketTest {

    private Ticket ticket;
    private AutoCloseable closeable;

    @Mock
    private Flight mockFlight;

    @Mock
    private Passenger mockPassenger;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Set up common mock behavior
        when(mockPassenger.getAge()).thenReturn(30); // Default adult age

        // Create a ticket with mocked dependencies
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);
    }

    /**
     * Test for Requirement 1: Ticket status must clearly indicate 'True' (booked) or 'False' (available)
     */

    @Test
    @DisplayName("Ticket status should be initially set to 'False' (available)")
    void testInitialTicketStatus() {
        // Assert that a newly created ticket is marked as available (not booked)
        assertFalse(ticket.ticketStatus(), "New ticket should be marked as available (False)");
    }

    @Test
    @DisplayName("Ticket status can be changed to 'True' (booked)")
    void testSetTicketStatus() {
        // Act
        ticket.setTicketStatus(true);

        // Assert
        assertTrue(ticket.ticketStatus(), "Ticket status should be set to booked (True)");
    }

    @Test
    @DisplayName("Ticket status can be changed back to 'False' (available)")
    void testResetTicketStatus() {
        // Arrange
        ticket.setTicketStatus(true);

        // Act
        ticket.setTicketStatus(false);

        // Assert
        assertFalse(ticket.ticketStatus(), "Ticket status should be reset to available (False)");
    }

    /**
     * Tests for Requirement 2: Discounts must apply correctly based on passenger age categories
     */

    @Test
    @DisplayName("Child discount (50%) should apply for ages under 15")
    void testChildDiscount() {
        // Arrange
        when(mockPassenger.getAge()).thenReturn(10); // Child age
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.saleByAge(10);

        // Assert
        assertEquals(500, ticket.getPrice(), "Child ticket should receive 50% discount");
    }

    @Test
    @DisplayName("No discount should apply for adults (ages 15-59)")
    void testAdultNoDiscount() {
        // Arrange
        when(mockPassenger.getAge()).thenReturn(30); // Adult age
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.saleByAge(30);

        // Assert
        assertEquals(1000, ticket.getPrice(), "Adult ticket should not receive a discount");
    }

    @Test
    @DisplayName("Senior discount (100%) should apply for ages 60 and over")
    void testSeniorDiscount() {
        // Arrange
        when(mockPassenger.getAge()).thenReturn(65); // Senior age
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.saleByAge(65);

        // Assert
        assertEquals(0, ticket.getPrice(), "Senior ticket should receive 100% discount");
    }

    @Test
    @DisplayName("Discount should be applied at boundary values")
    void testAgeDiscountBoundaries() {
        // Test age 14 (child)
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);
        ticket.saleByAge(14);
        assertEquals(500, ticket.getPrice(), "Age 14 should receive child discount");

        // Test age 15 (adult)
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);
        ticket.saleByAge(15);
        assertEquals(1000, ticket.getPrice(), "Age 15 should not receive a discount");

        // Test age 59 (adult)
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);
        ticket.saleByAge(59);
        assertEquals(1000, ticket.getPrice(), "Age 59 should not receive a discount");

        // Test age 60 (senior)
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);
        ticket.saleByAge(60);
        assertEquals(0, ticket.getPrice(), "Age 60 should receive senior discount");
    }

    /**
     * Tests for Requirement 3: Price and service tax must be valid non-negative numbers
     */

    @Test
    @DisplayName("Price should be non-negative")
    void testNonNegativePrice() {
        // Arrange
        int invalidPrice = -100;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticket.setPrice(invalidPrice);
        }, "Should throw IllegalArgumentException for negative price");

        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Price should be properly set for valid values")
    void testValidPrice() {
        // Act
        ticket.setPrice(500);

        // Assert - price will be modified by saleByAge and serviceTax
        // Adult with no discount but with 12% service tax: 500 * 1.12 = 560
        assertEquals(560, ticket.getPrice());
    }

    /**
     * Tests for Requirement 4: Service tax should always apply upon ticket sale
     */

    @Test
    @DisplayName("Service tax (12%) should be applied automatically")
    void testServiceTax() {
        // Arrange
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.serviceTax();

        // Assert
        assertEquals(1120, ticket.getPrice(), "12% service tax should be applied");
    }

    @Test
    @DisplayName("Service tax should be applied after age-based discount")
    void testDiscountThenTax() {
        // Arrange
        when(mockPassenger.getAge()).thenReturn(10); // Child age
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.setPrice(1000); // This will trigger both saleByAge and serviceTax

        // Assert
        // 1000 → 500 (50% child discount) → 560 (with 12% tax)
        assertEquals(560, ticket.getPrice(), "Service tax should be applied after discount");
    }

    @Test
    @DisplayName("Service tax should apply even with zero price (seniors)")
    void testTaxWithZeroPrice() {
        // Arrange
        when(mockPassenger.getAge()).thenReturn(65); // Senior age
        ticket = new Ticket(1, 1000, mockFlight, false, mockPassenger);

        // Act
        ticket.setPrice(1000); // This will trigger both saleByAge and serviceTax

        // Assert
        // 1000 → 0 (100% senior discount) → 0 (with tax, still 0)
        assertEquals(0, ticket.getPrice(), "Zero price should remain zero after tax");
    }

    /**
     * Tests for Requirement 5: Test that the Ticket class receives valid information of flight and passenger
     */

    @Test
    @DisplayName("Ticket should reject null Flight")
    void testNullFlight() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticket.setFlight(null);
        }, "Should throw IllegalArgumentException for null Flight");

        assertEquals("Flight cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Ticket should reject null Passenger")
    void testNullPassenger() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticket.setPassenger(null);
        }, "Should throw IllegalArgumentException for null Passenger");

        assertEquals("Passenger cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Ticket constructor should initialize with valid flight and passenger")
    void testValidConstructor() {
        // Act
        Ticket validTicket = new Ticket(1, 1000, mockFlight, true, mockPassenger);

        // Assert
        assertSame(mockFlight, validTicket.getFlight(), "Flight should be properly set");
        assertSame(mockPassenger, validTicket.getPassenger(), "Passenger should be properly set");
        assertEquals(1, validTicket.getTicket_id(), "Ticket ID should be properly set");
        assertEquals(1000, validTicket.getPrice(), "Price should be properly set");
        assertTrue(validTicket.getClassVip(), "VIP status should be properly set");
        assertFalse(validTicket.ticketStatus(), "Ticket status should be initially available");
    }

    @Test
    @DisplayName("Ticket constructor should throw exception with null flight")
    void testConstructorWithNullFlight() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Ticket(1, 1000, null, false, mockPassenger);
        }, "Constructor should throw IllegalArgumentException for null Flight");

        assertEquals("Flight cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Ticket constructor should throw exception with null passenger")
    void testConstructorWithNullPassenger() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Ticket(1, 1000, mockFlight, false, null);
        }, "Constructor should throw IllegalArgumentException for null Passenger");

        assertEquals("Passenger cannot be null", exception.getMessage());
    }

    /**
     * Additional tests for toString and other methods
     */

    @Test
    @DisplayName("ToString should include all ticket information")
    void testToString() {
        // Arrange
        when(mockFlight.toString()).thenReturn("Mock Flight");
        when(mockPassenger.toString()).thenReturn("Mock Passenger");

        // Act
        String result = ticket.toString();

        // Assert
        assertTrue(result.contains("Price=" + ticket.getPrice()), "ToString should include price");
        assertTrue(result.contains("Mock Flight"), "ToString should include flight info");
        assertTrue(result.contains("Vip status=" + ticket.getClassVip()), "ToString should include VIP status");
        assertTrue(result.contains("Mock Passenger"), "ToString should include passenger info");
        assertTrue(result.contains("Ticket was purchased=" + ticket.ticketStatus()), "ToString should include ticket status");
    }
}