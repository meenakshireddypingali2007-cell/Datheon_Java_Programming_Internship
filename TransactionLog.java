import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** In-memory transaction history for one account. */
public class TransactionLog {
    private final List<Transaction> transactions = new ArrayList<>();

    public void record(String type, BigDecimal amount, BigDecimal balance, String status) {
        transactions.add(new Transaction(type, amount, balance, status));
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
