package pl.kielce.tu.fudala.lab01.banking.model.account;

import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IBankAccount {
    void deposit(BigDecimal amount);

    void withdraw(BigDecimal amount);

    void transfer(IBankAccount targetAccount, BigDecimal amount);

    UUID getAccountId();
    String getFirstName();
    String getLastName();
    String getAccountNumber();
    BigDecimal getBalance();
    String getPesel();
    String getEmail();

    List<Transaction> getTransactionHistory();

    void setFirstName(String firstName);

    void setLastName(String lastName);

    void setEmail(String email);
}
