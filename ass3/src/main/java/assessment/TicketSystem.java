package assessment;

import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

public class TicketSystem {
    Passenger passenger;
    Ticket ticket;
    Flight flight;

    // Scanner as an instance variable for easier testing
    private Scanner scanner;

    public TicketSystem() {
        this.passenger = new Passenger();
        this.ticket = new Ticket();
        this.flight = new Flight();
        this.scanner = new Scanner(System.in);
    }

    // For testing purposes
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showTicket() {
        try {
            if (ticket == null || ticket.flight == null) {
                System.out.println("No flight details available");
                return;
            }
            System.out.println("You have bought a ticket for flight " +
                    ticket.flight.getDepartFrom() + " - " +
                    ticket.flight.getDepartTo() + "\n\nDetails:");
            System.out.println(this.ticket.toString());
        } catch (NullPointerException e) {
            System.out.println("No flight details available : " + e.getMessage());
        }
    }

    // Buy ticket with direct Flight
    public void buyTicket(int ticket_id) throws Exception {
        // Validate ticket exists
        Ticket validTicket = TicketCollection.getTicketInfo(ticket_id);

        if (validTicket == null) {
            System.out.println("This ticket does not exist.");
            return;
        }

        // Check if ticket is already booked
        if (validTicket.ticketStatus()) {
            throw new IllegalStateException("This ticket is already booked.");
        }

        // Validate flight exists
        if (validTicket.getFlight() == null) {
            System.out.println("This ticket has no associated flight.");
            return;
        }

        Flight flight = validTicket.getFlight();
        int flight_id = flight.getFlightID();

        // Get flight from collection to ensure it's valid
        Flight flightFromCollection = FlightCollection.getFlightInfo(flight_id);
        if (flightFromCollection == null) {
            System.out.println("Flight information not found.");
            return;
        }

        // Validate airplane exists
        if (flightFromCollection.getAirplane() == null) {
            System.out.println("Airplane information not found.");
            return;
        }

        Airplane airplane = flightFromCollection.getAirplane();

        // Check seat availability before collecting passenger info
        if (validTicket.getClassVip() && airplane.getBusinessSitsNumber() <= 0) {
            throw new IllegalStateException("No business class seats available.");
        } else if (!validTicket.getClassVip() && airplane.getEconomySitsNumber() <= 0) {
            throw new IllegalStateException("No economy class seats available.");
        }

        System.out.println("You have selected ticket: " + validTicket);

        try {
            // Collect passenger information
            System.out.println("Enter your First Name: ");
            String firstName = scanner.nextLine();
            passenger.setFirstName(firstName);

            System.out.println("Enter your Second name:");
            String secondName = scanner.nextLine();
            passenger.setSecondName(secondName);

            System.out.println("Enter your age:");
            Integer age = Integer.parseInt(scanner.nextLine());
            passenger.setAge(age);

            System.out.println("Enter your gender (Male/Woman/Non-Binary/Other): ");
            String gender = scanner.nextLine();
            passenger.setGender(gender);

            System.out.println("Enter your e-mail address:");
            String email = scanner.nextLine();
            passenger.setEmail(email);

            System.out.println("Enter your phone number:");
            String phoneNumber = scanner.nextLine();
            passenger.setPhoneNumber(phoneNumber);

            System.out.println("Enter your passport number:");
            String passportNumber = scanner.nextLine();
            passenger.setPassport(passportNumber);

            System.out.println("Do you want to purchase?\n 1-YES 0-NO");
            int purchase = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (purchase == 0) {
                System.out.println("Purchase cancelled.");
                return;
            }

            // Process the ticket purchase
            this.flight = flightFromCollection;
            this.ticket = validTicket;

            ticket.setPassenger(passenger);
            ticket.setTicket_id(ticket_id);
            ticket.setFlight(this.flight);
            ticket.setPrice(ticket.getPrice()); // This applies age discount and service tax
            ticket.setClassVip(validTicket.getClassVip());
            ticket.setTicketStatus(true);

            // Update seat availability
            if (ticket.getClassVip()) {
                airplane.setBusinessSitsNumber(airplane.getBusinessSitsNumber() - 1);
            } else {
                airplane.setEconomySitsNumber(airplane.getEconomySitsNumber() - 1);
            }

            // Display correct price information
            System.out.println("Your bill: $" + ticket.getPrice() + "\n");

            System.out.println("Enter your card number:");
            String cardNumber = scanner.nextLine();
            passenger.setCardNumber(cardNumber);

            System.out.println("Enter your security code:");
            Integer securityCode = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            passenger.setSecurityCode(securityCode);

            System.out.println("Payment successful! Ticket booked.");

        } catch (IllegalArgumentException e) {
            // Re-throw validation errors from passenger/ticket setters
            throw e;
        }
    }

    // Buy ticket with transfer flight (two tickets)
    public void buyTicket(int ticket_id_first, int ticket_id_second) throws Exception {
        System.out.println("Processing transfer booking: " + ticket_id_first + " -> " + ticket_id_second);

        // Validate both tickets exist
        Ticket validTicketFirst = TicketCollection.getTicketInfo(ticket_id_first);
        Ticket validTicketSecond = TicketCollection.getTicketInfo(ticket_id_second);

        if (validTicketFirst == null || validTicketSecond == null) {
            System.out.println("One or both tickets do not exist.");
            return;
        }

        // Check if tickets are already booked
        if (validTicketFirst.ticketStatus() || validTicketSecond.ticketStatus()) {
            throw new IllegalStateException("One or both tickets are already booked.");
        }

        // Validate flights exist
        if (validTicketFirst.getFlight() == null || validTicketSecond.getFlight() == null) {
            System.out.println("One or both tickets have no associated flight.");
            return;
        }

        // Get flights and airplanes
        Flight flight_first = FlightCollection.getFlightInfo(validTicketFirst.getFlight().getFlightID());
        Flight flight_second = FlightCollection.getFlightInfo(validTicketSecond.getFlight().getFlightID());

        if (flight_first == null || flight_second == null) {
            System.out.println("Flight information not found for one or both flights.");
            return;
        }

        if (flight_first.getAirplane() == null || flight_second.getAirplane() == null) {
            System.out.println("Airplane information not found for one or both flights.");
            return;
        }

        Airplane airplane_first = flight_first.getAirplane();
        Airplane airplane_second = flight_second.getAirplane();

        // Check seat availability for both flights
        if (validTicketFirst.getClassVip() && airplane_first.getBusinessSitsNumber() <= 0) {
            throw new IllegalStateException("No business class seats available on first flight.");
        } else if (!validTicketFirst.getClassVip() && airplane_first.getEconomySitsNumber() <= 0) {
            throw new IllegalStateException("No economy class seats available on first flight.");
        }

        if (validTicketSecond.getClassVip() && airplane_second.getBusinessSitsNumber() <= 0) {
            throw new IllegalStateException("No business class seats available on second flight.");
        } else if (!validTicketSecond.getClassVip() && airplane_second.getEconomySitsNumber() <= 0) {
            throw new IllegalStateException("No economy class seats available on second flight.");
        }

        try {
            // Collect passenger information (same for both tickets)
            System.out.println("Enter your First Name: ");
            String firstName = scanner.nextLine();
            passenger.setFirstName(firstName);

            System.out.println("Enter your Second name:");
            String secondName = scanner.nextLine();
            passenger.setSecondName(secondName);

            System.out.println("Enter your age:");
            Integer age = Integer.parseInt(scanner.nextLine());
            passenger.setAge(age);

            System.out.println("Enter your gender (Male/Woman/Non-Binary/Other): ");
            String gender = scanner.nextLine();
            passenger.setGender(gender);

            System.out.println("Enter your e-mail address:");
            String email = scanner.nextLine();
            passenger.setEmail(email);

            System.out.println("Enter your phone number:");
            String phoneNumber = scanner.nextLine();
            passenger.setPhoneNumber(phoneNumber);

            System.out.println("Enter your passport number:");
            String passportNumber = scanner.nextLine();
            passenger.setPassport(passportNumber);

            System.out.println("Do you want to purchase both tickets?\n 1-YES 0-NO");
            int purchase = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (purchase == 0) {
                System.out.println("Purchase cancelled.");
                return;
            }

            // Process first ticket
            Ticket ticket_first = validTicketFirst;
            ticket_first.setPassenger(passenger);
            ticket_first.setTicket_id(ticket_id_first);
            ticket_first.setFlight(flight_first);
            ticket_first.setPrice(ticket_first.getPrice());
            ticket_first.setClassVip(ticket_first.getClassVip());
            ticket_first.setTicketStatus(true);

            if (ticket_first.getClassVip()) {
                airplane_first.setBusinessSitsNumber(airplane_first.getBusinessSitsNumber() - 1);
            } else {
                airplane_first.setEconomySitsNumber(airplane_first.getEconomySitsNumber() - 1);
            }

            // Process second ticket
            Ticket ticket_second = validTicketSecond;
            ticket_second.setPassenger(passenger);
            ticket_second.setTicket_id(ticket_id_second);
            ticket_second.setFlight(flight_second);
            ticket_second.setPrice(ticket_second.getPrice());
            ticket_second.setClassVip(ticket_second.getClassVip());
            ticket_second.setTicketStatus(true);

            if (ticket_second.getClassVip()) {
                airplane_second.setBusinessSitsNumber(airplane_second.getBusinessSitsNumber() - 1);
            } else {
                airplane_second.setEconomySitsNumber(airplane_second.getEconomySitsNumber() - 1);
            }

            // Calculate and display total price
            int totalPrice = ticket_first.getPrice() + ticket_second.getPrice();
            this.ticket.setPrice(totalPrice);

            System.out.println("Your total bill: $" + totalPrice + "\n");

            System.out.println("Enter your card number:");
            String cardNumber = scanner.nextLine();
            passenger.setCardNumber(cardNumber);

            System.out.println("Enter your security code:");
            Integer securityCode = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            passenger.setSecurityCode(securityCode);

            System.out.println("Payment successful! Both tickets booked.");

        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    // Method for choosing a ticket based on cities
    public void chooseTicket(String city1, String city2) throws Exception {
        // Validate city names
        if (city1 == null || city1.trim().isEmpty() || city2 == null || city2.trim().isEmpty()) {
            throw new IllegalArgumentException("City names cannot be empty");
        }

        // Validate city names contain only letters and spaces
        if (!city1.matches("[a-zA-Z ]+") || !city2.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException("City names must only contain letters and spaces");
        }

        // Try to find direct flight from city1 to city2
        Flight directFlight = FlightCollection.getFlightInfo(city1, city2);

        if (directFlight != null) {
            // Direct flight available
            System.out.println("Direct flight found from " + city1 + " to " + city2);
            System.out.println("Available tickets for this flight:");

            // Show only tickets for this specific flight
            boolean foundTickets = false;
            for (Ticket t : TicketCollection.getTickets()) {
                if (t.getFlight() != null &&
                        t.getFlight().getFlightID() == directFlight.getFlightID() &&
                        !t.ticketStatus()) {
                    System.out.println(t);
                    foundTickets = true;
                }
            }

            if (!foundTickets) {
                throw new IllegalStateException("No available tickets for this flight");
            }

            System.out.println("\nEnter ID of ticket you want to choose:");
            int ticket_id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Verify the selected ticket is for this flight
            Ticket selectedTicket = TicketCollection.getTicketInfo(ticket_id);
            if (selectedTicket == null) {
                throw new IllegalArgumentException("Invalid ticket ID");
            }

            if (selectedTicket.getFlight() == null ||
                    selectedTicket.getFlight().getFlightID() != directFlight.getFlightID()) {
                throw new IllegalArgumentException("Selected ticket is not for this flight");
            }

            buyTicket(ticket_id);
        } else {
            // No direct flight, look for transfer options
            System.out.println("No direct flight from " + city1 + " to " + city2);
            System.out.println("Searching for transfer options...");

            // Strategy: Find any flight that goes TO city2 (our destination)
            // Then check if we can get from city1 to that flight's departure city

            boolean foundTransferRoute = false;
            int firstTicketId = -1;
            int secondTicketId = -1;
            String transferCity = null;

            // Look through all flights to find ones going to our destination
            for (Flight destFlight : FlightCollection.getFlights()) {
                if (destFlight.getDepartTo().equals(city2)) {
                    // This flight goes to our destination
                    // Now check if we can get from city1 to this flight's departure city
                    String potentialTransferCity = destFlight.getDepartFrom();

                    // Look for a flight from city1 to the transfer city
                    Flight firstLeg = FlightCollection.getFlightInfo(city1, potentialTransferCity);

                    if (firstLeg != null) {
                        // We found a valid transfer route!
                        System.out.println("Transfer route found: " + city1 + " -> " +
                                potentialTransferCity + " -> " + city2);

                        // Find available tickets for both flights
                        for (Ticket t : TicketCollection.getTickets()) {
                            if (t.getFlight() != null && !t.ticketStatus()) {
                                if (t.getFlight().getFlightID() == firstLeg.getFlightID() && firstTicketId == -1) {
                                    firstTicketId = t.getTicket_id();
                                } else if (t.getFlight().getFlightID() == destFlight.getFlightID() && secondTicketId == -1) {
                                    secondTicketId = t.getTicket_id();
                                }
                            }
                        }

                        if (firstTicketId != -1 && secondTicketId != -1) {
                            foundTransferRoute = true;
                            transferCity = potentialTransferCity;
                            break; // Found a complete route with available tickets
                        } else {
                            // Reset if we didn't find tickets for this route
                            firstTicketId = -1;
                            secondTicketId = -1;
                        }
                    }
                }
            }

            if (foundTransferRoute) {
                System.out.println("Available transfer route via " + transferCity);
                System.out.println("First flight ticket ID: " + firstTicketId);
                System.out.println("Second flight ticket ID: " + secondTicketId);

                System.out.println("\nDo you want to book this transfer route? 1-YES 0-NO");
                int confirm = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (confirm == 1) {
                    buyTicket(firstTicketId, secondTicketId);
                } else {
                    System.out.println("Booking cancelled.");
                }
            } else {
                System.out.println("No transfer routes available from " + city1 + " to " + city2);
                throw new IllegalStateException("No possible route found");
            }
        }
    }
}