import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ====================================================================
 * DOMAIN MODEL - Reservation
 * ====================================================================
 * Represents a guest's booking. Now includes the ability to store
 * an assigned Room ID upon confirmation.
 */
class Reservation {
    private String guestName;
    private String roomType;
    private String assignedRoomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.assignedRoomId = "Pending";
    }

    // Called when the booking is successfully allocated
    public void confirmBooking(String roomId) {
        this.assignedRoomId = roomId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getAssignedRoomId() { return assignedRoomId; }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + " | Room Type: " + roomType + " | Room ID: " + assignedRoomId + "]";
    }
}

/**
 * ====================================================================
 * HISTORY COMPONENT - BookingHistory
 * ====================================================================
 * Maintains a chronological record of confirmed reservations using a List.
 */
class BookingHistory {
    // List preserves insertion order, perfect for chronological audit trails
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        this.confirmedBookings = new ArrayList<>();
    }

    /**
     * Adds a successfully confirmed reservation to the history.
     */
    public void addRecord(Reservation reservation) {
        confirmedBookings.add(reservation);
        System.out.println("HISTORY LOGGED: Reservation for " + reservation.getGuestName() + " saved to history.");
    }

    /**
     * Retrieves the history. Returns a copy to prevent external modification
     * of the history state (Defensive Copying).
     */
    public List<Reservation> getHistory() {
        return new ArrayList<>(confirmedBookings);
    }
}

/**
 * ====================================================================
 * REPORTING SERVICE - BookingReportService
 * ====================================================================
 * Generates summaries and reports from stored booking data for Admin use.
 */
class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    /**
     * Displays a full, chronologically ordered audit trail of bookings.
     */
    public void displayFullHistory() {
        System.out.println("--------------------------------------------------");
        System.out.println("             FULL BOOKING HISTORY AUDIT           ");
        System.out.println("--------------------------------------------------");

        List<Reservation> history = bookingHistory.getHistory();

        if (history.isEmpty()) {
            System.out.println("No booking history found.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + ". " + history.get(i).toString());
            }
        }
        System.out.println("--------------------------------------------------\n");
    }

    /**
     * Generates a summary report of bookings grouped by room type.
     */
    public void displaySummaryReport() {
        System.out.println("--------------------------------------------------");
        System.out.println("             OPERATIONAL SUMMARY REPORT           ");
        System.out.println("--------------------------------------------------");

        List<Reservation> history = bookingHistory.getHistory();
        Map<String, Integer> roomTypeCounts = new HashMap<>();

        // Aggregate data
        for (Reservation res : history) {
            String type = res.getRoomType();
            roomTypeCounts.put(type, roomTypeCounts.getOrDefault(type, 0) + 1);
        }

        // Print Report
        System.out.println("Total Confirmed Bookings: " + history.size());
        for (Map.Entry<String, Integer> entry : roomTypeCounts.entrySet()) {
            System.out.println(" - " + entry.getKey() + "s Booked: " + entry.getValue());
        }
        System.out.println("--------------------------------------------------\n");
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 8: Booking History & Reporting
 *
 * Description:
 * Introduces historical tracking using a List to preserve insertion order.
 * Shows separation of data storage and reporting logic.
 *
 * @author Developer
 * @version 8.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // 1. Initialize System Components
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        // 2. Simulate Booking Confirmations (Output from Allocation phase)
        System.out.println("--- Processing Booking Confirmations ---");

        Reservation req1 = new Reservation("Alice Smith", "Double Room");
        req1.confirmBooking("DOU-101");
        history.addRecord(req1);

        Reservation req2 = new Reservation("Bob Jones", "Suite Room");
        req2.confirmBooking("SUI-101");
        history.addRecord(req2);

        Reservation req3 = new Reservation("Charlie Brown", "Single Room");
        req3.confirmBooking("SIN-101");
        history.addRecord(req3);

        Reservation req4 = new Reservation("Diana Prince", "Double Room");
        req4.confirmBooking("DOU-102");
        history.addRecord(req4);

        System.out.println();

        // 3. Admin requests reports (Separation of concerns)
        System.out.println("--- Admin requesting reports ---");
        reportService.displayFullHistory();
        reportService.displaySummaryReport();
    }
}