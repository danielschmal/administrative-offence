import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages all regulatory cases in the system
 */
public class CaseManager {
    // In-memory storage of cases (would be replaced by database in a real application)
    private Map<String, RegulatoryCase> cases = new HashMap<>();
    private Map<String, Offender> offenders = new HashMap<>();
    
    // Configuration parameters
    private static final int PAYMENT_DEADLINE_DAYS = 30;
    private static final int STATUTE_OF_LIMITATIONS_MONTHS = 24;
    private static final int REMINDER_INTERVAL_DAYS = 14;
    
    /**
     * Creates a new regulatory case
     * @param offense The offense details
     * @return The created case
     */
    public RegulatoryCase createNewCase(Offense offense) {
        // Store offender if not already in system
        Offender offender = offense.getOffender();
        if (!offenders.containsKey(offender.getId())) {
            offenders.put(offender.getId(), offender);
        } else {
            // Use the existing offender record to maintain history
            offender = offenders.get(offender.getId());
            offense.setOffender(offender);
        }
        
        // Calculate fine amount based on offense type and history
        double fineAmount = calculateFineAmount(offense);
        Fine fine = new Fine(fineAmount, PAYMENT_DEADLINE_DAYS);
        
        // Create the case
        RegulatoryCase regulatoryCase = new RegulatoryCase(offense, fine);
        regulatoryCase.updateStatus(CaseStatus.FINE_ISSUED, "Fine notice issued");
        
        // Add to offender's history
        offender.addCaseToHistory(regulatoryCase);
        
        // Store the case
        cases.put(regulatoryCase.getCaseId(), regulatoryCase);
        
        return regulatoryCase;
    }
    
    /**
     * Calculates the fine amount based on offense type and offender history
     * @param offense The offense details
     * @return The calculated fine amount
     */
    private double calculateFineAmount(Offense offense) {
        OffenseType offenseType = offense.getOffenseType();
        Offender offender = offense.getOffender();
        
        // Start with base fine
        double baseFine = offenseType.getBaseFineAmount();
        
        // Apply penalty factor for repeat offenders
        double penaltyFactor = 1.0;
        int previousOffenses = offender.getOffenseCount(offenseType);
        
        if (previousOffenses > 0) {
            // Increase penalty factor for each previous offense of the same type
            penaltyFactor += (previousOffenses * 0.25);
        }
        
        return offenseType.calculateFine(penaltyFactor);
    }
    
    /**
     * Retrieves a case by its ID
     * @param caseId The case ID
     * @return The case or null if not found
     */
    public RegulatoryCase getCaseById(String caseId) {
        return cases.get(caseId);
    }
    
    /**
     * Records a payment for a case
     * @param regulatoryCase The case
     * @param amount The payment amount
     * @return The payment object
     */
    public Payment recordPayment(RegulatoryCase regulatoryCase, double amount) {
        Payment payment = new Payment(amount, PaymentMethod.BANK_TRANSFER);
        regulatoryCase.recordPayment(payment);
        
        // Check if case can be closed due to full payment
        if (regulatoryCase.getFine().getPaymentStatus() == PaymentStatus.PAID &&
            regulatoryCase.getAppeal() == null) {
            regulatoryCase.closeCase("Fine paid in full");
        }
        
        return payment;
    }
    
    /**
     * Files an appeal for a case
     * @param regulatoryCase The case
     * @param reason The appeal reason
     * @return The appeal object
     */
    public Appeal fileAppeal(RegulatoryCase regulatoryCase, String reason) {
        return regulatoryCase.fileAppeal(reason);
    }
    
    /**
     * Decides on an appeal
     * @param regulatoryCase The case
     * @param approved Whether the appeal is approved
     * @param reason The decision reason
     * @param reviewerName The name of the reviewer
     */
    public void decideAppeal(RegulatoryCase regulatoryCase, boolean approved, 
                            String reason, String reviewerName) {
        regulatoryCase.decideAppeal(approved, reason, reviewerName);
        
        if (approved) {
            regulatoryCase.closeCase("Appeal approved");
        }
    }
    
    /**
     * Processes reminders for all cases with overdue payments
     * @return The number of reminders sent
     */
    public int processReminders() {
        int remindersSent = 0;
        
        for (RegulatoryCase regulatoryCase : cases.values()) {
            if (regulatoryCase.sendReminder()) {
                remindersSent++;
            }
        }
        
        return remindersSent;
    }
    
    /**
     * Gets cases by their current status
     * @param status The status to filter by
     * @return List of cases with the specified status
     */
    public List<RegulatoryCase> getCasesByStatus(CaseStatus status) {
        return cases.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets cases that are approaching statute of limitations
     * @param warningDays Days before expiration to include in warning list
     * @return List of cases approaching statute of limitations
     */
    public List<RegulatoryCase> getCasesApproachingStatuteOfLimitations(int warningDays) {
        LocalDate warningDate = LocalDate.now().plusDays(warningDays);
        
        return cases.values().stream()
                .filter(c -> c.getStatus() != CaseStatus.CLOSED)
                .filter(c -> {
                    LocalDate expirationDate = c.getOffense().getOffenseDate()
                            .plusMonths(STATUTE_OF_LIMITATIONS_MONTHS);
                    return !expirationDate.isAfter(warningDate);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all cases in the system
     * @return List of all cases
     */
    public List<RegulatoryCase> getAllCases() {
        return new ArrayList<>(cases.values());
    }
    
    /**
     * Gets cases for a specific offender
     * @param offenderId The offender ID
     * @return List of cases for the offender
     */
    public List<RegulatoryCase> getCasesByOffender(String offenderId) {
        return cases.values().stream()
                .filter(c -> c.getOffense().getOffender().getId().equals(offenderId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets offender by ID
     * @param offenderId The offender ID
     * @return The offender or null if not found
     */
    public Offender getOffenderById(String offenderId) {
        return offenders.get(offenderId);
    }
}
