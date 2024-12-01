package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.repository.DBManager;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {

    private static final ReentrantLock lock = new ReentrantLock();

    private static int totalTickets;
    private static int ticketReleaseRate;
    private static int customerRetrievalRate;
    private static int maxTicketCapacity;

    private static int ticketBookedCount = 1;
    private static int ticketCount = 0;

    private static List<Ticket> ticketsList = new ArrayList<>();
    private static DBManager db = new DBManager();

    public static void createTicket(Ticket ticket){

        lock.lock();
        try {
            ticketCount++;
            if(ticketCount>getTotalTickets()){
                LogManager.log("Total Ticket Count Reached! no more tickets can be added.");
                return;
            }
            ticket.setTicketId(ticketCount);

            ticketsList.add(ticket);// add ticket to ticket list

            List<Object> parameters = new ArrayList<>();
            parameters.add(ticket.getTicketId());
            parameters.add(ticket.getVendorId());

            db.writeDatabase("INSERT INTO tickets (id, customer_id, vendor_id, isAvailable) VALUES (?, 0, ?, 1)", parameters);

            SimulatorManager.recordVendorTicket(ticket.getVendorId());

            LogManager.log("Vendor "+ticket.getVendorId()+" added a ticket no "+ticketCount);
        }finally {
            lock.unlock();
        }

    }

    public static void bookTicket(int customerId){
        lock.lock();
        try{
            if (ticketBookedCount>totalTickets){
                LogManager.log("Sorry customer "+customerId+" out of tickets at "+ticketBookedCount+" tickets");
                return;
            }else if (ticketCount==0){
                LogManager.log("No Tickets Available yet for customer "+customerId);
                return;
            } else if (ticketBookedCount>ticketsList.size()) {
                LogManager.log("No tickets Available to book for customer "+customerId);
                return;
            } else{
                //System.out.println("Tickets Available");
                Ticket booking_ticket = ticketsList.get(ticketBookedCount-1);
                booking_ticket.setCustomerId(customerId);
                booking_ticket.setAvailable(false);
                ticketsList.set(ticketBookedCount-1, booking_ticket);

                List<Object> parameters = new ArrayList<>();

                parameters.add(customerId);
                parameters.add(ticketBookedCount);

                LogManager.log("Customer "+customerId+" booked a ticket no "+ticketBookedCount);

                db.writeDatabase("UPDATE tickets SET isAvailable=false, customer_id=? WHERE id=?", parameters);

                SimulatorManager.recordCustomerBooking(customerId);

                ticketBookedCount++;
            }
        }finally {
            lock.unlock();
        }

    }

    public String getStatus(){

        Map<String, Integer> map = new HashMap<>();

        map.put("totalTickets", getTotalTickets());
        map.put("ticketReleaseRate", getTicketReleaseRate());
        map.put("customerRetrievalRate", getCustomerRetrievalRate());
        map.put("maxTicketCapacity", getMaxTicketCapacity());

        map.put("ticketBookedCount", getTicketBookedCount());
        map.put("ticketCount", getTicketCount());

        Gson gson = new Gson();


        return gson.toJson(map);
    }

    public static void resetTicketPool(){
        setTicketBookedCount(1);
        setTicketCount(0);
        clearTicketsList();
    }


    public static List<Ticket> getTicketsList() {
        return ticketsList;
    }

    public static void clearTicketsList() {
        ticketsList = new ArrayList<>();
    }

    public static void setTotalTickets(int totalTickets) {
        TicketPool.totalTickets = totalTickets;
    }

    public static int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public static void setTicketReleaseRate(int ticketReleaseRate) {
        TicketPool.ticketReleaseRate = ticketReleaseRate;
    }

    public static int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public static void setCustomerRetrievalRate(int customerRetrievalRate) {
        TicketPool.customerRetrievalRate = customerRetrievalRate;
    }

    public static int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public static void setMaxTicketCapacity(int maxTicketCapacity) {
        TicketPool.maxTicketCapacity = maxTicketCapacity;
    }

    public static int getTotalTickets() {
        return totalTickets;
    }

    public static int getTicketBookedCount() {
        return ticketBookedCount;
    }

    public static void setTicketBookedCount(int ticketBookedCount) {
        TicketPool.ticketBookedCount = ticketBookedCount;
    }

    public static int getTicketCount() {
        return ticketCount;
    }

    public static void setTicketCount(int ticketCount) {
        TicketPool.ticketCount = ticketCount;
    }
}
