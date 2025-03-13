package pl.kielce.tu.fudala.lab01.banking.model.account;

import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface IBankAccount {
	void deposit(BigDecimal amount);

	void withdraw(BigDecimal amount);

	void transfer(IBankAccount targetAccount, BigDecimal amount);

	List<Transaction> getTransactionHistory();
}
