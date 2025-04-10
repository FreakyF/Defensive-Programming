package pl.kielce.tu.fudala.client.banking.persistence;

import pl.kielce.tu.fudala.client.banking.model.account.IBankAccount;
import pl.kielce.tu.fudala.client.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface IBankAccountRepository {

	IBankAccount getAccountByPesel(String pesel);

	void addAccount(IBankAccount account);

	void updateAccount(IBankAccount account);

	void createAccount(IBankAccount account);

	void deposit(String pesel, BigDecimal amount);

	void withdraw(String pesel, BigDecimal amount);

	void transfer(String fromPesel, String toPesel, BigDecimal amount);

	List<Transaction> getHistory(String pesel);

	void removeAccount(String pesel);

	IBankAccount getAccountData(String pesel);

	void updateAccountData(IBankAccount account);
}
