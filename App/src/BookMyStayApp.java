import java.util.HashMap;
import java.util.Map;

/**
 * The RoomInventory class manages room availability across the system.
 * It uses a centralized HashMap to ensure a single source of truth,
 * eliminating the scattered static variables from previous versions.
 */
class RoomInventory {
    // HashMap to store room types as keys and available counts as values
    private Map<String, Integer> inventory;

    /**
     * Constructor initializes the inventory component and registers
     * room types with their initial available counts.
     */
    public RoomInventory() {
        inventory = new HashMap<>();

        // Registering initial room types and their availability
        inventory.put("Single", 15);
        inventory.put("Double", 10);
        inventory.put("Suite", 3);
    }

    /**
     * Retrieves the current availability for a specific room type.
     * Demonstrates O(1) Lookup capability of HashMap.
     * * @param roomType The type of the room.
     * @return The number of available rooms, or 0 if type not found.
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Provides a controlled method to update room availability.
     * * @param roomType The type of the room.
     * @param newCount The new availability count.
     */
    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            if (newCount >= 0) {
                inventory.put(roomType, newCount);
            } else {
                System.out.println("Error: Inventory cannot be less than 0.");
            }
        } else {
            System.out.println("Error: Room type '" + roomType + "' does not exist.");
        }
    }

    /**
     * Displays the current state of the inventory.
     */
    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Room: " + entry.getValue() + " available");
        }
    }
}

/**
 * ====================================================================
 * MAIN CLASS - UseCase3InventorySetup
 * ====================================================================
 *
 * @version 3.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // 1. Initialize the inventory component
        RoomInventory inventory = new RoomInventory();

        // 2. Display the initial inventory state
        System.out.println("--- Initial Inventory State ---");
        inventory.displayInventory();

        // 3. Retrieve availability from centralized HashMap
        String typeToCheck = "Double";
        System.out.println("\nChecking availability for " + typeToCheck + " Room: "
                + inventory.getAvailability(typeToCheck));

        // 4. Update availability through controlled methods
        System.out.println("Booking 2 " + typeToCheck + " Rooms...");
        int currentDouble = inventory.getAvailability(typeToCheck);
        inventory.updateAvailability(typeToCheck, currentDouble - 2);

        // 5. Display the updated inventory state
        System.out.println("\n--- Updated Inventory State ---");
        inventory.displayInventory();
    }
}