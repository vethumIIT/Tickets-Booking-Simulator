package com.app.ticket_booking_simulator.models;

import com.app.ticket_booking_simulator.services.TicketPool;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;


    public Configuration(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public void configure(){
        TicketPool.setTotalTickets(this.totalTickets);
        TicketPool.setTicketReleaseRate(this.ticketReleaseRate);
        TicketPool.setCustomerRetrievalRate(this.customerRetrievalRate);
        TicketPool.setMaxTicketCapacity(this.maxTicketCapacity);

        writeConfigToFile();
    }

    public boolean isValid(){
        if(this.totalTickets<1 || ticketReleaseRate<1 || customerRetrievalRate<1 || maxTicketCapacity<1){
            return false;
        }
        else {
            return true;
        }
    }

    public String toJson(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public void writeConfigToFile(){
        try(FileWriter writer = new FileWriter("DataFiles/configs.txt")){
            writer.write(this.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
