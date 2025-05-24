package assessment;

import java.util.LinkedHashMap;
import java.util.*;
import java.util.stream.IntStream;

public class Airplane {
    private int airplaneID;
    private String airplaneModel;
    private int businessSitsNumber;
    private int economySitsNumber;
    private int crewSitsNumber;
    private Map<Character, List<String>> seatMap;


    public Airplane(int airplaneID, String airplaneModel, int businessSitsNumber, int economySitsNumber, int crewSitsNumber)
    {
        if (airplaneID <= 0) {
            throw new IllegalArgumentException("Airplane ID must be a positive integer.");
        }
        // Validate airplaneModel
        if (airplaneModel == null || airplaneModel.trim().isEmpty()) {
            throw new IllegalArgumentException("Airplane model is required.");
        }
        // Validate seat counts are non-negative
        if (businessSitsNumber < 0 || economySitsNumber < 0 || crewSitsNumber < 0) {
            throw new IllegalArgumentException("Seat counts cannot be negative.");
        }
        // Validate total passenger seats (business + economy)
        int totalPassengerSeats = businessSitsNumber + economySitsNumber;
        if (totalPassengerSeats < 7 || totalPassengerSeats > 70 || totalPassengerSeats % 7 != 0) {
            throw new IllegalArgumentException("Total passenger seats must be 7-70 and divisible by 7.");
        }
        buildSeatMap();

        this.airplaneID = airplaneID;
        this.airplaneModel = airplaneModel;
        this.businessSitsNumber = businessSitsNumber;
        this.economySitsNumber = economySitsNumber;
        this.crewSitsNumber = crewSitsNumber;
    }

    private void buildSeatMap() {
        seatMap = new LinkedHashMap<>();
        for (char row = 'A'; row <= 'J'; row++) {
            seatMap.put(row, IntStream.rangeClosed(1, 7)
                    .mapToObj(String::valueOf).toList());
        }
    }

    public Map<Character, List<String>> getSeatMap() {
        return Collections.unmodifiableMap(seatMap);
    }

    public int getAirplaneID()
    {
        return airplaneID;
    }

    public void setAirplaneID(int airplaneID)
    {
        this.airplaneID = airplaneID;
    }

    public String getAirplaneModel()
    {
        return airplaneModel;
    }

    public void setAirplaneModel(String airplaneModel)
    {
        this.airplaneModel = airplaneModel;
    }

    public int getBusinessSitsNumber()
    {
        return businessSitsNumber;
    }

    public void setBusinessSitsNumber(int businessSitsNumber)
    {
        if (businessSitsNumber < 0) {
            throw new IllegalArgumentException("Business seats cannot be negative.");
        }
        int total = businessSitsNumber + this.economySitsNumber;
        if (total < 7 || total > 70 || total % 7 != 0) {
            throw new IllegalArgumentException("Total passenger seats must be 7-70 and divisible by 7.");
        }
        this.businessSitsNumber = businessSitsNumber;
    }

    public int getEconomySitsNumber()
    {
        return economySitsNumber;
    }

    public void setEconomySitsNumber(int economSitsNumber)
    {
        if (economySitsNumber < 0) {
            throw new IllegalArgumentException("Economy seats cannot be negative.");
        }
        int total = this.businessSitsNumber + economySitsNumber;
        if (total < 7 || total > 70 || total % 7 != 0) {
            throw new IllegalArgumentException("Total passenger seats must be 7-70 and divisible by 7.");
        }
        this.economySitsNumber = economSitsNumber;
    }

    public int getCrewSitsNumber()
    {
        return crewSitsNumber;
    }

    public void setCrewSitsNumber(int crewSitsNumber)
    {
        if (crewSitsNumber < 0) {throw new IllegalArgumentException("Seat counts cannot be negative.");}
        this.crewSitsNumber = crewSitsNumber;
    }

    public String toString()
    {
        return "Airplane{" +
                "model=" + getAirplaneModel() + '\'' +
                ", business sits=" + getBusinessSitsNumber() + '\'' +
                ", economy sits=" + getEconomySitsNumber() + '\'' +
                ", crew sits=" + getCrewSitsNumber() + '\'' +
                '}';
    }

    public static Airplane getAirPlaneInfo(int airplane_id)
    {
        // TODO Auto-generated method stub
        return null;
    }
}