import java.util.HashMap;
import java.util.Map;

/**
 * ====================================================================
 * CUSTOM EXCEPTION - InvalidBookingException
 * ====================================================================
 * Domain-specific exception used to represent invalid booking scenarios.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * ====================================================================
 * DOMAIN MODEL - Reservation
 * ====================================================================
 */
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }

    @Override
    public String toString() {
        return "Reservation Request [Guest: '" + guestName + "' | Room Type: '" + requestedRoomType + "']";
    }
}

/**
 * ====================================================================
 * INVENTORY COMPONENT - RoomInventory
 * ====================================================================
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 15);
        inventory.put("Double Room", 10);
        // Setting Suite Room to 0 to simulate an out-of-stock scenario for validation testing
        inventory.put("Suite Room", 0);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        System.out.println("--------------------------------------------------");
        System.out.println("            CURRENT ROOM INVENTORY                ");
        System.out.println("--------------------------------------------------");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
        System.out.println("--------------------------------------------------\n");
    }
}

/**
 * ====================================================================
 * VALIDATION COMPONENT - InvalidBookingValidator
 * ====================================================================
 * Validates input and system state before processing requests.
 */
class InvalidBookingValidator {
    /**
     * Checks if a reservation is structurally and contextually valid.
     * Throws InvalidBookingException at the first sign of invalid data (Fail-Fast).
     */
    public static void validate(Reservation reservation, RoomInventory inventory) throws InvalidBookingException {
        // 1. Guard against empty or null guest names
        if (reservation.getGuestName() == null || reservation.getGuestName().trim().isEmpty()) {
            throw new InvalidBookingException("Validation Failed: Guest name cannot be empty.");
        }

        // 2. Guard against empty or null room types
        if (reservation.getRequestedRoomType() == null || reservation.getRequestedRoomType().trim().isEmpty()) {
            throw new InvalidBookingException("Validation Failed: Room type cannot be empty.");
        }

        // 3. Guard against non-existent room types (Case-sensitive check)
        if (!inventory.isValidRoomType(reservation.getRequestedRoomType())) {
            throw new InvalidBookingException("Validation Failed: Room type '" + reservation.getRequestedRoomType() + "' is invalid or unrecognized.");
        }

        // 4. Guard against overbooking (Check availability state)
        if (inventory.getAvailability(reservation.getRequestedRoomType()) <= 0) {
            throw new InvalidBookingException("Validation Failed: No availability for '" + reservation.getRequestedRoomType() + "'.");
        }
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 9: Error Handling & Validation
 *
 * Description:
 * Demonstrates fail-fast design, defensive programming, and the use of
 * custom exceptions to guard system state.
 *
 * @author Developer
 * @version 9.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        RoomInventory inventory = new RoomInventory();

        System.out.println("--- Current System State ---");
        inventory.displayInventory();

        System.out.println("--- Processing Booking Requests with Validation ---\n");

        // Prepare an array of test cases representing different validity states
        Reservation[] testCases = {
                new Reservation("Alice Smith", "Double Room"),           // Expected: SUCCESS
                new Reservation("", "Single Room"),                      // Expected: FAIL (Missing Name)
                new Reservation("Charlie Brown", "Penthouse Suite"),     // Expected: FAIL (Invalid Type)
                new Reservation("Diana Prince", "Suite Room")            // Expected: FAIL (0 Availability)
        };

        // Process requests using structured Try-Catch blocks
        for (int i = 0; i < testCases.length; i++) {
            Reservation req = testCases[i];
            System.out.println("Processing Request " + (i + 1) + ": " + req.toString());

            try {
                // Attempt validation
                InvalidBookingValidator.validate(req, inventory);

                // If no exception is thrown, the flow continues here
                System.out.println("SUCCESS: Validation passed. Proceeding with booking...\n");

            } catch (InvalidBookingException e) {
                // Catch our specific domain exception and handle it gracefully
                System.out.println("ERROR: " + e.getMessage() + "\n");
            }
        }

        System.out.println("Status: System gracefully handled all requests and remains stable.");
    }
}