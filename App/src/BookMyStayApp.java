/**
 * ====================================================================
 * MAIN CLASS - UseCase2HotelBookingApp
 * ====================================================================
 *
 * Use Case 2: Basic Room Types & Static Availability
 *
 * Description:
 * Builds upon Use Case 1 by introducing basic room types and
 * displaying their initial static availability.
 *
 * @author Developer
 * @version 2.0
 */
public class BookMyStayApp {

    /**
     * Enum representing the basic room types available in the hotel.
     */
    public enum RoomType {
        STANDARD,
        DELUXE,
        SUITE
    }

    // Static variables representing the initial availability of each room type
    private static int availableStandardRooms = 10;
    private static int availableDeluxeRooms = 5;
    private static int availableSuiteRooms = 2;

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Welcome message from Use Case 1
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // Displaying static availability for Use Case 2
        System.out.println("--- Current Room Availability ---");
        System.out.println(RoomType.STANDARD + " Rooms: " + availableStandardRooms);
        System.out.println(RoomType.DELUXE + " Rooms: " + availableDeluxeRooms);
        System.out.println(RoomType.SUITE + " Rooms: " + availableSuiteRooms);
    }
}