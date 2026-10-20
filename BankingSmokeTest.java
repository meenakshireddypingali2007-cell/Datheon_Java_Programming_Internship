import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dependency-free assertions for the core banking rules. Run with: java -ea -cp out BankingSmokeTest */
public class BankingSmokeTest {
    public static void main(String[] args) throws Exception {
        SavingsAccount savings = new SavingsAccount("S1", "Test Customer", new BigDecimal("100.00"), "1234", new BigDecimal("5.00"));
        savings.deposit(new BigDecimal("25.50"), "1234");
        equal("125.50", savings.getBalance(), "deposit");
        savings.withdraw(new BigDecimal("20.00"), "1234");
        equal("105.50", savings.getBalance(), "withdrawal");
        expectInvalidPin(() -> savings.deposit(new BigDecimal("10.00"), "0000"));
        expectIllegalArgument(() -> savings.deposit(BigDecimal.ZERO, "1234"));
        expectIllegalArgument(() -> savings.withdraw(new BigDecimal("-1.00"), "1234"));
        expectInsufficient(() -> savings.withdraw(new BigDecimal("105.51"), "1234"));
        equal("105.50", savings.getBalance(), "failed savings withdrawal leaves balance unchanged");
        equal("5.28", savings.calculateAndApplyAnnualInterest("1234"), "annual interest rounded to cents");
        equal("110.78", savings.getBalance(), "interest credit");

        CheckingAccount checking = new CheckingAccount("C1", "Test Customer", new BigDecimal("50.00"), "5678", new BigDecimal("100.00"));
        checking.withdraw(new BigDecimal("140.00"), "5678");
        equal("-90.00", checking.getBalance(), "withdrawal within overdraft");
        expectInsufficient(() -> checking.withdraw(new BigDecimal("10.01"), "5678"));
        equal("-90.00", checking.getBalance(), "failed overdraft leaves balance unchanged");

        Transaction first = savings.getTransactionLog().getTransactions().get(0);
        if (first.getTimestamp().isAfter(LocalDateTime.now())) throw new AssertionError("transaction timestamp is in the future");
        equal("125.50", first.getResultingBalance(), "transaction resulting balance");
        if (!"SUCCESS".equals(first.getStatus())) throw new AssertionError("successful deposit status missing");
        boolean sawRejection = savings.getTransactionLog().getTransactions().stream()
                .anyMatch(t -> t.getStatus().startsWith("REJECTED"));
        if (!sawRejection) throw new AssertionError("failed transaction was not logged");
        System.out.println("All banking smoke checks passed.");
    }

    private static void equal(String expected, BigDecimal actual, String label) {
        if (new BigDecimal(expected).compareTo(actual) != 0)
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
    }
    private static void expectInvalidPin(CheckedAction action) throws Exception {
        try { action.run(); throw new AssertionError("expected InvalidPINException"); }
        catch (InvalidPINException expected) { }
    }
    private static void expectInsufficient(CheckedAction action) throws Exception {
        try { action.run(); throw new AssertionError("expected InsufficientFundsException"); }
        catch (InsufficientFundsException expected) { }
    }
    private static void expectIllegalArgument(CheckedAction action) throws Exception {
        try { action.run(); throw new AssertionError("expected IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
    }
    @FunctionalInterface private interface CheckedAction { void run() throws Exception; }
}
