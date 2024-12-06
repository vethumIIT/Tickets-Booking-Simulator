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

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AppController {

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

    @RequestMapping("/api/get_logs")
    public ResponseEntity<List<String>> get_logs(){
        return ResponseEntity.ok(LogManager.getLogs());
    }

    @RequestMapping("/api/get_tickets")
    public ResponseEntity<List<Ticket>> getTickets() {
        return ResponseEntity.ok(TicketPool.getAsyncTicketsList());
    }

    @RequestMapping("/api/get_customer_bookings")
    public ResponseEntity<Map<Integer, Integer>> getCustomerBookings() {
        return ResponseEntity.ok(SimulatorManager.getCustomerBookings());
    }

    @RequestMapping("/api/get_vendor_tickets")
    public ResponseEntity<Map<Integer, Integer>> getVendorTickets() {
        return ResponseEntity.ok(SimulatorManager.getVendorTickets());
    }

    @RequestMapping("/api/stop")
    public ResponseEntity<String> stopSimulation(){
        SimulatorManager.stopSimulation();
        while(!SimulatorManager.isRunSimulationEnd()){/*System.out.println("stopping");*/}
        return ResponseEntity.ok("Stopped");
    }

    @RequestMapping("/api/is_running")
    public ResponseEntity<String> isRunning(){
        boolean simIsRunning = SimulatorManager.isRunSimulationEnd();
        return ResponseEntity.ok(String.valueOf(!simIsRunning));
    }

    @RequestMapping("/api/get_stats")
    public ResponseEntity<String> getStats(){
        return ResponseEntity.ok(TicketPool.getStatus());
    }

}
