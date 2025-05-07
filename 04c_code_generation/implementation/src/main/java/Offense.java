import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*Represents a specific regulatory offense incident*/
@Data
@RequiredArgsConstructor
public class Offense {
    private final String id = UUID.randomUUID().toString();

    @NonNull
    private Offender offender;

    @NonNull
    private String location;

    @NonNull
    private LocalDate offenseDate;

    @NonNull
    private OffenseType offenseType;

    private String evidenceDescription;

/*
    Adds evidence description to the offense
    @param description Description of the evidence
 */
    public void addEvidence(String description) {
        this.evidenceDescription = description;
    }

    /**

     Checks if the offense is within the statute of limitations
     @param currentDate The current date to check against
     @param limitInMonths The number of months in the statute of limitations
     @return true if the offense is still within the statute of limitations*/
    public boolean isWithinStatuteOfLimitations(LocalDate currentDate, int limitInMonths) {
        return offenseDate.plusMonths(limitInMonths).isAfter(currentDate);
    }

    @Override
    public String toString() {
        return "Offense [Type=" + offenseType + ", Date=" + offenseDate + ", Location=" + location + ", Offender=" + offender.getFullName() + "]";}
}