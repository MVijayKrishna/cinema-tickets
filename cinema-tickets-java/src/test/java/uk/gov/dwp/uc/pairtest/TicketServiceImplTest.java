package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TicketServiceImpl.
 *
 * Uses Mockito to stub out the third-party TicketPaymentService and
 * SeatReservationService so that tests are fully isolated and verify only the
 * business-logic decisions made by TicketServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    // =========================================================================
    // Account ID validation
    // =========================================================================

    @Nested
    @DisplayName("Account ID validation")
    class AccountIdValidation {

        @Test
        @DisplayName("Should throw when account ID is zero")
        void shouldThrowWhenAccountIdIsZero() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 1);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(0L, request));
        }

        @Test
        @DisplayName("Should throw when account ID is negative")
        void shouldThrowWhenAccountIdIsNegative() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 1);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(-1L, request));
        }

        @Test
        @DisplayName("Should throw when account ID is null")
        void shouldThrowWhenAccountIdIsNull() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 1);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(null, request));
        }

        @Test
        @DisplayName("Should accept account ID of 1 (minimum valid)")
        void shouldAcceptMinimumValidAccountId() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 1);
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L, request));
        }
    }

    // =========================================================================
    // Ticket request validation
    // =========================================================================

    @Nested
    @DisplayName("Ticket request validation")
    class TicketRequestValidation {

        @Test
        @DisplayName("Should throw when no ticket requests are provided")
        void shouldThrowWhenNoRequestsProvided() {
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L));
        }

        @Test
        @DisplayName("Should throw when ticket quantity is zero across all requests")
        void shouldThrowWhenAllQuantitiesAreZero() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 0);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, request));
        }

        @Test
        @DisplayName("Should throw when a request has a negative quantity")
        void shouldThrowWhenQuantityIsNegative() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, -1);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, request));
        }
    }

    // =========================================================================
    // Maximum ticket limit
    // =========================================================================

    @Nested
    @DisplayName("Maximum 25 ticket limit")
    class MaxTicketLimit {

        @Test
        @DisplayName("Should accept exactly 25 tickets")
        void shouldAccept25Tickets() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 25);
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L, request));
        }

        @Test
        @DisplayName("Should throw when total exceeds 25 tickets")
        void shouldThrowWhen26TicketsRequested() {
            TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 26);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, request));
        }

        @Test
        @DisplayName("Should throw when mix of ticket types totals more than 25")
        void shouldThrowWhenMixedTotalExceeds25() {
            // 20 adults + 5 children + 1 infant = 26
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L,
                            new TicketTypeRequest(Type.ADULT,  20),
                            new TicketTypeRequest(Type.CHILD,   5),
                            new TicketTypeRequest(Type.INFANT,  1)));
        }

        @Test
        @DisplayName("Should accept 25 tickets across multiple types")
        void shouldAccept25TicketsAcrossTypes() {
            // 20 adults + 3 children + 2 infants = 25
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  20),
                    new TicketTypeRequest(Type.CHILD,   3),
                    new TicketTypeRequest(Type.INFANT,  2)));
        }
    }

    // =========================================================================
    // Adult requirement for Child / Infant tickets
    // =========================================================================

    @Nested
    @DisplayName("Adult required with Child/Infant tickets")
    class AdultRequirement {

        @Test
        @DisplayName("Should throw when only Child tickets purchased")
        void shouldThrowWhenOnlyChildTickets() {
            TicketTypeRequest request = new TicketTypeRequest(Type.CHILD, 2);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, request));
        }

        @Test
        @DisplayName("Should throw when only Infant tickets purchased")
        void shouldThrowWhenOnlyInfantTickets() {
            TicketTypeRequest request = new TicketTypeRequest(Type.INFANT, 1);
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, request));
        }

        @Test
        @DisplayName("Should throw when Child and Infant tickets purchased without Adult")
        void shouldThrowWhenChildAndInfantWithoutAdult() {
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L,
                            new TicketTypeRequest(Type.CHILD,  2),
                            new TicketTypeRequest(Type.INFANT, 1)));
        }

        @Test
        @DisplayName("Should accept Child ticket when at least one Adult is present")
        void shouldAcceptChildWithAdult() {
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT, 1),
                    new TicketTypeRequest(Type.CHILD, 3)));
        }

        @Test
        @DisplayName("Should accept Infant ticket when at least one Adult is present")
        void shouldAcceptInfantWithAdult() {
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  1),
                    new TicketTypeRequest(Type.INFANT, 1)));
        }
    }

    // =========================================================================
    // Infant lap constraint
    // =========================================================================

    @Nested
    @DisplayName("Infants must not exceed Adults (lap constraint)")
    class InfantLapConstraint {

        @Test
        @DisplayName("Should throw when infants outnumber adults")
        void shouldThrowWhenMoreInfantsThanAdults() {
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L,
                            new TicketTypeRequest(Type.ADULT,  1),
                            new TicketTypeRequest(Type.INFANT, 2)));
        }

        @Test
        @DisplayName("Should accept when infants equal adults")
        void shouldAcceptWhenInfantsEqualAdults() {
            assertDoesNotThrow(() -> ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  2),
                    new TicketTypeRequest(Type.INFANT, 2)));
        }
    }

    // =========================================================================
    // Payment amount calculations
    // =========================================================================

    @Nested
    @DisplayName("Payment calculations")
    class PaymentCalculations {

        @Test
        @DisplayName("1 Adult should cost £25")
        void oneAdultCosts25() {
            ticketService.purchaseTickets(1L, new TicketTypeRequest(Type.ADULT, 1));
            verify(paymentService).makePayment(1L, 25);
        }

        @Test
        @DisplayName("1 Child should cost £15 (plus mandatory Adult)")
        void oneChildCosts15() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT, 1),
                    new TicketTypeRequest(Type.CHILD, 1));
            // Adult £25 + Child £15 = £40
            verify(paymentService).makePayment(1L, 40);
        }

        @Test
        @DisplayName("Infant tickets should be free (£0)")
        void infantTicketsAreFree() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  1),
                    new TicketTypeRequest(Type.INFANT, 1));
            // Only Adult £25 charged
            verify(paymentService).makePayment(1L, 25);
        }

        @Test
        @DisplayName("Mixed purchase: 3 Adults, 2 Children, 1 Infant = £105")
        void mixedPurchaseCalculation() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  3),
                    new TicketTypeRequest(Type.CHILD,  2),
                    new TicketTypeRequest(Type.INFANT, 1));
            // (3 × £25) + (2 × £15) + (1 × £0) = £75 + £30 = £105
            verify(paymentService).makePayment(1L, 105);
        }

        @Test
        @DisplayName("Multiple Adult-only requests are aggregated correctly")
        void multipleRequestsSameType() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT, 2),
                    new TicketTypeRequest(Type.ADULT, 1));
            // 3 Adults × £25 = £75
            verify(paymentService).makePayment(1L, 75);
        }
    }

    // =========================================================================
    // Seat reservation calculations
    // =========================================================================

    @Nested
    @DisplayName("Seat reservation calculations")
    class SeatReservationCalculations {

        @Test
        @DisplayName("1 Adult reserves 1 seat")
        void oneAdultReservesOneSeat() {
            ticketService.purchaseTickets(1L, new TicketTypeRequest(Type.ADULT, 1));
            verify(reservationService).reserveSeat(1L, 1);
        }

        @Test
        @DisplayName("Infants do not get a seat allocation")
        void infantsDoNotGetSeats() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  2),
                    new TicketTypeRequest(Type.INFANT, 2));
            // Only 2 seats for the Adults; Infants sit on laps
            verify(reservationService).reserveSeat(1L, 2);
        }

        @Test
        @DisplayName("Mixed purchase: 3 Adults, 2 Children, 1 Infant = 5 seats")
        void mixedPurchaseReservesCorrectSeats() {
            ticketService.purchaseTickets(1L,
                    new TicketTypeRequest(Type.ADULT,  3),
                    new TicketTypeRequest(Type.CHILD,  2),
                    new TicketTypeRequest(Type.INFANT, 1));
            // 3 adults + 2 children = 5 seats (1 infant on lap)
            verify(reservationService).reserveSeat(1L, 5);
        }
    }

    // =========================================================================
    // Service delegation
    // =========================================================================

    @Nested
    @DisplayName("Service delegation")
    class ServiceDelegation {

        @Test
        @DisplayName("Both payment and reservation services are called once per valid purchase")
        void bothServicesCalledOnce() {
            ticketService.purchaseTickets(1L, new TicketTypeRequest(Type.ADULT, 2));
            verify(paymentService,     times(1)).makePayment(anyLong(), anyInt());
            verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Neither service is called when request is invalid")
        void noServicesCalledOnInvalidRequest() {
            assertThrows(InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L,
                            new TicketTypeRequest(Type.CHILD, 1)));
            verify(paymentService,     never()).makePayment(anyLong(), anyInt());
            verify(reservationService, never()).reserveSeat(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Correct account ID is passed through to both services")
        void correctAccountIdPassedToServices() {
            long accountId = 42L;
            ticketService.purchaseTickets(accountId, new TicketTypeRequest(Type.ADULT, 1));
            verify(paymentService).makePayment(eq(accountId), anyInt());
            verify(reservationService).reserveSeat(eq(accountId), anyInt());
        }
    }
}
