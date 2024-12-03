package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.repository.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class SimulatorManager {
    private static DBManager db = new DBManager();

    private static final int vendor_customer_count = 50;
    private static int vendorDelayTime;
    private static int customerDelayTime;

    private static boolean runSimulationEnd = true;
    private static final ReentrantLock runSimulationEndLock = new ReentrantLock();


    private static final ReentrantLock customerBookingLock = new ReentrantLock();
    private static final ReentrantLock vendorTicketLock = new ReentrantLock();

    private static HashMap<Integer, Integer> customerBookings = new HashMap<>();
    private static HashMap<Integer, Integer> vendorTickets = new HashMap<>();

    private static final ReentrantLock runningSimulationLock = new ReentrantLock();
    private static boolean runningSimulator = false;


    public static void runSimulation(){


        setRunSimulationEnd(false);
        vendorDelayTime = 1000 / TicketPool.getTicketReleaseRate();
        customerDelayTime = 1000 / TicketPool.getCustomerRetrievalRate();
        List<Thread> customers = new ArrayList<>();
        List<Thread> vendors = new ArrayList<>();
        startRunningSimulator();

        initialise();

        for(int i=1;i<=vendor_customer_count;i++) { // Defining vendor and customer Threads.
            int id = i;
            customers.add(new Thread(new Customer(id, customerDelayTime)));
            vendors.add(new Thread(new Vendor(id, vendorDelayTime)));
        }

        for(int i=1;i<=vendor_customer_count;i++){ // Starting vendor and customer Threads.
            vendors.get(i-1).start();
            //LogManager.log("started vendor "+i);
            customers.get(i-1).start();
            //LogManager.log("started customer "+i);
        }
        LogManager.log("Vendors Started");

        while(TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets()
                && isRunningSimulator()
                //&& TicketPool.getTicketsList().size()<=TicketPool.getMaxTicketCapacity()
        ){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets()){
            LogManager.log("Simulation was stopped");
        }


        System.out.println("Joining Threads");
        for (int i = 1; i <= vendor_customer_count; i++) {
            try {
                vendors.get(i - 1).interrupt();
                customers.get(i - 1).interrupt();
                vendors.get(i - 1).join();
                customers.get(i - 1).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        stopSimulation();

        LogManager.log("Tickets in ticket pool: " + (TicketPool.getTicketsListSize()) );
        LogManager.log("Added Tickets Count: " + (TicketPool.getTicketCount()));

        LogManager.log("Booked tickets count: " + (TicketPool.getTicketBookedCount()));
        setRunSimulationEnd(true);
    }

    public static void initialise(){
        db.setup();
        LogManager.clearLogs();
        TicketPool.resetTicketPool();
        initialiseCustomerBookings();
        initialiseVendorTickets();
    }

    public static void stopSimulation(){
        runningSimulationLock.lock();
        try {
            runningSimulator = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            runningSimulationLock.unlock();
        }
    }

    public static void startRunningSimulator(){
        runningSimulationLock.lock();
        try {
            runningSimulator = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            runningSimulationLock.unlock();
        }
    }

    public static boolean isRunningSimulator(){
        runningSimulationLock.lock();
        try {
            return runningSimulator;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            runningSimulationLock.unlock();
        }
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
        customerBookingLock.lock();
        try {
            int currentBookingCount = customerBookings.get(id);
            customerBookings.put(id, currentBookingCount + 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            customerBookingLock.unlock();
        }

    }

    public static void recordVendorTicket(int id){
        vendorTicketLock.lock();
        try {
            int vendorTicketCount = vendorTickets.get(id);
            vendorTickets.put(id, vendorTicketCount + 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            vendorTicketLock.unlock();
        }
    }

    public static HashMap<Integer, Integer> getCustomerBookings() {
        return customerBookings;
    }

    public static HashMap<Integer, Integer> getVendorTickets() {
        return vendorTickets;
    }

    public static boolean isRunSimulationEnd() {
        runSimulationEndLock.lock();
        try {
            return runSimulationEnd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            runSimulationEndLock.unlock();
        }
    }

    public static void setRunSimulationEnd(boolean runSimulationEnd) {
        runSimulationEndLock.lock();
        try {
            SimulatorManager.runSimulationEnd = runSimulationEnd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            runSimulationEndLock.unlock();
        }
    }
}
