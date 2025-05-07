import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

/**
 * Represents a monetary fine associated with a regulatory offense
 */
@Data
public class Fine {
    private final String id = UUID.randomUUID().toString();
    private double amount;
    private LocalDate issueDate;
    private LocalDate paymentDeadline;
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    private List<Payment> paymentHistory = new ArrayList<>();
    private double totalPaid = 0.0;
    
    /**
     * Creates a new fine with the specified amount and calculates the payment deadline
     * @param amount The monetary amount of the fine
     * @param daysToPay The number of days allowed for payment
     */
    public Fine(double amount, int daysToPay) {
        this.amount = amount;
        this.issueDate = LocalDate.now();
        this.paymentDeadline = issueDate.plusDays(daysToPay);
    }
    
    /**
     * Records a payment towards the fine
     * @param payment The payment to record
     */
    public void recordPayment(Payment payment) {
        paymentHistory.add(payment);
        totalPaid += payment.getAmount();
        updatePaymentStatus();
    }
    
    /**
     * Updates the payment status based on the total amount paid
     */
    private void updatePaymentStatus() {
        if (totalPaid >= amount) {
            paymentStatus = PaymentStatus.PAID;
        } else if (totalPaid > 0) {
            paymentStatus = PaymentStatus.PARTIALLY_PAID;
        } else {
            paymentStatus = PaymentStatus.UNPAID;
        }
    }
    
    /**
     * Checks if the payment is overdue
     * @return true if the current date is after the payment deadline and the fine is not fully paid
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(paymentDeadline) && paymentStatus != PaymentStatus.PAID;
    }
    
    /**
     * Gets the remaining balance to be paid
     * @return The amount remaining to be paid
     */
    public double getRemainingBalance() {
        return Math.max(0, amount - totalPaid);
    }
    
    @Override
    public String toString() {
        return "Fine [Amount=$" + amount + ", Issued=" + issueDate + 
               ", Due=" + paymentDeadline + ", Status=" + paymentStatus + 
               ", Paid=$" + totalPaid + ", Remaining=$" + getRemainingBalance() + "]";
    }
}
