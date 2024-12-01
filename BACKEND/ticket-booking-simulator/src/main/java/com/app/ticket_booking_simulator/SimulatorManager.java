package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.repository.DBManager;

import java.util.ArrayList;
import java.util.List;


public class SimulatorManager {
    private static DBManager db = new DBManager();
    private static boolean runningSimulator = false;
    private static boolean runningManager = false;
    private static int vendor_customer_count = 100;
    private static int vendorDelayTime;
    private static int customerDelayTime;


    public static void runSimulation(){

        db.setup();
        vendorDelayTime = vendor_customer_count/TicketPool.getTicketReleaseRate();
        customerDelayTime = vendor_customer_count/TicketPool.getCustomerRetrievalRate();
        List<Thread> customers = new ArrayList<>();
        List<Thread> vendors = new ArrayList<>();
        startRunningSimulator();
        LogManager.clearLogs();
        TicketPool.resetTicketPool();

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

    public static void runSimulationManager(){
        startRunningManager();
        while (isRunningManager()){
            if(isRunningSimulator()){
                runSimulation();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

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

    public static boolean isRunningManager() {
        return runningManager;
    }

    public static void startRunningManager() {
        SimulatorManager.runningManager = true;
    }

    public static void stopRunningManager() {
        SimulatorManager.runningManager = false;
    }
}
