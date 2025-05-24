package assessment;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class IntegrationTest {

    private TicketSystem system;

    @BeforeEach
    void setUp() {
        // clear static lists
        FlightCollection.flights.clear();
        TicketCollection.tickets.clear();

        // construct a fresh system and override its Scanner
        system = new TicketSystem();
        system.setScanner(new Scanner("1\n4111111111111111\n123\n"));
    }

    /** R-1: YES flight, YES ticket, NOT booked → booking succeeds */
    @Test @DisplayName("R-1: booking succeeds for valid, unbooked ticket")
    void r1_bookingSucceeds() throws Exception {
        // arrange flight
        Airplane ap = new Airplane(1, "B737", 7, 14, 2);
        Timestamp d1 = Timestamp.valueOf("2025-01-01 10:00:00");
        Timestamp d2 = Timestamp.valueOf("2025-01-01 12:00:00");
        Flight f = new Flight(10, "Sydney", "Melbourne",
                "FL10","AirCo", d1, d2, ap);
        FlightCollection.addFlights(new ArrayList<>(List.of(f)));  // :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}

        // arrange passenger & ticket
        Passenger p = new Passenger(
                "John","Doe",30,"Male",
                "john@example.com","+61 412345678",
                "P1234567","4111111111111111",123
        );                                                       // :contentReference[oaicite:2]{index=2}&#8203;:contentReference[oaicite:3]{index=3}
        Ticket t = new Ticket(100, 200, f, false, p);             // :contentReference[oaicite:4]{index=4}&#8203;:contentReference[oaicite:5]{index=5}
        TicketCollection.addTickets(new ArrayList<>(List.of(t)));

        Scanner scanner = new Scanner("Alex\nBrown\n29\nWoman\nalice@x.com\n+61 434567890\nUS1234567\n1\n878923\n123");
        system.setScanner(scanner);
        // act
        system.buyTicket(100);

        // assert
        assertTrue(t.ticketStatus(), "Ticket should be marked as booked");
    }

    /** R-2: YES flight, YES ticket, ALREADY booked → IllegalStateException */
    @Test @DisplayName("R-2: double-booking throws IllegalStateException")
    void r2_doubleBook() throws Exception {
        Airplane ap = new Airplane(2, "A320", 7, 14, 2);
        Timestamp d1 = Timestamp.valueOf("2025-02-01 10:00:00");
        Timestamp d2 = Timestamp.valueOf("2025-02-01 12:00:00");
        Flight f = new Flight(20, "Melbourne", "Perth",
                "FL20","AirCo", d1, d2, ap);
        FlightCollection.addFlights(new ArrayList<>(List.of(f)));

        Passenger p = new Passenger(
                "Jane","Smith",28,"Female",
                "jane@doe.com","+61 423456789",
                "NZ1234567","4111111111111111",456
        );
        Ticket t = new Ticket(200,150,f,true,p);
        t.setTicketStatus(true);  // pre-book
        TicketCollection.addTickets(new ArrayList<>(List.of(t)));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> system.buyTicket(200)
        );
        assertEquals("This ticket is already booked.", ex.getMessage());
    }

    /** R-3: YES flight, NO ticket → prints “This ticket does not exist.” */
    @Test @DisplayName("R-3: missing ticket prints error")
    void r3_ticketNotFound() throws Exception {
        Airplane ap = new Airplane(3, "A330", 7, 14, 2);
        Timestamp d1 = Timestamp.valueOf("2025-03-01 10:00:00");
        Timestamp d2 = Timestamp.valueOf("2025-03-01 12:00:00");
        Flight f = new Flight(30, "Perth", "Sydney",
                "FL30","AirCo", d1, d2, ap);
        FlightCollection.addFlights(new ArrayList<>(List.of(f)));

        // no tickets added
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        system.buyTicket(999);

        assertTrue(out.toString().contains("This ticket does not exist."));
    }

    /** R-4: NO flight → prints “Flight information not found.” */
    @Test @DisplayName("R-4: ticket’s flight missing prints error")
    void r4_flightNotFound() throws Exception {
        Airplane ap = new Airplane(4, "B777", 7, 14, 2);
        Timestamp d1 = Timestamp.valueOf("2025-04-01 10:00:00");
        Timestamp d2 = Timestamp.valueOf("2025-04-01 12:00:00");
        Flight f = new Flight(40, "Adelaide", "Darwin",
                "FL40","AirCo", d1, d2, ap);

        // (flight not registered)
        Passenger p = new Passenger(
                "Alex","Brown",29,"Female",
                "alice@x.com","+61 434567890",
                "US1234567","4111111111111111",789
        );
        Ticket t = new Ticket(300,120,f,false, p);
        TicketCollection.addTickets(new ArrayList<>(List.of(t)));

        Scanner scanner = new Scanner("Alex\nBrown\n29\nWoman\nalice@x.com\n+61 434567890\nUS1234567\n1\n");
        system.setScanner(scanner);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        system.buyTicket(300);

        assertTrue(out.toString().contains("Flight information not found."));
    }
}
