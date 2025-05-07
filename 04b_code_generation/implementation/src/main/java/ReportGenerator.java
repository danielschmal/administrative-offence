import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * Generates various reports based on regulatory cases
 */
@RequiredArgsConstructor
public class ReportGenerator {
    private final CaseManager caseManager;
    
    /**
     * Generates a report on monthly fine statistics
     * @return The report as a formatted string
     */
    public String generateMonthlyFineStatistics() {
        List<RegulatoryCase> allCases = caseManager.getAllCases();
        
        // Group by month
        Map<Month, Double> monthlyFines = new HashMap<>();
        Map<Month, Integer> monthlyCounts = new HashMap<>();
        
        for (RegulatoryCase regulatoryCase : allCases) {
            Month month = regulatoryCase.getCreationDate().getMonth();
            double fineAmount = regulatoryCase.getFine().getAmount();
            
            monthlyFines.put(month, monthlyFines.getOrDefault(month, 0.0) + fineAmount);
            monthlyCounts.put(month, monthlyCounts.getOrDefault(month, 0) + 1);
        }
        
        // Build report
        StringBuilder report = new StringBuilder();
        report.append("========================\n");
        report.append("Monthly Fine Statistics\n");
        report.append("========================\n");
        report.append(String.format("%-10s %-15s %-15s %-15s\n", 
                      "Month", "Case Count", "Total Fines", "Average Fine"));
        report.append("--------------------------------------------------------\n");
        
        double totalFines = 0.0;
        int totalCases = 0;
        
        for (Month month : Month.values()) {
            if (monthlyFines.containsKey(month)) {
                double totalMonthlyFines = monthlyFines.get(month);
                int caseCount = monthlyCounts.get(month);
                double averageFine = totalMonthlyFines / caseCount;
                
                report.append(String.format("%-10s %-15d $%-14.2f $%-14.2f\n",
                              month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                              caseCount, totalMonthlyFines, averageFine));
                
                totalFines += totalMonthlyFines;
                totalCases += caseCount;
            }
        }
        
        report.append("--------------------------------------------------------\n");
        report.append(String.format("%-10s %-15d $%-14.2f $%-14.2f\n",
                     "TOTAL", totalCases, totalFines, 
                     totalCases > 0 ? totalFines / totalCases : 0.0));
        
        return report.toString();
    }
    
    /**
     * Generates a report on payment status
     * @return The report as a formatted string
     */
    public String generatePaymentStatusReport() {
        List<RegulatoryCase> allCases = caseManager.getAllCases();
        
        // Count cases by payment status
        Map<PaymentStatus, Integer> statusCounts = new HashMap<>();
        Map<PaymentStatus, Double> statusAmounts = new HashMap<>();
        
        for (RegulatoryCase regulatoryCase : allCases) {
            PaymentStatus status = regulatoryCase.getFine().getPaymentStatus();
            double amount = regulatoryCase.getFine().getAmount();
            
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            statusAmounts.put(status, statusAmounts.getOrDefault(status, 0.0) + amount);
        }
        
        // Build report
        StringBuilder report = new StringBuilder();
        report.append("========================\n");
        report.append("Payment Status Report\n");
        report.append("========================\n");
        report.append(String.format("%-15s %-15s %-15s\n", 
                      "Status", "Case Count", "Total Amount"));
        report.append("-------------------------------------------\n");
        
        int totalCases = 0;
        double totalAmount = 0.0;
        
        for (PaymentStatus status : PaymentStatus.values()) {
            int count = statusCounts.getOrDefault(status, 0);
            double amount = statusAmounts.getOrDefault(status, 0.0);
            
            report.append(String.format("%-15s %-15d $%-14.2f\n",
                         status, count, amount));
            
            totalCases += count;
            totalAmount += amount;
        }
        
        report.append("-------------------------------------------\n");
        report.append(String.format("%-15s %-15d $%-14.2f\n",
                     "TOTAL", totalCases, totalAmount));
        
        // Calculate overdue percentage
        int overdueCases = statusCounts.getOrDefault(PaymentStatus.OVERDUE, 0);
        double overduePercentage = totalCases > 0 ? (overdueCases * 100.0 / totalCases) : 0.0;
        
        report.append("\nPercentage of overdue cases: ").append(String.format("%.2f%%", overduePercentage));
        
        return report.toString();
    }
    
    /**
     * Generates a report on offense type distribution
     * @return The report as a formatted string
     */
    public String generateOffenseTypeDistribution() {
        List<RegulatoryCase> allCases = caseManager.getAllCases();
        
        // Count cases by offense type
        Map<OffenseType, Integer> typeCounts = new HashMap<>();
        Map<OffenseType, Double> typeAmounts = new HashMap<>();
        
        for (RegulatoryCase regulatoryCase : allCases) {
            OffenseType type = regulatoryCase.getOffense().getOffenseType();
            double amount = regulatoryCase.getFine().getAmount();
            
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
            typeAmounts.put(type, typeAmounts.getOrDefault(type, 0.0) + amount);
        }
        
        // Build report
        StringBuilder report = new StringBuilder();
        report.append("========================\n");
        report.append("Offense Type Distribution\n");
        report.append("========================\n");
        report.append(String.format("%-25s %-15s %-15s %-15s\n", 
                      "Offense Type", "Case Count", "Total Fines", "Average Fine"));
        report.append("-------------------------------------------------------------------------\n");
        
        int totalCases = 0;
        double totalAmount = 0.0;
        
        for (OffenseType type : OffenseType.values()) {
            int count = typeCounts.getOrDefault(type, 0);
            double amount = typeAmounts.getOrDefault(type, 0.0);
            double average = count > 0 ? amount / count : 0.0;
            
            report.append(String.format("%-25s %-15d $%-14.2f $%-14.2f\n",
                         type.name(), count, amount, average));
            
            totalCases += count;
            totalAmount += amount;
        }
        
        report.append("-------------------------------------------------------------------------\n");
        report.append(String.format("%-25s %-15d $%-14.2f $%-14.2f\n",
                     "TOTAL", totalCases, totalAmount, 
                     totalCases > 0 ? totalAmount / totalCases : 0.0));
        
        return report.toString();
    }
    
    /**
     * Generates a report on cases approaching statute of limitations
     * @param warningDays Days before expiration to include in warning list
     * @return The report as a formatted string
     */
    public String generateStatuteOfLimitationsReport(int warningDays) {
        List<RegulatoryCase> approachingCases = 
            caseManager.getCasesApproachingStatuteOfLimitations(warningDays);
        
        // Build report
        StringBuilder report = new StringBuilder();
        report.append("=======================================\n");
        report.append("Cases Approaching Statute of Limitations\n");
        report.append("=======================================\n");
        
        if (approachingCases.isEmpty()) {
            report.append("No cases approaching statute of limitations within " + warningDays + " days.");
            return report.toString();
        }
        
        report.append(String.format("%-10s %-20s %-15s %-20s\n", 
                     "Case ID", "Offense Type", "Offense Date", "Offender Name"));
        report.append("-----------------------------------------------------------------------\n");
        
        for (RegulatoryCase regulatoryCase : approachingCases) {
            String caseId = regulatoryCase.getCaseId().substring(0, 8) + "...";
            String offenderName = regulatoryCase.getOffense().getOffender().getFullName();
            
            report.append(String.format("%-10s %-20s %-15s %-20s\n",
                         caseId,
                         regulatoryCase.getOffense().getOffenseType(),
                         regulatoryCase.getOffense().getOffenseDate(),
                         offenderName));
        }
        
        return report.toString();
    }
}
