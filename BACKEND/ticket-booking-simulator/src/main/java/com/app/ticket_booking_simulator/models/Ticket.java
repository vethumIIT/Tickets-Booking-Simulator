package com.app.ticket_booking_simulator.models;

/**
 * Represents a Ticket
 */
public class Ticket {
    private int vendorId;
    private int customerId;
    private int ticketId;
    private boolean isAvailable = true;

    /**
     * The vendor class constructor.
     * @param vendorId Id of the vendor.
     */
    public Ticket(int vendorId) {
        this.vendorId = vendorId;
    }

    /**
     *
     * @return vendorId
     */
    public int getVendorId() {
        return vendorId;
    }

    /**
     *
     * @param vendorId vendorsId
     */
    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    /**
     *
     * @return customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     *
     * @param customerId customerId
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     *
     * @return available
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     *
     * @param available value to set
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     *
     * @return ticketId
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     *
     * @param ticketId value to set
     */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

}
