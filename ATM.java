import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ATM {
    private final Customer customer;
    private final Scanner scanner;
    private int selectedAccountIndex;

    public ATM(Customer customer, Scanner scanner) {
        this.customer = customer;
        this.scanner = scanner;
        if (customer.getAccounts().isEmpty()) throw new IllegalArgumentException("ATM requires at least one account.");
    }

    public void start() {
        System.out.println("Educational simulation only - not production banking software.");
        System.out.println("Welcome, " + customer.getName() + "!");
        boolean running = true;
        while (running) {
            Account account = currentAccount();
            System.out.printf("\n%s account ****%s%n", account.getAccountType(),
                    account.getAccountNumber().substring(Math.max(0, account.getAccountNumber().length() - 4)));
            System.out.println("1. View balance  2. Deposit  3. Withdraw  4. Apply savings interest  5. Transaction history  6. Switch account  7. Exit");
            int choice = readChoice("Choose an option: ");
            try {
                switch (choice) {
                    case 1: viewBalance(account); break;
                    case 2: deposit(account); break;
                    case 3: withdraw(account); break;
                    case 4: applyInterest(account); break;
                    case 5: showHistory(account); break;
                    case 6: switchAccount(); break;
                    case 7: running = false; System.out.println("Thank you. Goodbye."); break;
                    default: System.out.println("Invalid choice. Enter a number from 1 to 7.");
                }
            } catch (InvalidPINException | InsufficientFundsException | IllegalArgumentException ex) {
                System.out.println("Transaction rejected: " + ex.getMessage());
            }
        }
    }

    private void viewBalance(Account account) throws InvalidPINException {
        account.verifyPIN(readPIN());
        System.out.println("Current balance: " + account.getBalance());
    }
    private void deposit(Account account) throws InvalidPINException {
        String pin = readPIN();
        BigDecimal amount = readAmount();
        account.deposit(amount, pin);
        System.out.println("Deposit successful. New balance: " + account.getBalance());
    }
    private void withdraw(Account account) throws InvalidPINException, InsufficientFundsException {
        String pin = readPIN();
        BigDecimal amount = readAmount();
        account.withdraw(amount, pin);
        System.out.println("Withdrawal successful. New balance: " + account.getBalance());
    }
    private void applyInterest(Account account) throws InvalidPINException {
        if (!(account instanceof SavingsAccount)) { System.out.println("Interest is available for savings accounts only."); return; }
        BigDecimal amount = ((SavingsAccount) account).calculateAndApplyAnnualInterest(readPIN());
        System.out.println("Annual interest credited: " + amount + ". New balance: " + account.getBalance());
    }
    private void showHistory(Account account) {
        List<Transaction> history = account.getTransactionLog().getTransactions();
        if (history.isEmpty()) { System.out.println("No transactions recorded yet."); return; }
        System.out.println("Transaction history:");
        for (Transaction transaction : history) System.out.println(transaction);
    }
    private void switchAccount() {
        List<Account> accounts = customer.getAccounts();
        if (accounts.size() < 2) { System.out.println("Only one account is available."); return; }
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            System.out.printf("%d. %s account ****%s%n", i + 1, a.getAccountType(),
                    a.getAccountNumber().substring(Math.max(0, a.getAccountNumber().length() - 4)));
        }
        int selected = readChoice("Select account: ");
        if (selected < 1 || selected > accounts.size()) System.out.println("Invalid account selection.");
        else { selectedAccountIndex = selected - 1; System.out.println("Switched account."); }
    }
    private Account currentAccount() { return customer.getAccounts().get(selectedAccountIndex); }
    private String readPIN() { System.out.print("Enter 4-digit PIN: "); return scanner.nextLine().trim(); }
    private BigDecimal readAmount() {
        while (true) {
            System.out.print("Enter amount: ");
            try { return new BigDecimal(scanner.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.println("Enter a valid numeric amount (for example, 25.50)."); }
        }
    }
    private int readChoice(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException ex) { return -1; }
    }
}
