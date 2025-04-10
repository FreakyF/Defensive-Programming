package pl.kielce.tu.fudala.client.banking.model.account;

import pl.kielce.tu.fudala.client.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IBankAccount {
	void deposit(BigDecimal amount);

	void withdraw(BigDecimal amount);

	void transfer(IBankAccount targetAccount, BigDecimal amount);

	UUID getAccountId();

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getAccountNumber();

	BigDecimal getBalance();

	String getPesel();

	String getEmail();

	void setEmail(String email);

	List<Transaction> getTransactionHistory();
}
