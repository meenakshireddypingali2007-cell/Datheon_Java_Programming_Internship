import java.math.BigDecimal;
import java.math.RoundingMode;

/** Shared account state and operations. Subclasses provide withdrawal rules. */
public abstract class Account {
    private final String accountNumber;
    private final String accountHolderName;
    private BigDecimal balance;
    private final String pin;
    private final TransactionLog transactionLog = new TransactionLog();

    protected Account(String accountNumber, String accountHolderName, BigDecimal openingBalance, String pin) {
        if (accountNumber == null || accountNumber.isBlank() || accountHolderName == null || accountHolderName.isBlank())
            throw new IllegalArgumentException("Account number and holder name are required.");
        if (openingBalance == null || openingBalance.signum() < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        if (pin == null || !pin.matches("\\d{4}")) throw new IllegalArgumentException("PIN must contain exactly four digits.");
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = money(openingBalance);
        this.pin = pin;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public synchronized BigDecimal getBalance() { return balance; }
    public abstract String getAccountType();
    protected abstract boolean canWithdraw(BigDecimal amount);

    public void verifyPIN(String suppliedPin) throws InvalidPINException {
        if (suppliedPin == null || !pin.equals(suppliedPin)) throw new InvalidPINException("Incorrect PIN.");
    }

    public synchronized void deposit(BigDecimal amount, String suppliedPin) throws InvalidPINException {
        try {
            verifyPIN(suppliedPin);
            requirePositive(amount);
            BigDecimal normalized = money(amount);
            balance = balance.add(normalized);
            transactionLog.record("DEPOSIT", normalized, balance, "SUCCESS");
        } catch (InvalidPINException | IllegalArgumentException ex) {
            transactionLog.record("DEPOSIT", safeAmount(amount), balance, "REJECTED: " + ex.getMessage());
            throw ex;
        }
    }

    public synchronized void withdraw(BigDecimal amount, String suppliedPin)
            throws InvalidPINException, InsufficientFundsException {
        try {
            verifyPIN(suppliedPin);
            requirePositive(amount);
            BigDecimal normalized = money(amount);
            if (!canWithdraw(normalized)) throw new InsufficientFundsException("Withdrawal exceeds the available funds and permitted overdraft.");
            balance = balance.subtract(normalized);
            transactionLog.record("WITHDRAWAL", normalized, balance, "SUCCESS");
        } catch (InvalidPINException | InsufficientFundsException | IllegalArgumentException ex) {
            transactionLog.record("WITHDRAWAL", safeAmount(amount), balance, "REJECTED: " + ex.getMessage());
            throw ex;
        }
    }

    protected synchronized void creditInterest(BigDecimal amount) {
        balance = balance.add(amount);
        transactionLog.record("INTEREST", amount, balance, "SUCCESS");
    }

    public TransactionLog getTransactionLog() { return transactionLog; }

    protected static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }
    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
    }
    private static BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO.setScale(2) : amount;
    }
}
