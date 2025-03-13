package pl.kielce.tu.fudala.lab01.banking.persistence;

import pl.kielce.tu.fudala.lab01.banking.model.account.IBankAccount;

public interface IBankAccountRepository {

    IBankAccount getAccountByPesel(String pesel);

    void addAccount(IBankAccount account);

    void updateAccount(IBankAccount account);
}
