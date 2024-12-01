package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.repository.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SimulatorManager {
    private static DBManager db = new DBManager();
    private static boolean runningSimulator = false;
    private static int vendor_customer_count = 100;
    private static int vendorDelayTime;
    private static int customerDelayTime;

    private static HashMap<Integer, Integer> customerBookings = new HashMap<>();
    private static HashMap<Integer, Integer> vendorTickets = new HashMap<>();


    public static void runSimulation(){

        db.setup();
        vendorDelayTime = vendor_customer_count/TicketPool.getTicketReleaseRate();
        customerDelayTime = vendor_customer_count/TicketPool.getCustomerRetrievalRate();
        List<Thread> customers = new ArrayList<>();
        List<Thread> vendors = new ArrayList<>();
        startRunningSimulator();
        LogManager.clearLogs();
        TicketPool.resetTicketPool();
        initialiseCustomerBookings();
        initialiseVendorTickets();

        for(int i=1;i<=vendor_customer_count;i++) { // Defining vendor and customer Threads.
            int id = i;
            customers.add(new Thread(new Customer(id, customerDelayTime)));
            vendors.add(new Thread(new Vendor(id, vendorDelayTime)));
        }

        for(int i=1;i<=vendor_customer_count;i++){ // Starting vendor and customer Threads.
            vendors.get(i-1).start();
            customers.get(i-1).start();
        }
        LogManager.log("Vendors Started");

        while (TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        stopSimulation();

        for(int i=1;i<=vendor_customer_count;i++){
            try {
                vendors.get(i-1).join();
                customers.get(i-1).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        LogManager.log("Added Tickets Count: "+TicketPool.getTicketsList().size());
        int bookedCount = 0;
        for(Ticket ticket : TicketPool.getTicketsList()){
            if (!ticket.isAvailable()){
                bookedCount++;
            }
        }
        LogManager.log("Booked tickets count: "+bookedCount);
    }

    public static void stopSimulation(){
        runningSimulator =false;
    }

    public static void startRunningSimulator(){
        runningSimulator = true;
    }

    public static boolean isRunningSimulator(){
        return runningSimulator;
    }

    public static void initialiseCustomerBookings(){
        customerBookings = new HashMap<>();
        for(int i=1; i<=vendor_customer_count; i++){
            customerBookings.put(i, 0);
        }
    }

    public static void initialiseVendorTickets(){
        vendorTickets = new HashMap<>();
        for(int i=1; i<=vendor_customer_count; i++){
            vendorTickets.put(i, 0);
        }

    }

    public static void recordCustomerBooking(int id){
        int currentBookingCount = customerBookings.get(id);
        customerBookings.put(id, currentBookingCount+1);
    }

    public static void recordVendorTicket(int id){
        int vendorTicketCount = vendorTickets.get(id);
        vendorTickets.put(id, vendorTicketCount+1);
    }

    public static HashMap<Integer, Integer> getCustomerBookings() {
        return customerBookings;
    }

    public static HashMap<Integer, Integer> getVendorTickets() {
        return vendorTickets;
    }
}
