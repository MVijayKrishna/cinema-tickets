package uk.gov.dwp.uc.pairtest.exception;

/**
 * Thrown when a ticket purchase request violates business rules.
 */
public class InvalidPurchaseException extends RuntimeException {

    public InvalidPurchaseException(String message) {
        super(message);
    }
}