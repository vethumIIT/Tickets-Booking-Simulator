package com.app.ticket_booking_simulator;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Configuration {
    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args){

        int totalTickets = getIntInput("Enter total tickets: ");
        int ticketReleaseRate = getIntInput("Enter ticket release rate: ");
        int customerRetrievalRate = getIntInput("Enter customer retrieval rate: ");
        int maxTicketCapacity = getIntInput("Enter maximum ticket capacity: ");

        TicketPool.setTotalTickets(totalTickets);
        TicketPool.setTicketReleaseRate(ticketReleaseRate);
        TicketPool.setCustomerRetrievalRate(customerRetrievalRate);
        TicketPool.setMaxTicketCapacity(maxTicketCapacity);

        SimulatorManager.runSimulation();
    }

    public static int getIntInput(String promptText){
        boolean validValue = false;
        int returnValue = -1;
        while(!validValue) {
            try {
                System.out.print(promptText);
                returnValue = input.nextInt();
                validValue = true;
            } catch (InputMismatchException e) {
                System.out.println("Your input must be a text");
            }
        }
        if (returnValue==-1){
            System.exit(1);
        }
        return returnValue;


    }
}
