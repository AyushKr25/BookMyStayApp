import java.util.LinkedList;
import java.util.Queue;

/**
 * ====================================================================
 * DOMAIN MODEL - Reservation
 * ====================================================================
 * Represents a guest's intent to book a room.
 */
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRequestedRoomType() {
        return requestedRoomType;
    }

    @Override
    public String toString() {
        return "Reservation Request [Guest: " + guestName + " | Room Type: " + requestedRoomType + "]";
    }
}

/**
 * ====================================================================
 * QUEUE COMPONENT - BookingRequestQueue
 * ====================================================================
 * Manages and orders incoming booking requests using a FIFO Queue.
 */
class BookingRequestQueue {
    // Queue data structure to enforce First-Come-First-Served ordering
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        // LinkedList is a standard implementation of the Queue interface in Java
        this.requestQueue = new LinkedList<>();
    }

    /**
     * Accepts a booking request and adds it to the end of the queue.
     * * @param reservation The reservation request to add.
     */
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Received and queued: " + reservation.toString());
    }

    /**
     * Displays the current state of the queue to verify FIFO order.
     */
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

/**
 * ====================================================================
 * MAIN CLASS - UseCase5BookingRequestQueue
 * ====================================================================
 *
 * Use Case 5: Booking Request (First-Come-First-Served)
 *
 * Description:
 * Handles multiple booking requests fairly by introducing a FIFO queue.
 * This decouples request intake from inventory allocation.
 *
 * @author Developer
 * @version 5.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // 1. Initialize the booking request queue
        BookingRequestQueue queueManager = new BookingRequestQueue();

        // 2. Simulate guests submitting booking requests (Simultaneous Arrival)
        System.out.println("--- Simulating Incoming Requests ---");
        Reservation req1 = new Reservation("Alice Smith", "Double Room");
        Reservation req2 = new Reservation("Bob Jones", "Suite Room");
        Reservation req3 = new Reservation("Charlie Brown", "Single Room");
        Reservation req4 = new Reservation("Diana Prince", "Double Room");

        // 3. Add requests to the queue (Preserving order)
        queueManager.addRequest(req1);
        queueManager.addRequest(req2);
        queueManager.addRequest(req3);
        queueManager.addRequest(req4);
        System.out.println();

        // 4. Display the queue to prove FIFO structure
        queueManager.displayQueue();

        // 5. Confirm no inventory changes happened
        System.out.println("Status: Requests queued successfully. Waiting for allocation processing.");
        System.out.println("Note: Inventory remains unchanged at this stage.");
    }
}