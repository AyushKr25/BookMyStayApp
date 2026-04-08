import java.util.HashMap;
import java.util.Map;

// ==========================================
// DOMAIN MODELS (From Use Case 2)
// ==========================================
abstract class Room {
    private String roomType;
    private int numberOfBeds;
    private double size;
    private double price;

    public Room(String roomType, int numberOfBeds, double size, double price) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() { return roomType; }
    public int getNumberOfBeds() { return numberOfBeds; }
    public double getSize() { return size; }
    public double getPrice() { return price; }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 250.0, 100.00); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 400.0, 150.00); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 2, 700.0, 350.00); }
}

// ==========================================
// INVENTORY COMPONENT (From Use Case 3)
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 15);
        inventory.put("Double Room", 10);
        inventory.put("Suite Room", 3);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType) && newCount >= 0) {
            inventory.put(roomType, newCount);
        }
    }
}

// ==========================================
// SEARCH COMPONENT (New for Use Case 4)
// ==========================================
/**
 * SearchService handles read-only access to inventory and room information.
 * It strictly prevents modification to the system state during searches.
 */
class SearchService {
    private RoomInventory inventory;
    private Room[] allRooms;

    /**
     * Constructor links the inventory state and room domain objects.
     */
    public SearchService(RoomInventory inventory, Room[] allRooms) {
        this.inventory = inventory;
        this.allRooms = allRooms;
    }

    /**
     * Displays available room types and their details.
     * Filters out unavailable rooms (availability == 0).
     */
    public void displayAvailableRooms() {
        System.out.println("--------------------------------------------------");
        System.out.println("             AVAILABLE ROOM SEARCH                ");
        System.out.println("--------------------------------------------------");

        boolean roomsFound = false;

        for (Room room : allRooms) {
            int availability = inventory.getAvailability(room.getRoomType());

            // Defensive check: Only show rooms with availability > 0
            if (availability > 0) {
                System.out.println(room.getRoomType() + " - " + availability + " available");
                System.out.println("   Beds: " + room.getNumberOfBeds() +
                        " | Size: " + room.getSize() + " sq.ft" +
                        " | Price: $" + room.getPrice() + "/night\n");
                roomsFound = true;
            }
        }

        if (!roomsFound) {
            System.out.println("We're sorry, no rooms are currently available.");
        }

        System.out.println("--------------------------------------------------\n");
    }
}

// ==========================================
// MAIN CLASS - Application Entry
// ==========================================
/**
 * Use Case 4: Room Search & Availability Check
 * * Demonstrates safe data access and clear separation of responsibilities
 * without modifying system state.
 *
 * @author Developer
 * @version 4.0
 */
public class BookMyStayApp {
    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // 1. Initialize core system components
        RoomInventory inventory = new RoomInventory();
        Room[] rooms = new Room[] {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // 2. Initialize the Search Service with read-only access
        SearchService searchService = new SearchService(inventory, rooms);

        // 3. Guest initiates a search request
        System.out.println("Guest initiating room search (Initial State)...");
        searchService.displayAvailableRooms();

        // 4. Simulate an external system change (e.g., Suite rooms get fully booked)
        System.out.println("Simulating system update: All Suite Rooms have been booked.\n");
        inventory.updateAvailability("Suite Room", 0);

        // 5. Guest initiates another search request to see defensive filtering
        System.out.println("Guest initiating another room search...");
        searchService.displayAvailableRooms();
    }
}