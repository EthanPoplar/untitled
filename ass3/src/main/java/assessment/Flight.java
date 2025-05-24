package assessment;

import java.sql.Timestamp;

public class Flight {
    private int flightID;
    private String departTo;
    private String departFrom;
    private String code;
    private String company;
    private Timestamp dateFrom;
    private Timestamp dateTo;
    Airplane airplane;
    
    public Flight()
    {

    }

    public Flight(int flight_id, String departTo, String departFrom, String code, String company, Timestamp dateFrom,Timestamp dateTo, Airplane airplane)
    {
        if (flight_id == 0 || departFrom == null || departTo == null || code == null || company == null || airplane == null || dateFrom == null || dateTo == null)
        {
            throw new IllegalArgumentException("All fields are required");
        }

        // Date format checker
        String fmtFrom = dateFrom.toString().split(" ")[0].trim();
        String fmtTo   = dateTo.toString().split(" ")[0].trim();

        if (!fmtFrom.matches("\\d{4}-\\d{2}-\\d{2}") || !fmtTo  .matches("\\d{4}-\\d{2}-\\d{2}"))
        {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
        }

        // Time format checker
        String timeFrom = dateFrom.toString().split(" ")[1].split("\\.")[0].trim();
        String timeTo   = dateTo.toString().split(" ")[1].split("\\.")[0].trim();

        if (!timeFrom.matches("\\d{2}:\\d{2}:\\d{2}")
                || !timeTo  .matches("\\d{2}:\\d{2}:\\d{2}"))
        {
            throw new IllegalArgumentException("Time must be in HH:MM:SS format");
        }

        //check if flight exists in the system
        if (FlightCollection.getFlights() != null)
        {
            for(Flight flight : FlightCollection.getFlights()) {
                if (flight_id == flight.getFlightID()) {
                    throw new IllegalArgumentException("Flight Already in the system");
                }
            }
        }


        this.flightID=flight_id;
        this.departTo = departTo;
        this.departFrom = departFrom;
        this.code = code;
        this.company = company;
        this.airplane = airplane;
        this.dateTo = dateTo;
        this.dateFrom = dateFrom;
    }

    public int getFlightID()
    {
        return flightID;
    }

    public void setFlightID(int flightid)
    {
        this.flightID = flightid;
    }

    public String getDepartTo()
    {
        return departTo;
    }

    public void setDepartTo(String departTo)
    {
        this.departTo = departTo;
    }

    public String getDepartFrom()
    {
        return departFrom;
    }

    public void setDepartFrom(String departFrom)
    {
        this.departFrom = departFrom;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public Timestamp getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(Timestamp dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public Timestamp getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(Timestamp dateTo)
    {
        this.dateTo = dateTo;
    }

    public void setAirplane(Airplane airplane)
    {
        this.airplane = airplane;
    }

    public Airplane getAirplane()
    {
        return airplane;
    }

    public String toString()
    {
            return "Flight{" + airplane.toString() +
                    ", date to=" +  getDateTo() + ", " + '\'' +
                    ", date from='" + getDateFrom() + '\'' +
                    ", depart from='" + getDepartFrom() + '\'' +
                    ", depart to='" + getDepartTo() + '\'' +
                    ", code=" + getCode() + '\'' +
                    ", company=" + getCompany() + '\'' +
                    ", code=" + getCode() + '\'' +
                    '}';
    }
}
