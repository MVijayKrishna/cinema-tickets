package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable value object representing a request for a quantity of a particular ticket type.
 * This class CANNOT be modified per the exercise constraints.
 */
public final class TicketTypeRequest {

    private final Type ticketType;
    private final int noOfTickets;

    public TicketTypeRequest(Type ticketType, int noOfTickets) {
        this.ticketType = ticketType;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return ticketType;
    }

    public enum Type {
        ADULT, CHILD, INFANT
    }
}