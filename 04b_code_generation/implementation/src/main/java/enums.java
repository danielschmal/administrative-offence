/**
 * Enum representing different methods of payment
 */
public enum PaymentMethod {
    CREDIT_CARD,
    BANK_TRANSFER,
    CASH,
    CHECK,
    ONLINE_PAYMENT,
    MONEY_ORDER
}

/**
 * Enum representing the payment status of a fine
 */
public enum PaymentStatus {
    UNPAID,
    PARTIALLY_PAID,
    PAID,
    OVERDUE
}

/**
 * Enum representing the current status of a regulatory case
 */
public enum CaseStatus {
    CREATED,
    FINE_ISSUED,
    PAYMENT_PENDING,
    PAYMENT_OVERDUE,
    APPEAL_FILED,
    APPEAL_UNDER_REVIEW,
    APPEAL_REJECTED,
    APPEAL_APPROVED,
    PAID,
    ESCALATED,
    CLOSED
}

/**
 * Enum representing types of actions that can be taken on a case
 */
public enum ActionType {
    CASE_CREATED,
    FINE_CALCULATED,
    PAYMENT_REQUEST_SENT,
    PAYMENT_RECEIVED,
    REMINDER_SENT,
    APPEAL_RECEIVED,
    APPEAL_DECISION,
    CASE_CLOSED,
    EVIDENCE_ADDED,
    STATUS_UPDATED,
    NOTICE_GENERATED
}
