package assessment;

import java.util.ArrayList;

public class TicketCollection {

	public static ArrayList<Ticket> tickets = new ArrayList<>();

	public static ArrayList<Ticket> getTickets()
	{
		return tickets;
	}

	public static void addTickets(ArrayList<Ticket> tickets_db)
	{
		if (tickets_db == null) {
			throw new NullPointerException("Cannot add null ticket list");
		}
		TicketCollection.tickets.addAll(tickets_db);
	}

	public static void getAllTickets()
	{
		//display all available tickets from the Ticket collection
		for (Ticket ticket : tickets)
		{
			System.out.println(ticket);
		}
	}
	public static Ticket getTicketInfo(int ticket_id) {
		//SELECT a ticket where ticket id = ticket_id
		for (Ticket ticket : tickets)
		{
			if(ticket.getTicket_id() == ticket_id)
			{
				return ticket;
			}
		}
		return null;
	}
}