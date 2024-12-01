package com.app.ticket_booking_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:5173")
@ComponentScan(basePackages = {
		"com.app.ticket_booking_simulator.controller",
		"com.app.ticket_booking_simulator.services",
		"com.app.ticket_booking_simulator.repositories"
})
public class TicketBookingSimApp {
	public static void main(String[] args) {

		Thread simulationManagerThread = new Thread(() -> {
			SimulatorManager.startRunningManager();
		});
		//simulationManagerThread.start();

		SpringApplication.run(TicketBookingSimApp.class, args);
	}

}
