package com.app.ticket_booking_simulator.services;

import com.app.ticket_booking_simulator.models.Ticket;
import com.app.ticket_booking_simulator.repository.DBManager;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {

    private static final ReentrantLock lock = new ReentrantLock();

    private static int totalTickets; // total number of tickets that can be added and purchased
    private static int ticketReleaseRate; // the rate at which tickets are added to the ticket pool
    private static int customerRetrievalRate;// the rate at which tickets are purchased from the ticket pool
    private static int maxTicketCapacity;// the maximum number of tickets that can exist in the ticket pool at a given time.

    private static int ticketBookedCount = 0;
    private static final ReentrantLock ticketBookedCountLock = new ReentrantLock();

    private static int ticketCount = 0;
    private static final ReentrantLock ticketCountLock = new ReentrantLock();

    private static List<Ticket> ticketsList = new ArrayList<>();
    private static DBManager db = new DBManager();

    private static int ticketsListSize = 0;
    private static final ReentrantLock ticketsListSizeLock = new ReentrantLock();

    public static synchronized void createTicket(Ticket ticket){
        boolean maxTicketsFlag = false;
        if(!SimulatorManager.isRunningSimulator()){
            return;
        } else if(getTicketsListSize()>=getMaxTicketCapacity()
                //&& getTicketCount()!=getTotalTickets()-1 // added to ensure that the last ticket doesn't get left in case MaxTicketCapacity is 1
        ){
            LogManager.log("Maximum Ticket Capacity Reached Please wait Vendor "+ticket.getVendorId());
            maxTicketsFlag=true;
            return;
        } else {

            lock.lock();
            try {
                if (maxTicketsFlag){
                    LogManager.log("illegal running after maxTicketCapacity was reached!");
                }

                if (getTicketCount() >= getTotalTickets()) {
                    LogManager.log("Total Ticket Count Reached! no more tickets can be added.");
                    return;
                }
                ticket.setTicketId(getTicketCount()+1);

                ticketsList.add(ticket);// add ticket to ticket list
                changeTicketsListSize(1);


                List<Object> parameters = new ArrayList<>();
                parameters.add(ticket.getTicketId());
                parameters.add(ticket.getVendorId());

                db.writeDatabase("INSERT INTO tickets (id, customer_id, vendor_id, isAvailable) VALUES (?, 0, ?, 1)", parameters);

                SimulatorManager.recordVendorTicket(ticket.getVendorId());

                setTicketCount(getTicketCount() + 1);
                LogManager.log("Vendor " + ticket.getVendorId() + " added ticket no " + getTicketCount());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

    }

    public static synchronized void bookTicket(int customerId){
        if(!SimulatorManager.isRunningSimulator()){
            return;
        }
        lock.lock();
        try{
            /*try {
                Thread.sleep(1000*0/getCustomerRetrievalRate());
            } catch (InterruptedException e) {
                return;
            }*/

            if (getTicketBookedCount()>=totalTickets){
                LogManager.log("Sorry customer "+customerId+" reached total tickets tickets at "+(getTicketBookedCount()+1)+" tickets");
                return;
            }else if (getTicketCount()==0){
                LogManager.log("No Tickets Available yet for customer "+customerId);
                return;
            } else if (ticketsList.isEmpty()) {
                LogManager.log("No tickets Available to book for customer "+customerId);
                return;
            } else{
                Ticket booking_ticket = ticketsList.get(0);

                ticketsList.remove(0);
                changeTicketsListSize(-1);

                List<Object> parameters = new ArrayList<>();

                parameters.add(customerId);
                parameters.add(getTicketBookedCount()+1);

                LogManager.log("Customer "+customerId+" booked ticket no "+(getTicketBookedCount()+1));

                db.writeDatabase("UPDATE tickets SET isAvailable=false, customer_id=? WHERE id=?", parameters);

                SimulatorManager.recordCustomerBooking(customerId);

                //ticketBookedCount++;
                setTicketBookedCount(getTicketBookedCount()+1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }

    }

    public static int getTicketsListSize() {
        lock.lock();
        try {
            return ticketsListSize;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }
    }

    public static void setTicketsListSize(int ticketsListSize) {
        lock.lock();
        try {
            TicketPool.ticketsListSize = ticketsListSize;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }
    }

    public static void changeTicketsListSize(int changeValue) {
        TicketPool.ticketsListSize += changeValue;
    }

    public static String getStatus(){

        Map<String, Integer> map = new HashMap<>();

        map.put("ticketBookedCount", getTicketBookedCount());
        map.put("ticketCount", getTicketCount());
        map.put("ticketListSize", getTicketsListSize());

        Gson gson = new Gson();


        return gson.toJson(map);
    }

    public static void resetTicketPool(){
        setTicketBookedCount(0);
        setTicketCount(0);
        setTicketsListSize(0);
        clearTicketsList();
    }


    public static List<Ticket> getTicketsList() {
        lock.lock();
        try {
            return ticketsList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static List<Ticket> getAsyncTicketsList() {
        return ticketsList;
    }

    public static void clearTicketsList() {
        lock.lock();
        try {
            ticketsList = new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
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
        ticketBookedCountLock.lock();
        try {
            return ticketBookedCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            ticketBookedCountLock.unlock();
        }
    }

    public static void setTicketBookedCount(int ticketBookedCount) {
        ticketBookedCountLock.lock();
        try {
            TicketPool.ticketBookedCount = ticketBookedCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            ticketBookedCountLock.unlock();
        }
    }

    public static int getTicketCount() {
        ticketCountLock.lock();
        try {
            return ticketCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            ticketCountLock.unlock();
        }
    }

    public static void setTicketCount(int ticketCount) {
        ticketCountLock.lock();
        try {
            TicketPool.ticketCount = ticketCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            ticketCountLock.unlock();
        }
    }
}
