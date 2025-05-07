/**
 * Main application class to run the Regulatory Offense Management System
 */


import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Scanner;

@Slf4j
public class RegulationOffenseManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CaseManager caseManager = new CaseManager();

    public static void main(String[] args) {
        boolean running = true;
        
        log.info("Welcome to Regulatory Offense Management System");
        
        while (running) {
            printMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    recordNewOffense();
                    break;
                case 2:
                    viewCaseDetails();
                    break;
                case 3:
                    processPayment();
                    break;
                case 4:
                    recordAppeal();
                    break;
                case 5:
                    generateReports();
                    break;
                case 6:
                    running = false;
                    log.info("Exiting system. Goodbye!");
                    break;
                default:
                    log.info("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void printMenu() {
        log.info("\n========= MENU =========");
        log.info("1. Record New Offense");
        log.info("2. View Case Details");
        log.info("3. Process Payment");
        log.info("4. Record Appeal");
        log.info("5. Generate Reports");
        log.info("6. Exit");
        log.info("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void recordNewOffense() {
        log.info("\n=== Record New Regulatory Offense ===");
        
        // Collect offender details
        log.info("Enter offender's full name: ");
        String name = scanner.nextLine();
        
        log.info("Enter offender's address: ");
        String address = scanner.nextLine();
        
        log.info("Enter offender's date of birth (YYYY-MM-DD): ");
        LocalDate dob = LocalDate.parse(scanner.nextLine());
        
        Offender offender = new Offender(name, address, dob);
        
        // Collect offense details
        log.info("Enter offense location: ");
        String location = scanner.nextLine();
        
        log.info("Enter offense date (YYYY-MM-DD): ");
        LocalDate offenseDate = LocalDate.parse(scanner.nextLine());
        
        log.info("Available offense types:");
        for (OffenseType type : OffenseType.values()) {
            log.info(type.ordinal() + 1 + ". " + type.name() + " - " + type.getDescription());
        }
        
        log.info("Select offense type (number): ");
        int typeChoice = Integer.parseInt(scanner.nextLine());
        OffenseType offenseType = OffenseType.values()[typeChoice - 1];
        
        // Create the offense
        Offense offense = new Offense(offender, location, offenseDate, offenseType);
        
        // Record the case
        RegulatoryCase regulatoryCase = caseManager.createNewCase(offense);
        
        log.info("Case created successfully with ID: " + regulatoryCase.getCaseId());
        log.info("Fine amount: $" + regulatoryCase.getFine().getAmount());
        log.info("Payment deadline: " + regulatoryCase.getFine().getPaymentDeadline());
    }
    
    private static void viewCaseDetails() {
        log.info("\n=== View Case Details ===");
        log.info("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            log.info(regulatoryCase.toString());
            
            log.info("\nCase History:");
            for (CaseAction action : regulatoryCase.getCaseHistory()) {
                log.info("- " + action.getActionDate() + ": " + action.getActionType() + " - " + action.getDescription());
            }
        } else {
            log.info("Case not found.");
        }
    }
    
    private static void processPayment() {
        log.info("\n=== Process Payment ===");
        log.info("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            log.info("Case found. Fine amount: $" + regulatoryCase.getFine().getAmount());
            log.info("Enter payment amount: $");
            double amount = Double.parseDouble(scanner.nextLine());
            
            caseManager.recordPayment(regulatoryCase, amount);
            log.info("Payment processed successfully.");
        } else {
            log.info("Case not found.");
        }
    }
    
    private static void recordAppeal() {
        log.info("\n=== Record Appeal ===");
        log.info("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            log.info("Enter appeal reason: ");
            String reason = scanner.nextLine();
            
            caseManager.fileAppeal(regulatoryCase, reason);
            log.info("Appeal recorded successfully.");
        } else {
            log.info("Case not found.");
        }
    }
    
    private static void generateReports() {
        log.info("\n=== Generate Reports ===");
        log.info("Available reports:");
        log.info("1. Monthly Fine Statistics");
        log.info("2. Payment Status Report");
        log.info("3. Offense Type Distribution");
        log.info("Select report type: ");
        
        int reportType = Integer.parseInt(scanner.nextLine());
        ReportGenerator reportGenerator = new ReportGenerator(caseManager);
        
        switch (reportType) {
            case 1:
                log.info(reportGenerator.generateMonthlyFineStatistics());
                break;
            case 2:
                log.info(reportGenerator.generatePaymentStatusReport());
                break;
            case 3:
                log.info(reportGenerator.generateOffenseTypeDistribution());
                break;
            default:
                log.info("Invalid report type.");
        }
    }
}
