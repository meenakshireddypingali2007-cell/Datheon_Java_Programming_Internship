import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A PIN-free record of an attempted account transaction. */
public class Transaction {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime timestamp;
    private final String type;
    private final BigDecimal amount;
    private final BigDecimal resultingBalance;
    private final String status;

    public Transaction(String type, BigDecimal amount, BigDecimal resultingBalance, String status) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.resultingBalance = resultingBalance;
        this.status = status;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getResultingBalance() { return resultingBalance; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%s | %-18s | Amount: %s | Balance: %s | %s",
                timestamp.format(FORMAT), type, amount.toPlainString(),
                resultingBalance.toPlainString(), status);
    }
}
