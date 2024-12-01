package com.app.ticket_booking_simulator;

public class Customer implements Runnable{
    private int id;
    private long delayTime;
    private String name;


    public Customer(int id, long delayTime) {
        this.id = id;
        this.delayTime = delayTime;
    }

    @Override
    public void run(){
        while (TicketPool.getTicketBookedCount()<TicketPool.getTotalTickets() && SimulatorManager.isRunningSimulator()){
            //System.out.println("customer "+id+" is waiting.");
            TicketPool.bookTicket(this.getId());
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