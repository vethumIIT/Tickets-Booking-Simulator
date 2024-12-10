package com.app.ticket_booking_simulator.controller;

import com.app.ticket_booking_simulator.services.LogManager;
import com.app.ticket_booking_simulator.services.SimulatorManager;
import com.app.ticket_booking_simulator.models.Configuration;
import com.app.ticket_booking_simulator.models.Ticket;
import com.app.ticket_booking_simulator.services.TicketPool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * App Controller class where the end points are added.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AppController {

    /**
     * endpoint to start the simulation.
     * @param config
     * @return String version of the status
     */
    @RequestMapping("/api/start")
    public ResponseEntity<String> start(@RequestBody Configuration config){
        if (!config.isValid()){
            return ResponseEntity.ok("all fields must have a minimum value of 1");
        }

        System.out.println("right before entering the if statement where we start simulator");
        if (!SimulatorManager.isRunningSimulator()) {
            System.out.println("starting run simulation");
            SimulatorManager.runSimulation(config);
        }else {
            System.out.println("thread is already running");
            return ResponseEntity.ok("Running");
        }

        System.out.println(LogManager.getLogs().size());

        return ResponseEntity.ok("Ended");
    }

    /**
     * endpoint to retrieve the logs
     * @return logs in a List<String>
     */
    @RequestMapping("/api/get_logs")
    public ResponseEntity<List<String>> get_logs(){
        return ResponseEntity.ok(LogManager.getLogs());
    }

    /**
     * endpoint to get the tickets in the Ticket Pool.
     * @return a List containing Ticket Objects
     */
    @RequestMapping("/api/get_tickets")
    public ResponseEntity<List<Ticket>> getTickets() {
        return ResponseEntity.ok(TicketPool.getAsyncTicketsList());
    }

    /**
     * Endpoint to get the customer bookings
     * @return Hashmap containing customer name and number of tickets booked count.
     */
    @RequestMapping("/api/get_customer_bookings")
    public ResponseEntity<Map<Integer, Integer>> getCustomerBookings() {
        return ResponseEntity.ok(SimulatorManager.getCustomerBookings());
    }

    /**
     * Endpoint to get the customer tickets
     * @return HashMap containing vendor name and number of tickets added.
     */
    @RequestMapping("/api/get_vendor_tickets")
    public ResponseEntity<Map<Integer, Integer>> getVendorTickets() {
        return ResponseEntity.ok(SimulatorManager.getVendorTickets());
    }

    /**
     * Endpoint to stop the simulation
     * @return returns a String once the simulation has stopped.
     */
    @RequestMapping("/api/stop")
    public ResponseEntity<String> stopSimulation(){
        SimulatorManager.stopSimulation();// stopping the threads an blocking additional changes to ticket pool
        while(!SimulatorManager.asyncIsRunSimulationEnd()){/*wait until the simulation has stopped*/}
        // waiting for the runSimulation method to finish running.
        return ResponseEntity.ok("Stopped");
    }

    /**
     * Endpoint to check if the simulation is running
     * @return true if the simulation is running.
     */
    @RequestMapping("/api/is_running")
    public ResponseEntity<String> isRunning(){
        boolean simIsRunning = SimulatorManager.asyncIsRunSimulationEnd();
        // check if the runSimulation method is running.
        return ResponseEntity.ok(String.valueOf(!simIsRunning));
    }

    /**
     * Endpoint to get the stats
     * @return JSON String containing the stats.
     */
    @RequestMapping("/api/get_stats")
    public ResponseEntity<String> getStats(){
        return ResponseEntity.ok(TicketPool.getStatus());
    }

}
