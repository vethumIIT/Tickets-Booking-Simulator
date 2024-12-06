package com.app.ticket_booking_simulator.models;

import com.app.ticket_booking_simulator.services.SimulatorManager;
import com.app.ticket_booking_simulator.services.TicketPool;

public class Customer implements Runnable{
    private int id;
    private long delayTime;


    public Customer(int id, long delayTime) {
        this.id = id;
        this.delayTime = delayTime;
    }

    @Override
    public void run(){
        while (TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets()
                && SimulatorManager.isRunningSimulator()
                && !Thread.interrupted()
        ){// if all the tickets have not been booked, the simulation is manually stopped or the thread has not been interrupted
            TicketPool.bookTicket(this.getId());
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
