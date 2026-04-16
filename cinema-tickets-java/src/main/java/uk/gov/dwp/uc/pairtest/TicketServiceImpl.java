package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

/**
 * Ticket service implementation enforcing business rules before delegating
 * to payment and seat-reservation providers.
 */
public class TicketServiceImpl implements TicketService {

    private static final int ADULT_TICKET_PRICE = 25;
    private static final int CHILD_TICKET_PRICE = 15;
    private static final int MAX_TICKETS_PER_TRANSACTION = 25;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        validateAccountId(accountId);
        validateTicketRequests(ticketTypeRequests);

        int adultCount = countTicketsOfType(ticketTypeRequests, Type.ADULT);
        int childCount = countTicketsOfType(ticketTypeRequests, Type.CHILD);
        int infantCount = countTicketsOfType(ticketTypeRequests, Type.INFANT);
        int totalTickets = adultCount + childCount + infantCount;

        validateBusinessRules(adultCount, childCount, infantCount, totalTickets);

        int totalAmountToPay = calculateTotalAmount(adultCount, childCount);
        int totalSeatsToReserve = calculateTotalSeats(adultCount, childCount);

        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalSeatsToReserve);
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException(
                    "Invalid account ID: account ID must be greater than zero.");
        }
    }

    private void validateTicketRequests(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException(
                    "Invalid purchase request: at least one ticket request must be provided.");
        }
        for (TicketTypeRequest request : requests) {
            if (request == null) {
                throw new InvalidPurchaseException(
                        "Invalid purchase request: ticket request cannot be null.");
            }
            if (request.getNoOfTickets() < 0) {
                throw new InvalidPurchaseException(
                        "Invalid purchase request: ticket quantity cannot be negative.");
            }
        }
    }

    private void validateBusinessRules(int adults, int children, int infants, int total) {
        if (total == 0) {
            throw new InvalidPurchaseException(
                    "Invalid purchase request: at least one ticket must be purchased.");
        }

        if (total > MAX_TICKETS_PER_TRANSACTION) {
            throw new InvalidPurchaseException(
                    "Invalid purchase request: a maximum of " + MAX_TICKETS_PER_TRANSACTION
                            + " tickets can be purchased per transaction. Requested: " + total + ".");
        }

        if (adults == 0 && (children > 0 || infants > 0)) {
            throw new InvalidPurchaseException(
                    "Invalid purchase request: Child and Infant tickets cannot be purchased "
                            + "without at least one Adult ticket.");
        }

        if (infants > adults) {
            throw new InvalidPurchaseException(
                    "Invalid purchase request: the number of Infants (" + infants
                            + ") cannot exceed the number of Adults (" + adults
                            + ") as each Infant must sit on an Adult's lap.");
        }
    }

    private int countTicketsOfType(TicketTypeRequest[] requests, Type type) {
        int count = 0;
        for (TicketTypeRequest request : requests) {
            if (request.getTicketType() == type) {
                count += request.getNoOfTickets();
            }
        }
        return count;
    }

    private int calculateTotalAmount(int adults, int children) {
        return (adults * ADULT_TICKET_PRICE) + (children * CHILD_TICKET_PRICE);
    }

    private int calculateTotalSeats(int adults, int children) {
        return adults + children;
    }
}

