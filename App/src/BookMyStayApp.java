/**
 * ====================================================================
 * INTERFACE ABSTRACTION - PaymentStrategy
 * ====================================================================
 * Defines a common contract for all payment methods.
 */
interface PaymentStrategy {
    void pay(double amount);
}

/**
 * ====================================================================
 * CONCRETE STRATEGIES - Implementations of PaymentStrategy
 * ====================================================================
 */
class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Processing Credit Card payment of $" + amount);
    }
}

class UPIPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Processing UPI payment of $" + amount);
    }
}

class CashPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Processing Cash payment of $" + amount);
    }
}

/**
 * ====================================================================
 * PROCESSOR COMPONENT - PaymentProcessor
 * ====================================================================
 * Processes payments without needing to know the specific payment type.
 * It relies entirely on the PaymentStrategy abstraction.
 */
class PaymentProcessor {
    public void processPayment(PaymentStrategy strategy, double amount) {
        // Polymorphism in action: calling the specific implementation's pay method
        strategy.pay(amount);
    }
}

/**
 * ====================================================================
 * MAIN CLASS - Application Entry
 * ====================================================================
 *
 * Use Case 10: Extensibility & Interface Abstraction
 *
 * Description:
 * Demonstrates the Strategy Pattern to decouple payment processing
 * from specific payment methods, ensuring easy extensibility.
 *
 * @author Developer
 * @version 10.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        // Application Startup
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.\n");

        // Initialize the Payment Processor
        PaymentProcessor processor = new PaymentProcessor();

        System.out.println("--- Processing Payments ---");

        // 1. Process a Credit Card Payment
        PaymentStrategy creditCard = new CreditCardPayment();
        processor.processPayment(creditCard, 150.0);

        // 2. Process a UPI Payment
        PaymentStrategy upi = new UPIPayment();
        processor.processPayment(upi, 50.0);

        // 3. Process a Cash Payment
        PaymentStrategy cash = new CashPayment();
        processor.processPayment(cash, 200.0);

        System.out.println("\nStatus: All payments processed successfully.");
    }
}