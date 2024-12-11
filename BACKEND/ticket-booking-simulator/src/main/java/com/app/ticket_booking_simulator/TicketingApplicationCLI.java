package com.app.ticket_booking_simulator;

import com.app.ticket_booking_simulator.models.Configuration;
import com.app.ticket_booking_simulator.services.SimulatorManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TicketingApplicationCLI {
    private static Scanner input = new Scanner(System.in);

    /**
     * main class function.
     * @param args
     */
    public static void main(String[] args){

        int totalTickets = getIntInput("Enter total tickets: ");
        int ticketReleaseRate = getIntInput("Enter ticket release rate: ");
        int customerRetrievalRate = getIntInput("Enter customer retrieval rate: ");
        int maxTicketCapacity = getIntInput("Enter maximum ticket capacity: ");


        Configuration config = new Configuration(totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);

        SimulatorManager.runSimulation(config);
    }

    /**
     * Prompts, gets and validates the input.
     * @param promptText - The text to be displayed as the prompt.
     * @return - Integer with the relevant value.
     */
    public static int getIntInput(String promptText){
        boolean validValue = false;
        int returnValue = 0;
        while(!validValue) {
            try {
                System.out.print(promptText);
                returnValue = input.nextInt();
                if (returnValue>=1) {
                    validValue = true;
                }else{
                    System.out.println("value must be 1 or greater");
                }

            } catch (InputMismatchException e) {
                System.out.println("Your input must be an integer");
                input.next();
            }
        }
        return returnValue;
    }
}
