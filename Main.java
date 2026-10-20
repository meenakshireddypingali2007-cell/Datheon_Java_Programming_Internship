import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer("C1001", "Alex Morgan");
        customer.addAccount(new SavingsAccount("SAV-10001", customer.getName(), new BigDecimal("1500.00"), "1234", new BigDecimal("4.00")));

        System.out.println("Demo credentials: Savings PIN 1234; Checking PIN 5678.");
        new ATM(customer, new Scanner(System.in)).start();
    }
}
