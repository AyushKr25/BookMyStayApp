import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

// ==========================================
// 1. DOMAIN MODEL (From Use Case 5)
// ==========================================
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
        return "Reservation Request [Guest: " + guestName + " | Room Type: " + requestedRoomType + "]";
    }
}

// ==========================================
// 2. INVENTORY COMPONENT (From Use Case 3)
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 15);
        inventory.put("Double Room", 10);
        inventory.put("Suite Room", 3); // Note: Only 3 suites available
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType) && newCount >= 0) {
            inventory.put(roomType, newCount);
        }
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

// ==========================================
// 3. QUEUE COMPONENT (From Use Case 5)
// ==========================================
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Received and queued: " + reservation.toString());
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    public Reservation poll() {
        return requestQueue.poll();
    }

    public void displayQueue() {
        System.out.println("--------------------------------------------------");
        System.out.println("             PENDING BOOKING QUEUE                ");
        System.out.println("--------------------------------------------------");
        if (requestQueue.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            int position = 1;
            for (Reservation req : requestQueue) {
                System.out.println(position + ". " + req.toString());
                position++;
            }
        }
        System.out.println("--------------------------------------------------\n");
    }
}

// ==========================================
// 4. BOOKING SERVICE (New for Use Case 6)
// ==========================================
/**
 * Processes queued booking requests and performs room allocation.
 * Uses a Set to ensure unique room assignments, preventing double-booking.
 */
class BookingService {
    private RoomInventory inventory;
    private BookingRequestQueue requestQueue;

    // Map to track allocated room IDs per room type using a Set for guaranteed uniqueness
    private Map<String, Set<String>> allocatedRooms;

    public BookingService(RoomInventory inventory, BookingRequestQueue requestQueue) {
        this.inventory = inventory;
        this.requestQueue = requestQueue;
        this.allocatedRooms = new HashMap<>();
    }

    /**
     * Processes requests from the queue in FIFO order.
     */
    public void processQueue() {
        System.out.println("--- Processing Booking Requests ---");

        while (!requestQueue.isEmpty()) {
            Reservation req = requestQueue.poll(); // Dequeue
            String type = req.getRequestedRoomType();

            // Check availability
            if (inventory.getAvailability(type) > 0) {
                String roomId = generateUniqueRoomId(type);

                // Record the room ID to prevent reuse
                allocatedRooms.putIfAbsent(type, new HashSet<>());
                allocatedRooms.get(type).add(roomId);

                // Decrement inventory immediately
                inventory.updateAvailability(type, inventory.getAvailability(type) - 1);

                System.out.println("SUCCESS: " + req.getGuestName() + " booked a " + type + ". Assigned Room ID: " + roomId);
            } else {
                System.out.println("FAILED: " + req.getGuestName() + "'s request for a " + type + " failed. No availability.");
            }
        }
        System.out.println();
    }

    /**
     * Helper method to generate a unique room ID based on type and current allocation count.
     */
    private String generateUniqueRoomId(String roomType) {
        Set<String> currentAllocations = allocatedRooms.getOrDefault(roomType, new HashSet<>());
        int nextNumber = 101 + currentAllocations.size();
        String prefix = roomType.substring(0, 3).toUpperCase(); // e.g., "SIN", "DOU", "SUI"
        return prefix + "-" + nextNumber;
    }

    /**
     * Displays all safely allocated rooms.
     */
    public void displayAllocations() {
        System.out.println("--------------------------------------------------");
        System.out.println("             CURRENT ROOM ALLOCATIONS             ");
        System.out.println("--------------------------------------------------");
        if (allocatedRooms.isEmpty()) {
            System.out.println("No rooms currently allocated.");
        } else {
            for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
                System.out.println(entry.getKey() + " Allocated IDs: " + entry.getValue());
            }
        }
        System.out.println("--------------------------------------------------\n");
    }
}

// ==========================================
// 5. MAIN CLASS - Application Entry
// ==========================================
/**
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * @author Developer
 * @version 6.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // Initialize Core Services
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queueManager = new BookingRequestQueue();

        // 1. Queue Booking Requests (Simulate incoming traffic)
        // Notice we are requesting 4 Suite Rooms, but inventory only has 3.
        System.out.println("--- Simulating Incoming Requests ---");
        queueManager.addRequest(new Reservation("Alice Smith", "Single Room"));
        queueManager.addRequest(new Reservation("Bob Jones", "Suite Room"));
        queueManager.addRequest(new Reservation("Charlie Brown", "Suite Room"));
        queueManager.addRequest(new Reservation("Diana Prince", "Suite Room"));
        queueManager.addRequest(new Reservation("Eve Davis", "Suite Room")); // Should fail
        System.out.println();

        // 2. Display Queue state before processing
        queueManager.displayQueue();

        // 3. Initialize Booking Service and Process Queue
        BookingService bookingService = new BookingService(inventory, queueManager);
        bookingService.processQueue();

        // 4. Display Final States to verify logic
        inventory.displayInventory();
        bookingService.displayAllocations();
    }
}