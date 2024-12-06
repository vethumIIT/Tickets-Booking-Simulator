package com.app.ticket_booking_simulator.services;

import com.app.ticket_booking_simulator.models.Ticket;
import com.app.ticket_booking_simulator.repository.DBManager;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {

    private static final ReentrantLock lock = new ReentrantLock();
    // defines the reentrant lock for creating and removing tickets

    private static int totalTickets; // total number of tickets that can be added and purchased
    private static int ticketReleaseRate; // the rate at which vendors add tickets to the ticket pool.
    private static int customerRetrievalRate;// the rate at which a customer purchases tickets from the ticket pool
    private static int maxTicketCapacity;// the maximum number of tickets that can exist in the ticket pool at a given time.

    private static int ticketBookedCount = 0;// the total number of tickets that have been purchased by the customer
    private static final ReentrantLock ticketBookedCountLock = new ReentrantLock();
    // lock for the booked ticket count

    private static int ticketCount = 0;// total number of tickets added to the ticket pool by vendors
    private static final ReentrantLock ticketCountLock = new ReentrantLock();
    // lock for the ticket count

    private static List<Ticket> ticketsList = new ArrayList<>();// list where the added tickets are stored.
    private static DBManager db = new DBManager();
    // database manager object which contains the database related functions

    private static int ticketsListSize = 0; // number of tickets that are currently in the ticket pool
    private static final ReentrantLock ticketsListSizeLock = new ReentrantLock();
    // lock for the ticketPoolSize

    /**
     *
     * @param ticket
     */
    public static synchronized void createTicket(Ticket ticket){
        if(!SimulatorManager.isRunningSimulator()){// if the simulation has ended return
            return;
        } else if(getTicketsListSize()>=getMaxTicketCapacity()){
            // do not add ticket if max ticket capacity is reached.
            LogManager.log("Maximum Ticket Capacity Reached Please wait Vendor "+ticket.getVendorId());
            return;
        } else {

            lock.lock();// using reentrant lock before adding tickets
            try {
                if (getTicketCount() >= getTotalTickets()) {
                    LogManager.log("Total Ticket Count Reached! no more tickets can be added.");
                    return;
                }
                ticket.setTicketId(getTicketCount()+1);

                ticketsList.add(ticket);// add ticket to ticket list
                changeTicketsListSize(1);


                List<Object> parameters = new ArrayList<>();// setting the parameters for the database instruction.
                parameters.add(ticket.getTicketId());
                parameters.add(ticket.getVendorId());

                db.writeDatabase("INSERT INTO tickets (id, customer_id, vendor_id, isAvailable) VALUES (?, 0, ?, 1)", parameters);
                // create the record for the ticket and set it as not booked.

                SimulatorManager.recordVendorTicket(ticket.getVendorId());
                // update the vendorTickets list in SimulatorManager

                setTicketCount(getTicketCount() + 1); // increment ticketCount
                LogManager.log("Vendor " + ticket.getVendorId() + " added ticket no " + getTicketCount());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();// unlock the lock
            }
        }

    }

    public static synchronized void bookTicket(int customerId){
        if(!SimulatorManager.isRunningSimulator()){
            // do not book ticket if the simulation has stopped.
            return;
        }
        lock.lock();
        try{

            if (getTicketBookedCount()>=totalTickets){ // cannot book ticket if all the tickets are booked.
                LogManager.log("Sorry customer "+customerId+" reached total tickets tickets at "+(getTicketBookedCount()+1)+" tickets");
                return;
            }else if (getTicketCount()==0){// cannot book ticket if no tickets have been added yet.
                LogManager.log("No Tickets Available yet for customer "+customerId);
                return;
            } else if (ticketsList.isEmpty()) {// cannot buy ticket if ticketPool is empty
                LogManager.log("No tickets Available to book for customer "+customerId);
                return;
            } else{
                Ticket booking_ticket = ticketsList.get(0);

                ticketsList.remove(0);// removing ticket from ticket pool
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

    public static String getStatus(){ // creates a json string to represent the stats

        Map<String, Integer> map = new HashMap<>();

        map.put("ticketBookedCount", getTicketBookedCount());
        map.put("ticketCount", getTicketCount());
        map.put("ticketListSize", getTicketsListSize());

        map.put("totalTickets", getTotalTickets());
        map.put("ticketReleaseRate", getTicketReleaseRate());
        map.put("customerRetrievalRate", getCustomerRetrievalRate());
        map.put("maxTicketCapacity", getMaxTicketCapacity());

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
