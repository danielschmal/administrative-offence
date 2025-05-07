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