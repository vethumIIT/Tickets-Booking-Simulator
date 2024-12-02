package com.app.ticket_booking_simulator;

public class Vendor implements Runnable{
    private int id;
    private long delayTime;

    public Vendor(int id, int delayTime) {
        this.id = id;
        this.delayTime = delayTime;
    }

    @Override
    public void run(){
        while (TicketPool.getTicketCount()<TicketPool.getTotalTickets()
                && SimulatorManager.isRunningSimulator()
                && !Thread.interrupted()
        ){
            TicketPool.createTicket(new Ticket(this.getId()));
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
