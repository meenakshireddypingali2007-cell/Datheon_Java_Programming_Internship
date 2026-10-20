# Core Banking Engine (Java)

A beginner-friendly, in-memory, console banking simulation built with Core Java and OOP. It has no external dependencies and is for learning only; it is not production banking software. Demo PINs are included for local practice only.

## Features

- Savings and checking accounts with shared account operations.
- `BigDecimal` money arithmetic, positive-amount validation, PIN checks, and overdraft enforcement.
- Savings simple interest credited for one year at a configurable annual percentage rate.
- Per-account transaction history with local timestamps, amounts, resulting balances, and success/rejection status. PINs are never recorded.
- Menu operations for viewing balance, deposits, withdrawals, interest, history, account switching, and exit.

## Requirements and run

JDK 11 or newer. From the project root:

```sh
javac -d out src/*.java
java -cp out Main
```

On Windows PowerShell, the same commands work from the project directory. No build tool or dependency download is needed. To run the included dependency-free checks, compile the test source too and launch it:

```sh
javac -d out src/*.java test/*.java
java -ea -cp out BankingSmokeTest
```

## Demo credentials

- Customer: Alex Morgan (`C1001`)
- Savings: `SAV-10001`, opening balance 1500.00, annual interest 4.00%, PIN `1234`
- Checking: `CHK-20001`, opening balance 500.00, overdraft limit 300.00, PIN `5678`

## Sample session

```text
Demo credentials: Savings PIN 1234; Checking PIN 5678.
Educational simulation only - not production banking software.
Welcome, Alex Morgan!
Savings account ****0001
1. View balance  2. Deposit  3. Withdraw  4. Apply savings interest  5. Transaction history  6. Switch account  7. Exit
Choose an option: 2
Enter 4-digit PIN: 1234
Enter amount: 100.00
Deposit successful. New balance: 1600.00
```

## Manual test guide

Start the app and use the menu; each account has its own history.

1. Savings deposit `100.00` with PIN `1234`: balance rises from 1500.00 to 1600.00; history shows `DEPOSIT`, timestamp, amount, resulting balance, and `SUCCESS`.
2. Savings withdrawal `200.00` with PIN `1234`: balance becomes 1400.00.
3. Try a protected operation with a wrong PIN: it is rejected and the balance stays unchanged. Rejected deposits/withdrawals are recorded with a rejection status.
4. Withdraw `1501.00` from the original savings balance using PIN `1234`: insufficient funds; balance is unchanged.
5. Switch to checking (option 6), then withdraw `700.00` using PIN `5678`: allowed, leaving `-200.00` within the `300.00` overdraft. Try another `101.00`: rejected because it exceeds the remaining available amount; balance stays `-200.00`.
6. Attempt `0`, a negative amount, or non-numeric input for a transaction. Zero/negative amounts are rejected without a balance change; non-numeric input is reprompted.
7. On savings, apply interest with PIN `1234`: 4% of 1500.00 credits 60.00 (balance 1560.00). History records the interest credit. Apply once per desired annual period; this demo does not track calendar accrual.
8. Inspect history after success and rejection: timestamps are generated with `LocalDateTime`; each record shows the resulting balance at that moment. Rejected attempts preserve the prior balance.

## OOP concepts

- **Abstraction:** `Account` defines common state and operations and leaves account type and withdrawal eligibility to subclasses.
- **Inheritance:** `SavingsAccount` and `CheckingAccount` extend `Account` with interest and overdraft rules.
- **Encapsulation:** state is private; callers use getters and validated methods rather than changing balances directly.
- **Polymorphism:** `Customer` stores `Account` references, while the ATM uses common account behavior and each subclass supplies its own withdrawal rule.
