import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents an individual who has committed a regulatory offense
 */
@Data
@RequiredArgsConstructor
public class Offender {
    private final String id = UUID.randomUUID().toString();
    
    @NonNull
    private String fullName;
    
    @NonNull
    private String address;
    
    @NonNull
    private LocalDate dateOfBirth;
    
    private List<RegulatoryCase> offenseHistory = new ArrayList<>();
    
    /**
     * Adds a case to the offender's history
     * @param regulatoryCase The case to add
     */
    public void addCaseToHistory(RegulatoryCase regulatoryCase) {
        offenseHistory.add(regulatoryCase);
    }
    
    /**
     * Checks if the offender is a repeat offender based on previous history
     * @param offenseType The type of offense to check
     * @return true if the offender has previously committed the same type of offense
     */
    public boolean isRepeatOffender(OffenseType offenseType) {
        return offenseHistory.stream()
                .anyMatch(c -> c.getOffense().getOffenseType() == offenseType);
    }
    
    /**
     * Gets the number of previous offenses of a specific type
     * @param offenseType The type of offense to count
     * @return The number of previous offenses
     */
    public int getOffenseCount(OffenseType offenseType) {
        return (int) offenseHistory.stream()
                .filter(c -> c.getOffense().getOffenseType() == offenseType)
                .count();
    }
    
    @Override
    public String toString() {
        return "Offender [ID=" + id + ", Name=" + fullName + ", DOB=" + dateOfBirth + 
               ", Address=" + address + ", Previous offenses=" + offenseHistory.size() + "]";
    }
}
