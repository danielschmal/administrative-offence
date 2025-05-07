import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different types of regulatory offenses
 * Each type has a description and base fine amount
 */
@RequiredArgsConstructor
public enum OffenseType {
    PARKING_VIOLATION("Illegal parking in restricted areas", 75.0),
    NOISE_DISTURBANCE("Exceeding permitted noise levels", 150.0),
    WASTE_DISPOSAL("Improper waste disposal", 250.0),
    BUILDING_CODE("Violation of building regulations", 500.0),
    BUSINESS_PERMIT("Operating without proper business permits", 750.0),
    ENVIRONMENTAL("Environmental protection violations", 1000.0),
    FOOD_SAFETY("Food safety and hygiene violations", 800.0),
    PUBLIC_SAFETY("Endangering public safety", 600.0);
    
    @Getter
    private final String description;
    
    @Getter
    private final double baseFineAmount;
    
    /**
     * Calculates the fine amount based on the base amount and penalty factor
     * Penalty factor can increase for repeat offenses or more severe violations
     * 
     * @param penaltyFactor Factor to multiply the base fine by
     * @return The calculated fine amount
     */
    public double calculateFine(double penaltyFactor) {
        return baseFineAmount * penaltyFactor;
    }
}
