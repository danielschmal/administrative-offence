import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents an appeal against a regulatory fine
 */
@Data
@RequiredArgsConstructor
public class Appeal {
    private final String id = UUID.randomUUID().toString();
    
    @NonNull
    private String reason;
    
    private final LocalDate filingDate = LocalDate.now();
    
    private LocalDate decisionDate;
    
    private boolean approved = false;
    
    private String decisionReason;
    
    private String reviewedBy;
    
    private List<String> supportingDocuments = new ArrayList<>();
    
    /**
     * Records a decision on the appeal
     * @param approved Whether the appeal is approved
     * @param reason The reason for the decision
     * @param reviewerName The name of the reviewer
     */
    public void recordDecision(boolean approved, String reason, String reviewerName) {
        this.approved = approved;
        this.decisionReason = reason;
        this.reviewedBy = reviewerName;
        this.decisionDate = LocalDate.now();
    }
    
    /**
     * Adds a supporting document to the appeal
     * @param documentName The name or description of the document
     */
    public void addSupportingDocument(String documentName) {
        supportingDocuments.add(documentName);
    }
    
    /**
     * Checks if the appeal has been decided
     * @return true if a decision has been made on the appeal
     */
    public boolean isDecided() {
        return decisionDate != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Appeal [Filed=" + filingDate + ", Reason=" + reason);
        
        if (isDecided()) {
            sb.append(", Decision=" + (approved ? "Approved" : "Rejected"));
            sb.append(", Decision Date=" + decisionDate);
            sb.append(", Decision Reason=" + decisionReason);
            sb.append(", Reviewed By=" + reviewedBy);
        } else {
            sb.append(", Status=Pending");
        }
        
        sb.append("]");
        return sb.toString();
    }
}
