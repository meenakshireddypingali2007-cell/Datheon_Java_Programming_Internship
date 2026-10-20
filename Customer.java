import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private final String customerId;
    private final String name;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String name) {
        if (customerId == null || customerId.isBlank() || name == null || name.isBlank())
            throw new IllegalArgumentException("Customer ID and name are required.");
        this.customerId = customerId;
        this.name = name;
    }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public void addAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("Account cannot be null.");
        accounts.add(account);
    }
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }
}
