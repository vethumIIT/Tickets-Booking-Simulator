package com.app.ticket_booking_simulator.models;

import com.app.ticket_booking_simulator.services.SimulatorManager;
import com.app.ticket_booking_simulator.services.TicketPool;


/**
 * Represents a Vendor
 */
public class Vendor implements Runnable{
    private int id;
    private long delayTime;

    /**
     * The vendor class constructor.
     * @param id - ID of the vendor.
     * @param delayTime - The time interval between each ticket added.
     */
    public Vendor(int id, int delayTime) {
        this.id = id;
        this.delayTime = delayTime;
    }

    /**
     * Run function for Vendor Thread
     */
    @Override
    public void run(){
        while (TicketPool.getTicketCount()<TicketPool.getTotalTickets()
                && SimulatorManager.isRunningSimulator()
                && !Thread.interrupted()
        ){// if all the tickets have not been added, the simulation is manually stopped or the thread has not been interrupted
            TicketPool.createTicket(new Ticket(this.getId()));
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     *
     * @return getId
     */
    public int getId() {
        return id;
    }

    /**
     * set the vendorId
     * @param id value to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
