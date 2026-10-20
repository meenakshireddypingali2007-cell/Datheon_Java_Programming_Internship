import java.math.BigDecimal;

public class SavingsAccount extends Account {
    private final BigDecimal annualInterestRate;

    /** Rate is expressed as a percentage, e.g. 4.50 means 4.5% per year. */
    public SavingsAccount(String number, String holder, BigDecimal balance, String pin, BigDecimal annualInterestRate) {
        super(number, holder, balance, pin);
        if (annualInterestRate == null || annualInterestRate.signum() < 0)
            throw new IllegalArgumentException("Annual interest rate cannot be negative.");
        this.annualInterestRate = annualInterestRate;
    }

    @Override public String getAccountType() { return "Savings"; }
    @Override protected boolean canWithdraw(BigDecimal amount) { return getBalance().compareTo(amount) >= 0; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }

    /** Calculates and credits one year's simple interest on the current balance. */
    public synchronized BigDecimal calculateAndApplyAnnualInterest(String pin) throws InvalidPINException {
        try {
            verifyPIN(pin);
            BigDecimal interest = money(getBalance().multiply(annualInterestRate).movePointLeft(2));
            if (interest.signum() > 0) creditInterest(interest);
            return interest;
        } catch (InvalidPINException ex) {
            getTransactionLog().record("INTEREST", BigDecimal.ZERO.setScale(2), getBalance(), "REJECTED: " + ex.getMessage());
            throw ex;
        }
    }
}
