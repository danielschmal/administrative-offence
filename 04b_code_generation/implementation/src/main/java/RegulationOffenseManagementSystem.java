/**
 * Main application class to run the Regulatory Offense Management System
 */


import java.time.LocalDate;
import java.util.Scanner;

public class RegulationOffenseManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CaseManager caseManager = new CaseManager();

    public static void main(String[] args) {
        boolean running = true;
        
        System.out.println("Welcome to Regulatory Offense Management System");
        
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
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void printMenu() {
        System.out.println("\n========= MENU =========");
        System.out.println("1. Record New Offense");
        System.out.println("2. View Case Details");
        System.out.println("3. Process Payment");
        System.out.println("4. Record Appeal");
        System.out.println("5. Generate Reports");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void recordNewOffense() {
        System.out.println("\n=== Record New Regulatory Offense ===");
        
        // Collect offender details
        System.out.print("Enter offender's full name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter offender's address: ");
        String address = scanner.nextLine();
        
        System.out.print("Enter offender's date of birth (YYYY-MM-DD): ");
        LocalDate dob = LocalDate.parse(scanner.nextLine());
        
        Offender offender = new Offender(name, address, dob);
        
        // Collect offense details
        System.out.print("Enter offense location: ");
        String location = scanner.nextLine();
        
        System.out.print("Enter offense date (YYYY-MM-DD): ");
        LocalDate offenseDate = LocalDate.parse(scanner.nextLine());
        
        System.out.println("Available offense types:");
        for (OffenseType type : OffenseType.values()) {
            System.out.println(type.ordinal() + 1 + ". " + type.name() + " - " + type.getDescription());
        }
        
        System.out.print("Select offense type (number): ");
        int typeChoice = Integer.parseInt(scanner.nextLine());
        OffenseType offenseType = OffenseType.values()[typeChoice - 1];
        
        // Create the offense
        Offense offense = new Offense(offender, location, offenseDate, offenseType);
        
        // Record the case
        RegulatoryCase regulatoryCase = caseManager.createNewCase(offense);
        
        System.out.println("Case created successfully with ID: " + regulatoryCase.getCaseId());
        System.out.println("Fine amount: $" + regulatoryCase.getFine().getAmount());
        System.out.println("Payment deadline: " + regulatoryCase.getFine().getPaymentDeadline());
    }
    
    private static void viewCaseDetails() {
        System.out.println("\n=== View Case Details ===");
        System.out.print("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            System.out.println(regulatoryCase.toString());
            
            System.out.println("\nCase History:");
            for (CaseAction action : regulatoryCase.getCaseHistory()) {
                System.out.println("- " + action.getActionDate() + ": " + action.getActionType() + " - " + action.getDescription());
            }
        } else {
            System.out.println("Case not found.");
        }
    }
    
    private static void processPayment() {
        System.out.println("\n=== Process Payment ===");
        System.out.print("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            System.out.println("Case found. Fine amount: $" + regulatoryCase.getFine().getAmount());
            System.out.print("Enter payment amount: $");
            double amount = Double.parseDouble(scanner.nextLine());
            
            caseManager.recordPayment(regulatoryCase, amount);
            System.out.println("Payment processed successfully.");
        } else {
            System.out.println("Case not found.");
        }
    }
    
    private static void recordAppeal() {
        System.out.println("\n=== Record Appeal ===");
        System.out.print("Enter case ID: ");
        String caseId = scanner.nextLine();
        
        RegulatoryCase regulatoryCase = caseManager.getCaseById(caseId);
        
        if (regulatoryCase != null) {
            System.out.print("Enter appeal reason: ");
            String reason = scanner.nextLine();
            
            caseManager.fileAppeal(regulatoryCase, reason);
            System.out.println("Appeal recorded successfully.");
        } else {
            System.out.println("Case not found.");
        }
    }
    
    private static void generateReports() {
        System.out.println("\n=== Generate Reports ===");
        System.out.println("Available reports:");
        System.out.println("1. Monthly Fine Statistics");
        System.out.println("2. Payment Status Report");
        System.out.println("3. Offense Type Distribution");
        System.out.print("Select report type: ");
        
        int reportType = Integer.parseInt(scanner.nextLine());
        ReportGenerator reportGenerator = new ReportGenerator(caseManager);
        
        switch (reportType) {
            case 1:
                System.out.println(reportGenerator.generateMonthlyFineStatistics());
                break;
            case 2:
                System.out.println(reportGenerator.generatePaymentStatusReport());
                break;
            case 3:
                System.out.println(reportGenerator.generateOffenseTypeDistribution());
                break;
            default:
                System.out.println("Invalid report type.");
        }
    }
}
