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
        while (TicketPool.getTicketCount()<TicketPool.getTotalTickets() && SimulatorManager.isRunningSimulator()){
            //System.out.println("vendor "+id+" is waiting.");
            TicketPool.createTicket(new Ticket(this.getId()));
            try {
                Thread.sleep(delayTime*1000);
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
