import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
}

/**
 * ====================================================================
 * THREAD-SAFE COMPONENT - SharedRoomInventory
 * ====================================================================
 * Manages inventory. Methods that read and modify state are synchronized
 * to prevent race conditions during concurrent access.
 */
class SharedRoomInventory {
    private Map<String, Integer> inventory;

    public SharedRoomInventory() {
        inventory = new HashMap<>();
        // Intentionally setting low availability to force contention among threads
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    /**
     * CRITICAL SECTION: Synchronized method ensures only one thread can
     * check availability and update inventory at any given time.
     */
    public synchronized boolean bookRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            // Simulate processing delay to expose race conditions (if it wasn't synchronized)
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Deduct from inventory
            inventory.put(roomType, available - 1);
            return true;
        }
        return false; // No availability
    }

    public synchronized void displayInventory() {
        System.out.println("--------------------------------------------------");
        System.out.println("            FINAL ROOM INVENTORY STATE            ");
        System.out.println("--------------------------------------------------");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
        System.out.println("--------------------------------------------------\n");
    }
}

/**
 * ====================================================================
 * THREAD-SAFE COMPONENT - SharedBookingQueue
 * ====================================================================
 * A queue that multiple threads can safely read from and write to.
 */
class SharedBookingQueue {
    private Queue<Reservation> requestQueue;

    public SharedBookingQueue() {
        this.requestQueue = new LinkedList<>();
    }

    public synchronized void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    /**
     * Safely retrieves and removes the next request from the queue.
     */
    public synchronized Reservation pollRequest() {
        return requestQueue.poll();
    }
}

/**
 * ====================================================================
 * WORKER THREAD - ConcurrentBookingProcessor
 * ====================================================================
 * Represents a worker thread that processes requests from the shared queue.
 */
class ConcurrentBookingProcessor implements Runnable {
    private SharedBookingQueue queue;
    private SharedRoomInventory inventory;
    private String processorName;

    public ConcurrentBookingProcessor(SharedBookingQueue queue, SharedRoomInventory inventory, String processorName) {
        this.queue = queue;
        this.inventory = inventory;
        this.processorName = processorName;
    }

    @Override
    public void run() {
        while (true) {
            // 1. Safely dequeue a request
            Reservation req = queue.pollRequest();

            // If queue is empty, the thread's work is done
            if (req == null) {
                break;
            }

            System.out.println(processorName + " is processing request for: " + req.getGuestName());

            // 2. Attempt to book the room safely
            boolean success = inventory.bookRoom(req.getRequestedRoomType());

            if (success) {
                System.out.println(" >>> SUCCESS: " + processorName + " booked a " + req.getRequestedRoomType() + " for " + req.getGuestName());
            } else {
                System.out.println(" >>> FAILED: " + processorName + " could not book a " + req.getRequestedRoomType() + " for " + req.getGuestName() + " (Out of Stock)");
            }

            // Yield to encourage thread context switching for the simulation
            Thread.yield();
        }
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 11: Concurrent Booking Simulation (Thread Safety)
 *
 * @author Developer
 * @version 11.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully. Starting Concurrent Simulation...\n");

        // 1. Initialize Shared Resources
        SharedRoomInventory sharedInventory = new SharedRoomInventory();
        SharedBookingQueue sharedQueue = new SharedBookingQueue();

        // 2. Load the Queue with more requests than available rooms
        // We only have 2 Single Rooms and 1 Double Room available.
        sharedQueue.addRequest(new Reservation("Alice", "Single Room"));
        sharedQueue.addRequest(new Reservation("Bob", "Single Room"));
        sharedQueue.addRequest(new Reservation("Charlie", "Single Room")); // Should fail
        sharedQueue.addRequest(new Reservation("Diana", "Double Room"));
        sharedQueue.addRequest(new Reservation("Eve", "Double Room"));     // Should fail

        // 3. Create Multiple Booking Processor Threads (Simulating concurrent users/servers)
        Thread t1 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory, "Worker-Thread-1"));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory, "Worker-Thread-2"));
        Thread t3 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory, "Worker-Thread-3"));

        // 4. Start concurrent execution
        System.out.println("--- Processing Bookings Concurrently ---");
        t1.start();
        t2.start();
        t3.start();

        // 5. Wait for all threads to finish processing before showing final state
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        System.out.println("\nAll worker threads have finished processing.");

        // 6. Display final inventory to prove no double-booking occurred (Inventory shouldn't drop below 0)
        sharedInventory.displayInventory();
    }
}