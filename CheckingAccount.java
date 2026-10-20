import java.math.BigDecimal;

public class CheckingAccount extends Account {
    private final BigDecimal overdraftLimit;

    public CheckingAccount(String number, String holder, BigDecimal balance, String pin, BigDecimal overdraftLimit) {
        super(number, holder, balance, pin);
        if (overdraftLimit == null || overdraftLimit.signum() < 0)
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        this.overdraftLimit = overdraftLimit.setScale(2, java.math.RoundingMode.HALF_EVEN);
    }

    @Override public String getAccountType() { return "Checking"; }
    @Override protected boolean canWithdraw(BigDecimal amount) {
        return getBalance().add(overdraftLimit).compareTo(amount) >= 0;
    }
    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
}
