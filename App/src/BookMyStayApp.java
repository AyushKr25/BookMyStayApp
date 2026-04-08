import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ====================================================================
 * DOMAIN MODEL - PersistentRoomInventory
 * ====================================================================
 * Implements Serializable to allow the object's state to be saved
 * to and restored from a file.
 */
class PersistentRoomInventory implements Serializable {
    // Recommended for Serializable classes to ensure version compatibility
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory;

    public PersistentRoomInventory() {
        inventory = new HashMap<>();
        // Default fresh state
        inventory.put("Single Room", 15);
        inventory.put("Double Room", 10);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateInventory(String roomType, int count) {
        inventory.put(roomType, count);
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
 * SERVICE COMPONENT - PersistenceService
 * ====================================================================
 * Handles writing (Serialization) and reading (Deserialization)
 * of the system state to/from a durable file.
 */
class PersistenceService {
    private static final String FILE_NAME = "inventory_state.ser";

    /**
     * Serializes the inventory object and saves it to a file.
     */
    public static void saveState(PersistentRoomInventory inventory) {
        // Using Try-With-Resources to automatically close the streams
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            System.out.println("SUCCESS: System state successfully persisted to '" + FILE_NAME + "'.");
        } catch (IOException e) {
            System.out.println("ERROR: Failed to save system state. " + e.getMessage());
        }
    }

    /**
     * Attempts to load and deserialize the inventory object from a file.
     * Fails gracefully if the file does not exist.
     */
    public static PersistentRoomInventory loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (PersistentRoomInventory) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("WARNING: No previous state found. Starting fresh.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ERROR: Failed to restore system state. File may be corrupted. " + e.getMessage());
            return null;
        }
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 12: Data Persistence & System Recovery
 *
 * Description:
 * Demonstrates stateful system design by saving the application state
 * to disk upon shutdown and restoring it upon startup.
 *
 * @author Developer
 * @version 12.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System starting up...\n");

        // 1. Startup & Recovery Phase
        System.out.println("--- System Recovery Phase ---");
        System.out.println("Attempting to recover previous system state...");

        PersistentRoomInventory inventory = PersistenceService.loadState();

        if (inventory == null) {
            System.out.println("Initializing fresh system state...");
            inventory = new PersistentRoomInventory();
        } else {
            System.out.println("SUCCESS: System state restored successfully.");
        }

        System.out.println("\n--- Current System State ---");
        inventory.displayInventory();

        // 2. Simulate Business Activity
        System.out.println("--- Simulating Business Activity ---");
        int currentAvail = inventory.getAvailability("Single Room");
        if(currentAvail > 0) {
            System.out.println("Action: Guest booked 1 'Single Room'. Decrementing inventory...");
            inventory.updateInventory("Single Room", currentAvail - 1);
        } else {
            System.out.println("Action: 'Single Room' is out of stock. No changes made.");
        }

        System.out.println("\n--- Updated System State ---");
        inventory.displayInventory();

        // 3. Shutdown & Persistence Phase
        System.out.println("--- System Shutdown Phase ---");
        System.out.println("Saving system state before shutdown...");
        PersistenceService.saveState(inventory);

        System.out.println("\nSystem gracefully shut down.");
    }
}