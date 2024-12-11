package com.app.ticket_booking_simulator.services;

import com.app.ticket_booking_simulator.models.Configuration;
import com.app.ticket_booking_simulator.models.Customer;
import com.app.ticket_booking_simulator.models.Vendor;
import com.app.ticket_booking_simulator.repository.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the running of the simulation including Creating the vendor and customer threads etc.
 */
public class SimulatorManager {
    private static DBManager db = new DBManager();

    private static final int vendor_customer_count = 50;

    private static boolean runSimulationEnd = true;
    private static final ReentrantLock runSimulationEndLock = new ReentrantLock();


    private static final ReentrantLock customerBookingLock = new ReentrantLock();
    private static final ReentrantLock vendorTicketLock = new ReentrantLock();

    private static HashMap<Integer, Integer> customerBookings = new HashMap<>();
    // keeps track of how many tickets each customer has bought
    private static HashMap<Integer, Integer> vendorTickets = new HashMap<>();
    // keeps track of how many tickets each vendor has added

    private static final ReentrantLock runningSimulationLock = new ReentrantLock();
    private static boolean runningSimulator = false;
    // while the simulation is running (multiple vendor and customer
    // threads are adding and removing tickets to and from the ticket pool) this will be set to true.
    // if it is set to false, that means the vendors and customers need to stop running and they are
    // not allowed to make changes to the ticket pool.


    /**
     * Starts running the simulation.
     * @param config config object with values
     */
    public static void runSimulation(Configuration config){


        setRunSimulationEnd(false);// indicating that the runSimulation method has finished running.

        TicketPool.setTotalTickets(config.getTotalTickets());
        TicketPool.setTicketReleaseRate(config.getTicketReleaseRate());
        TicketPool.setCustomerRetrievalRate(config.getCustomerRetrievalRate());
        TicketPool.setMaxTicketCapacity(config.getMaxTicketCapacity());

        config.writeConfigToFile();// writes the configuration parameters to the configuration file

        int vendorDelayTime = 1000 / TicketPool.getTicketReleaseRate();
        int customerDelayTime = 1000 / TicketPool.getCustomerRetrievalRate();
        // calculates the delay time for vendor and customer

        List<Thread> customers = new ArrayList<>();
        List<Thread> vendors = new ArrayList<>();
        startRunningSimulator();
        // sets runningSimulator to true which allows the vendor threads
        // to run and make changes to the ticket pool

        initialise();

        LogManager.log("Starting...");
        for(int i=1;i<=vendor_customer_count;i++) { // Defining vendor and customer Threads.
            int id = i;
            customers.add(new Thread(new Customer(id, customerDelayTime)));
            List<Object> parameters = new ArrayList<>();
            parameters.add(id);
            parameters.add("Customer "+id);

            db.writeDatabase("INSERT INTO customers (id, name) VALUES (?, ?)", parameters);

            vendors.add(new Thread(new Vendor(id, vendorDelayTime)));
            parameters = new ArrayList<>();
            parameters.add(id);
            parameters.add("Vendor "+id);

            db.writeDatabase("INSERT INTO vendors (id, name) VALUES (?, ?)", parameters);
        }

        for(int i=1;i<=vendor_customer_count;i++){ // Starting vendor and customer Threads.
            vendors.get(i-1).start();
            customers.get(i-1).start();
        }
        LogManager.log("Vendors Started");

        while(TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets() && isRunningSimulator()){
            // waiting until all the tickets have been booked or the simulation is stopped manually
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets()){
            // if all the tickets have not been booked then the program was stopped manually
            LogManager.log("Simulation was stopped");
        }

        stopSimulation();
        // sets runningSimulator to false stopping the
        // customers and vendors from trying to write to the ticket pool
        LogManager.log("Terminating Threads");
        for (int i = 1; i <= vendor_customer_count; i++) {
            try {
                vendors.get(i - 1).interrupt();
                customers.get(i - 1).interrupt();
                // terminating the threads
                vendors.get(i - 1).join();
                customers.get(i - 1).join();
                // Joining the threads
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        LogManager.log("Tickets in ticket pool: " + (TicketPool.getTicketsListSize()) );
        LogManager.log("Added Tickets Count: " + (TicketPool.getTicketCount()));

        LogManager.log("Booked tickets count: " + (TicketPool.getTicketBookedCount()));
        LogManager.writeToFile();
        setRunSimulationEnd(true);
        // indicating that the runSimulation method itself has finished running.
        // If this is false that means the simulation has been stopped or ended AND that
        // the summary has also been added to the logs.
    }

    /**
     *
     */
    public static void initialise(){
        db.setup();
        LogManager.clearLogs();
        TicketPool.resetTicketPool();
        initialiseCustomerBookings();
        initialiseVendorTickets();
    }

    /**
     * set runningSimulator flag to false.(and stops the program in the process)
     */
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

    /**
     * Set the runningSimulator flag to true.
     */
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

    /**
     * check if the simulation.
     * @return runningSimulator
     */
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

    /**
     *
     */
    public static void initialiseCustomerBookings(){ // setup the customer bookings HashMap
        customerBookings = new HashMap<>();
        for(int i=1; i<=vendor_customer_count; i++){
            customerBookings.put(i, 0);
        }
    }

    /**
     *
     */
    public static void initialiseVendorTickets(){ // setup the vendor tickets HashMap
        vendorTickets = new HashMap<>();
        for(int i=1; i<=vendor_customer_count; i++){
            vendorTickets.put(i, 0);
        }

    }

    /**
     *
     * @param id customer id
     */
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

    /**
     *
     * @param id vendor id
     */
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

    /**
     *
     * @return customerBookings List
     */
    public static HashMap<Integer, Integer> getCustomerBookings() {
        return customerBookings;
    }

    /**
     *
     * @return vendorTickets List
     */
    public static HashMap<Integer, Integer> getVendorTickets() {
        return vendorTickets;
    }

    /**
     *
     * @return runSimulationEnd
     */
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

    /**
     * check if runSimulationEnd function is still running asynchronously without creating a lock.
     * @return runSimulationEnd
     */
    public static boolean asyncIsRunSimulationEnd() {
            return runSimulationEnd;
    }

    /**
     * set that run simulation has ended.
     * @param runSimulationEnd value to set runSimulationEnd to
     */
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
