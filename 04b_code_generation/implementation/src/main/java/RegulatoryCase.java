import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents a complete regulatory case including offense, fine, and current status
 */
@Data
public class RegulatoryCase {
    private final String caseId = UUID.randomUUID().toString();

    @NonNull
    private Offense offense;

    @NonNull
    private Fine fine;

    private Appeal appeal;

    private CaseStatus status = CaseStatus.CREATED;

    private final LocalDate creationDate = LocalDate.now();

    private LocalDate closedDate;

    private List<CaseAction> caseHistory = new ArrayList<>();

    /**
     * Creates a new regulatory case with the specified offense and fine
     * @param offense The offense that occurred
     * @param fine The fine imposed
     */
    public RegulatoryCase(Offense offense, Fine fine) {
        this.offense = offense;
        this.fine = fine;

        // Add initial actions to the case history
        addAction(ActionType.CASE_CREATED, "Case created for " + offense.getOffenseType() + " by " + offense.getOffender().getFullName());
        addAction(ActionType.FINE_CALCULATED, "Fine calculated: $" + fine.getAmount());
    }

    /**
     * Adds an action to the case history
     * @param actionType The type of action
     * @param description Description of the action
     * @return The created case action
     */
    public CaseAction addAction(ActionType actionType, String description) {
        CaseAction action = new CaseAction(actionType, description);
        caseHistory.add(action);
        return action;
    }

    /**
     * Updates the status of the case
     * @param newStatus The new status
     * @param reason The reason for the status change
     */
    public void updateStatus(CaseStatus newStatus, String reason) {
        CaseStatus oldStatus = this.status;
        this.status = newStatus;

        addAction(ActionType.STATUS_UPDATED,
                "Status changed from " + oldStatus + " to " + newStatus + ": " + reason);

        if (newStatus == CaseStatus.CLOSED) {
            this.closedDate = LocalDate.now();
        }
    }

    /**
     * Records an appeal against the fine
     * @param reason The reason for the appeal
     * @return The created appeal
     */
    public Appeal fileAppeal(String reason) {
        if (this.appeal != null) {
            throw new IllegalStateException("An appeal has already been filed for this case");
        }

        this.appeal = new Appeal(reason);
        updateStatus(CaseStatus.APPEAL_FILED, "Appeal filed: " + reason);
        addAction(ActionType.APPEAL_RECEIVED, "Appeal received: " + reason);

        return this.appeal;
    }

    /**
     * Records a payment towards the fine
     * @param payment The payment to record
     */
    public void recordPayment(Payment payment) {
        fine.recordPayment(payment);

        addAction(ActionType.PAYMENT_RECEIVED,
                "Payment received: $" + payment.getAmount() + " via " + payment.getPaymentMethod());

        if (fine.getPaymentStatus() == PaymentStatus.PAID) {
            updateStatus(CaseStatus.PAID, "Fine paid in full");
        } else {
            updateStatus(CaseStatus.PAYMENT_PENDING, "Partial payment received");
        }
    }

    /**
     * Checks if the case is still within statute of limitations
     * @param limitInMonths The number of months in the statute of limitations
     * @return true if the case is still actionable
     */
    public boolean isWithinStatuteOfLimitations(int limitInMonths) {
        return offense.isWithinStatuteOfLimitations(LocalDate.now(), limitInMonths);
    }

    /**
     * Sends a reminder for unpaid or partially paid fines
     * @return true if a reminder was sent
     */
    public boolean sendReminder() {
        if (fine.isOverdue() && status != CaseStatus.CLOSED && status != CaseStatus.PAID) {
            addAction(ActionType.REMINDER_SENT, "Payment reminder sent for overdue fine");
            updateStatus(CaseStatus.PAYMENT_OVERDUE, "Payment deadline passed without full payment");
            return true;
        }
        return false;
    }

    /**
     * Decides on an appeal and updates the case accordingly
     * @param approved Whether the appeal is approved
     * @param reason The reason for the decision
     * @param reviewerName The name of the reviewer
     */
    public void decideAppeal(boolean approved, String reason, String reviewerName) {
        if (appeal == null) {
            throw new IllegalStateException("No appeal has been filed for this case");
        }

        appeal.recordDecision(approved, reason, reviewerName);

        if (approved) {
            updateStatus(CaseStatus.APPEAL_APPROVED, "Appeal approved: " + reason);
            closedDate = LocalDate.now();
        } else {
            updateStatus(CaseStatus.APPEAL_REJECTED, "Appeal rejected: " + reason);
        }

        addAction(ActionType.APPEAL_DECISION,
                "Appeal " + (approved ? "approved" : "rejected") + ": " + reason);
    }

    /**
     * Closes the case
     * @param reason The reason for closing the case
     */
    public void closeCase(String reason) {
        updateStatus(CaseStatus.CLOSED, reason);
        this.closedDate = LocalDate.now();
        addAction(ActionType.CASE_CLOSED, "Case closed: " + reason);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Case [ID=").append(caseId)
                .append(", Status=").append(status)
                .append(", Created=").append(creationDate);

        if (closedDate != null) {
            sb.append(", Closed=").append(closedDate);
        }

        sb.append("]\n")
                .append("Offense: ").append(offense).append("\n")
                .append("Fine: ").append(fine);

        if (appeal != null) {
            sb.append("\nAppeal: ").append(appeal);
        }

        return sb.toString();
    }
}