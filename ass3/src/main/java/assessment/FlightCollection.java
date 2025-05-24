package assessment;

import java.util.ArrayList;

public class FlightCollection {

	public static ArrayList<Flight> flights = new ArrayList<>();

	public static ArrayList<Flight> getFlights()
	{
		return flights;
	}

	public static void addFlights(ArrayList<Flight> flights)
	{
		if (flights == null) {
			throw new NullPointerException("Cannot add null flight list");
		}
		FlightCollection.flights.addAll(flights);
	}

	public static Flight getFlightInfo(String city1, String city2)
	{
		//display the flights where there is a direct flight from city 1 to city2
		Flight flight = null;
		for (Flight f : flights)
		{
			if (f.getDepartFrom().equals(city2) && f.getDepartTo().equals(city1))
			{
				System.out.println(f.toString());
				flight = f;
			}
		};
		return flight;
	}

	public static Flight getFlightInfo(String city)
	{
		//SELECT a flight where depart_to = city
		Flight flight = null;
		for (Flight f : flights)
		{
			if (f.getDepartTo().equals(city))
			{
				flight = f;
			}
		}
		return flight;
	}

	public static Flight getFlightInfo(int flight_id)
	{
		//SELECT a flight with a particular flight id
		Flight flight =  null;

		for (Flight f : flights)
		{
			if (f.getFlightID() == flight_id)
			{
				flight = f;
			}
		}
		return flight;
	}
}