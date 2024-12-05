package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.services.SimulatorManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:5173")
@ComponentScan(basePackages = {
		"com.app.ticket_booking_simulator.controller"
})
public class TicketBookingSimApp {
	public static void main(String[] args) {

		SpringApplication.run(TicketBookingSimApp.class, args);
	}

}
