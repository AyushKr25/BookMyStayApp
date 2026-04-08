import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ====================================================================
 * DOMAIN MODEL - AddOnService
 * ====================================================================
 * Represents an individual optional offering that a guest can select.
 */
class AddOnService {
    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + price + ")";
    }
}

/**
 * ====================================================================
 * SERVICE COMPONENT - AddOnServiceManager
 * ====================================================================
 * Manages the association between confirmed reservations (Room IDs)
 * and their selected add-on services using a Map and List combination.
 */
class AddOnServiceManager {
    // Map linking a Reservation/Room ID to a List of selected AddOnServices
    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        this.reservationServicesMap = new HashMap<>();
    }

    /**
     * Attaches a selected service to a specific reservation ID.
     * @param reservationId The unique ID of the confirmed booking.
     * @param service The add-on service selected by the guest.
     */
    public void addServiceToReservation(String reservationId, AddOnService service) {
        // If the reservation ID isn't in the map yet, add it with a new empty ArrayList
        reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());

        // Add the service to the reservation's list
        reservationServicesMap.get(reservationId).add(service);

        System.out.println("SUCCESS: Added '" + service.getServiceName() + "' to Reservation ID: " + reservationId);
    }

    /**
     * Calculates the total additional cost of all services attached to a reservation.
     * @param reservationId The unique ID of the confirmed booking.
     * @return The total cost of the add-on services.
     */
    public double calculateTotalAddOnCost(String reservationId) {
        List<AddOnService> services = reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
        double totalCost = 0.0;

        for (AddOnService service : services) {
            totalCost += service.getPrice();
        }

        return totalCost;
    }

    /**
     * Displays a summary of selected services and their total cost for a given reservation.
     * @param reservationId The unique ID of the confirmed booking.
     */
    public void displayServicesSummary(String reservationId) {
        System.out.println("--------------------------------------------------");
        System.out.println("        ADD-ON SERVICES FOR: " + reservationId);
        System.out.println("--------------------------------------------------");

        List<AddOnService> services = reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());

        if (services.isEmpty()) {
            System.out.println("No optional services selected.");
        } else {
            for (AddOnService service : services) {
                System.out.println(" - " + service.toString());
            }
            System.out.println("\nTotal Add-On Cost: $" + calculateTotalAddOnCost(reservationId));
        }
        System.out.println("--------------------------------------------------\n");
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 7: Add-On Service Selection
 *
 * Description:
 * Extends the booking model to support optional services without
 * modifying core booking or allocation state.
 *
 * @author Developer
 * @version 7.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // 1. Initialize the Add-On Service Manager
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // 2. Define some available Add-On Services
        AddOnService breakfast = new AddOnService("Buffet Breakfast", 25.00);
        AddOnService airportTransfer = new AddOnService("Airport Transfer", 50.00);
        AddOnService spaAccess = new AddOnService("Spa Access", 80.00);
        AddOnService lateCheckout = new AddOnService("Late Check-out", 40.00);

        // 3. Mock Confirmed Reservation IDs (Simulating Output from Use Case 6)
        String guest1ReservationId = "DOU-101"; // E.g., a Double Room booking
        String guest2ReservationId = "SUI-102"; // E.g., a Suite Room booking

        // 4. Simulate Guests selecting optional services
        System.out.println("--- Processing Add-On Service Selections ---");

        // Guest 1 selects Breakfast and a Late Check-out
        addOnManager.addServiceToReservation(guest1ReservationId, breakfast);
        addOnManager.addServiceToReservation(guest1ReservationId, lateCheckout);

        // Guest 2 selects Airport Transfer, Spa Access, and Breakfast
        addOnManager.addServiceToReservation(guest2ReservationId, airportTransfer);
        addOnManager.addServiceToReservation(guest2ReservationId, spaAccess);
        addOnManager.addServiceToReservation(guest2ReservationId, breakfast);
        System.out.println();

        // 5. Display the mapped services and total aggregated costs
        addOnManager.displayServicesSummary(guest1ReservationId);
        addOnManager.displayServicesSummary(guest2ReservationId);
    }
}