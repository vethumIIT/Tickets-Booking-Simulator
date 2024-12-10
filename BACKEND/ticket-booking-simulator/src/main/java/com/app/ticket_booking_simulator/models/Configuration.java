package com.app.ticket_booking_simulator.models;

import com.app.ticket_booking_simulator.services.TicketPool;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration class that stores the configuration values.
 */
public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;


    /**
     *
     * @param totalTickets
     * @param ticketReleaseRate
     * @param customerRetrievalRate
     * @param maxTicketCapacity
     */
    public Configuration(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    /**
     * checks if all the values are >=1
     * @return true if the values are valid.
     */
    public boolean isValid(){
        if(this.totalTickets<1 || this.ticketReleaseRate<1 || this.customerRetrievalRate<1 || this.maxTicketCapacity<1){
            return false;
        }
        else {
            return true;
        }
    }

    /**
     *
     * @return A json string with the class attributes
     */
    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    /**
     * writes the configurations to config.txt
     */
    public void writeConfigToFile(){
        try(FileWriter writer = new FileWriter("DataFiles/configs.txt")){
            writer.write(this.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return totalTickets
     */
    public int getTotalTickets() {
        return totalTickets;
    }

    /**
     *
     * @return ticketReleaseRate
     */
    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    /**
     *
     * @return customerRetrievalRate
     */
    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    /**
     *
     * @return maxTicketCapacity
     */
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}
