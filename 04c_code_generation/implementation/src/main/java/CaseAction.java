import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents an action taken on a regulatory case
 */
@Data
@RequiredArgsConstructor
public class CaseAction {
    private final String id = UUID.randomUUID().toString();
    
    @NonNull
    private ActionType actionType;
    
    @NonNull
    private String description;
    
    private final LocalDateTime actionDate = LocalDateTime.now();
    
    private String performedBy = "System";
    
    /**
     * Records the user who performed this action
     * @param username The username of the user who performed the action
     */
    public void setPerformedBy(String username) {
        this.performedBy = username;
    }
    
    @Override
    public String toString() {
        return "Action [Type=" + actionType + ", Description=" + description + 
               ", Date=" + actionDate + ", By=" + performedBy + "]";
    }
}
