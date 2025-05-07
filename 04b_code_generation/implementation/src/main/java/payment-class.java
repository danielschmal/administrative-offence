import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a payment made towards a fine
 */
@Data
@RequiredArgsConstructor
public class Payment {
    private final String id = UUID.randomUUID().toString();
    
    @NonNull
    private Double amount;
    
    private LocalDate paymentDate = LocalDate.now();
    
    @NonNull
    private PaymentMethod paymentMethod;
    
    private String referenceNumber;
    
    /**
     * Sets a reference number for the payment (e.g., transaction ID, check number)
     * @param referenceNumber The reference number
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    @Override
    public String toString() {
        return "Payment [Amount=$" + amount + ", Date=" + paymentDate + 
               ", Method=" + paymentMethod + 
               (referenceNumber != null ? ", Reference=" + referenceNumber : "") + "]";
    }
}
