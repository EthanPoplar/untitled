package assessment;

import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

public class TicketSystem
{
    Passenger passenger;
    Ticket ticket;
    Flight flight;

    // Scanner as an instance variable for easier testing
    private Scanner scanner;

    public TicketSystem()
    {
        this.passenger = new Passenger();
        this.ticket = new Ticket();
        this.flight = new Flight();
        this.scanner = new Scanner(System.in);
    }

    // For testing purposes
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showTicket()
    {
        try
        {
            System.out.println("You have bought a ticket for flight " + ticket.flight.getDepartFrom() + " - " + ticket.flight.getDepartTo() + "\n\nDetails:");
            System.out.println(this.ticket.toString());
        }
        catch (NullPointerException e)
        {
            System.out.println("No flight details available : " + e.getMessage());
        }
    }

    // Buy ticket with direct Flight
    public void buyTicket(int ticket_id) throws Exception
    {
        int flight_id = 0;

        //select ticket where ticket_id="+ticket_id"
        Ticket validTicket = TicketCollection.getTicketInfo(ticket_id);

        //if there is a valid ticket id was input then we buy it, otherwise show message
        if(validTicket == null)
        {
            System.out.println("This ticket does not exist.");
            return;
        }
        else{
            System.out.println("You have bought a ticket for flight " + validTicket);
            // Check if ticket is already booked
            if (validTicket.ticketStatus()) {
                throw new IllegalStateException("This ticket is already booked.");
            }

            //select flight_id from ticket where ticket_id=" + ticket_id
            // Add null check for flight to avoid NullPointerException
            if (validTicket.getFlight() == null) {
                System.out.println("This ticket has no associated flight.");
                return;
            }

            flight_id = validTicket.getFlight().getFlightID();

            try
            {
                System.out.println("Enter your First Name: ");
                String firstName = scanner.nextLine();

                passenger.setFirstName(firstName);

                System.out.println("Enter your Second name:");
                String secondName = scanner.nextLine();
                passenger.setSecondName(secondName); //setting passengers info

                System.out.println("Enter your age:");
                Integer age = Integer.parseInt(scanner.nextLine());
                passenger.setAge(age);

                System.out.println("Enter your gender: ");
                String gender = scanner.nextLine();
                passenger.setGender(gender);

                System.out.println("Enter your e-mail address");
                String email = scanner.nextLine();
                passenger.setEmail(email);

                System.out.println("Enter your phone number (+7):");
                String phoneNumber = scanner.nextLine();
                passenger.setPhoneNumber(phoneNumber);

                System.out.println("Enter your passport number:");
                String passportNumber = scanner.nextLine();
                passenger.setPassport(passportNumber);

                System.out.println("Do you want to purchase?\n 1-YES 0-NO");

                int purch = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (purch == 0)
                {
                    return;
                }
                else
                {
                    flight = FlightCollection.getFlightInfo(flight_id);

                    // Add null checks for flight and airplane
                    if (flight == null) {
                        System.out.println("Flight information not found.");
                        return;
                    }

                    if (flight.getAirplane() == null) {
                        System.out.println("Airplane information not found.");
                        return;
                    }

                    Airplane airplane = flight.getAirplane();

                    // Add null check for airplane
                    if (airplane == null) {
                        System.out.println("Airplane details not found.");
                        return;
                    }

                    ticket = TicketCollection.getTicketInfo(ticket_id);

                    ticket.setPassenger(passenger);
                    ticket.setTicket_id(ticket_id);
                    ticket.setFlight(flight);
                    ticket.setPrice(ticket.getPrice());
                    ticket.setClassVip(ticket.getClassVip());
                    ticket.setTicketStatus(true);
                    if (ticket.getClassVip() == true)
                    {
                        airplane.setBusinessSitsNumber(airplane.getBusinessSitsNumber() - 1);
                    } else
                    {
                        airplane.setEconomySitsNumber(airplane.getEconomySitsNumber() - 1);
                    }

                }
                System.out.println("Your bill: " + ticket.getPrice() + "\n");

                System.out.println("Enter your card number:");
                String cardNumber = scanner.next();
                passenger.setCardNumber(cardNumber);

                System.out.println("Enter your security code:");
                Integer securityCode = scanner.nextInt();
                passenger.setSecurityCode(securityCode);

            } catch (PatternSyntaxException patternException)
            {
                patternException.printStackTrace();
            }
        }
    }

    public void buyTicket(int ticket_id_first, int ticket_id_second) throws Exception
    {
        //method for buying two tickets with transfer flight

        int flight_id_first = 0;
        int flight_id_second = 0;

        System.out.println(ticket_id_first + " " + ticket_id_second);

        Ticket validTicketfirst = TicketCollection.getTicketInfo(ticket_id_first);
        Ticket validTicketSecond = TicketCollection.getTicketInfo(ticket_id_second);

        System.out.println("Processing...");

        //if there is a valid ticket id was input then we buy it, otherwise show message
        if(validTicketfirst == null || validTicketSecond == null)
        {
            System.out.println("This ticket does not exist.");
            return;
        }
        else
        {
            // Check if tickets are already booked
            if (validTicketfirst.ticketStatus() || validTicketSecond.ticketStatus()) {
                System.out.println("One of the tickets is already booked.");
                return;
            }

            // Add null check for flights
            if (validTicketfirst.getFlight() == null || validTicketSecond.getFlight() == null) {
                System.out.println("One of the tickets has no associated flight.");
                return;
            }

            flight_id_first = validTicketfirst.getFlight().getFlightID();
            flight_id_second = validTicketSecond.getFlight().getFlightID();

            try
            {
                System.out.println("Enter your First Name: ");
                String firstName = scanner.nextLine();
                passenger.setFirstName(firstName);

                System.out.println("Enter your Second name:");
                String secondName = scanner.nextLine();
                passenger.setSecondName(secondName); //setting passengers info

                System.out.println("Enter your age:");
                Integer age = Integer.parseInt(scanner.nextLine());
                passenger.setAge(age);

                System.out.println("Enter your gender: ");
                String gender = scanner.nextLine();
                passenger.setGender(gender);

                System.out.println("Enter your e-mail address");
                String email = scanner.nextLine();
                passenger.setEmail(email);

                System.out.println("Enter your phone number (+7):");
                String phoneNumber = scanner.nextLine();
                passenger.setPhoneNumber(phoneNumber);

                System.out.println("Enter your passport number:");
                String passportNumber = scanner.nextLine();
                passenger.setPassport(passportNumber);

                System.out.println("Do you want to purchase?\n 1-YES 0-NO");
                int purch = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (purch == 0)
                {
                    return;
                }
                else
                {
                    //  "select * from flight, airplane where flight_id=" + flight_id_first + " and flight.airplane_id=airplane.airplane_id");
                    Flight flight_first = FlightCollection.getFlightInfo(flight_id_first);

                    // Add null checks
                    if (flight_first == null || flight_first.getAirplane() == null) {
                        System.out.println("Flight or airplane information not found for first flight.");
                        return;
                    }

                    int airplane_id_first = flight_first.getAirplane().getAirplaneID();

                    Airplane airplane_first = Airplane.getAirPlaneInfo(airplane_id_first);

                    if (airplane_first == null) {
                        System.out.println("Airplane details not found for first flight.");
                        return;
                    }

                    Flight flight_second = FlightCollection.getFlightInfo(flight_id_second);

                    // Add null checks
                    if (flight_second == null || flight_second.getAirplane() == null) {
                        System.out.println("Flight or airplane information not found for second flight.");
                        return;
                    }

                    int airplane_id_second = flight_second.getAirplane().getAirplaneID();

                    Airplane airpairplane_second = Airplane.getAirPlaneInfo(airplane_id_second);

                    if (airpairplane_second == null) {
                        System.out.println("Airplane details not found for second flight.");
                        return;
                    }

                    Ticket ticket_first = TicketCollection.getTicketInfo(ticket_id_first);

                    Ticket ticket_second = TicketCollection.getTicketInfo(ticket_id_second);

                    ticket_first.setPassenger(passenger);
                    ticket_first.setTicket_id(ticket_id_first);
                    ticket_first.setFlight(flight_first);
                    ticket_first.setPrice(ticket_first.getPrice());
                    ticket_first.setClassVip(ticket_first.getClassVip());
                    ticket_first.setTicketStatus(true);

                    if (ticket_first.getClassVip() == true)
                    {
                        airplane_first.setBusinessSitsNumber(airplane_first.getBusinessSitsNumber() - 1);
                    } else
                    {
                        airplane_first.setEconomySitsNumber(airplane_first.getEconomySitsNumber() - 1);
                    }

                    System.out.println("--*-*-");

                    ticket_second.setPassenger(passenger);
                    ticket_second.setTicket_id(ticket_id_second);
                    ticket_second.setFlight(flight_second);
                    ticket_second.setPrice(ticket_second.getPrice());
                    ticket_second.setClassVip(ticket_second.getClassVip());
                    ticket_second.setTicketStatus(true);
                    if (ticket_second.getClassVip() == true)
                    {
                        airpairplane_second.setBusinessSitsNumber(airpairplane_second.getBusinessSitsNumber() - 1);
                    } else
                    {
                        airpairplane_second.setEconomySitsNumber(airpairplane_second.getEconomySitsNumber() - 1);
                    }

                    System.out.println("--*-*-");

                    ticket.setPrice(ticket_first.getPrice() + ticket_second.getPrice());

                    System.out.println("Your bill: " + ticket.getPrice() + "\n");

                    System.out.println("Enter your card number:");

                    String cardNumber = scanner.next();
                    passenger.setCardNumber(cardNumber);

                    System.out.println("Enter your security code:");
                    Integer securityCode = scanner.nextInt();
                    passenger.setSecurityCode(securityCode);
                }
            } catch (PatternSyntaxException patternException)
            {
                patternException.printStackTrace();
            }
        }
    }

    // Method for choosing a ticket based on cities
    public void chooseTicket(String city1, String city2) throws Exception {
        // Validate city names
        if (city1 == null || city1.isEmpty() || city2 == null || city2.isEmpty()) {
            throw new IllegalArgumentException("City names cannot be empty");
        }

        if (!city1.matches("[a-zA-Z ]+") || !city2.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException("City names must only contain letters and spaces");
        }

        int counter = 1;
        int idFirst = 0;
        int idSecond = 0;
        Flight flight = FlightCollection.getFlightInfo(city1, city2);

        if (flight != null) {
            TicketCollection.getAllTickets();
            System.out.println("\nEnter ID of ticket you want to choose:");
            int ticket_id = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            buyTicket(ticket_id);
        } else {
            // When there is no direct flight
            Flight depart_to = FlightCollection.getFlightInfo(city2);
            if (depart_to == null) {
                System.out.println("No flights found to destination: " + city2);
                return;
            }

            String connectCity = depart_to.getDepartFrom();
            Flight flightConnectingTwoCities = FlightCollection.getFlightInfo(city1, connectCity);
            if (flightConnectingTwoCities != null) {
                System.out.println("There is a special way to go there. And it is a transfer flight, like above. Way №" + counter);
                idFirst = depart_to.getFlightID();
                idSecond = flightConnectingTwoCities.getFlightID();
            } else {
                System.out.println("No connecting flights found.");
                return;
            }
            counter++;
            buyTicket(idFirst, idSecond);
            if (counter == 1) {
                System.out.println("There are no possible variants.");
            }
        }
    }
}