package pl.kielce.tu.fudala.lab01.banking.model.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.OperationType;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;
import pl.kielce.tu.fudala.lab01.banking.validator.email.ValidEmail;
import pl.kielce.tu.fudala.lab01.banking.validator.iban.ValidIban;
import pl.kielce.tu.fudala.lab01.banking.validator.pesel.ValidPesel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount implements IBankAccount {
    private final ReentrantLock lock = new ReentrantLock();
    @NotNull
    private UUID accountId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @ValidIban
    @NotBlank
    private String accountNumber;
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> transactionHistory = new ArrayList<>();
    @ValidPesel
    @NotBlank
    private String pesel;
    @ValidEmail
    @NotBlank
    private String email;

    @Override
    public void deposit(BigDecimal amount) {
        validateAmount(amount, OperationType.DEPOSIT);
        lock.lock();
        try {
            processDeposit(amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(BigDecimal amount) {
        validateAmount(amount, OperationType.WITHDRAWAL);
        lock.lock();
        try {
            processWithdrawal(amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void transfer(IBankAccount targetAccount, BigDecimal amount) {
        validateTargetAccount(targetAccount);
        validateAmount(amount, OperationType.TRANSFER);

        BankAccount target = castToBankAccount(targetAccount);

        BankAccount firstLockAccount = this.accountNumber.compareTo(target.accountNumber) < 0 ? this : target;
        BankAccount secondLockAccount = this.accountNumber.compareTo(target.accountNumber) < 0 ? target : this;

        try {
            lockBoth(firstLockAccount, secondLockAccount);
            if (this.balance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient funds for transfer.");
            }
            this.balance = this.balance.subtract(amount);
            addTransaction(OperationType.TRANSFER, amount, "Transfer out to account " + target.accountNumber + ". New" +
					" balance: " + this.balance);

            target.balance = target.balance.add(amount);
            target.addTransaction(OperationType.TRANSFER, amount, "Transfer in from account " + this.accountNumber +
					". New balance: " + target.balance);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Transfer interrupted while waiting for locks.", e);
        } finally {
            if (secondLockAccount.lock.isHeldByCurrentThread()) {
                secondLockAccount.lock.unlock();
            }
            if (firstLockAccount.lock.isHeldByCurrentThread()) {
                firstLockAccount.lock.unlock();
            }
        }
    }

    @Override
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    private void validateTargetAccount(IBankAccount targetAccount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account must not be null.");
        }
        if (this.equals(targetAccount)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }
    }

    private BankAccount castToBankAccount(IBankAccount account) {
        if (!(account instanceof BankAccount)) {
            throw new IllegalArgumentException("Target account must be a BankAccount instance.");
        }
        return (BankAccount) account;
    }

    private void lockBoth(BankAccount first, BankAccount second) throws InterruptedException {
        boolean firstLocked = first.lock.tryLock(1, TimeUnit.SECONDS);
        if (!firstLocked) {
            throw new IllegalStateException("Could not acquire lock on account: " + first.accountNumber);
        }
        boolean secondLocked = second.lock.tryLock(1, TimeUnit.SECONDS);
        if (!secondLocked) {
            first.lock.unlock();
            throw new IllegalStateException("Could not acquire lock on account: " + second.accountNumber);
        }
    }

    private void processWithdrawal(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }
        balance = balance.subtract(amount);
        addTransaction(OperationType.WITHDRAWAL, amount, "Withdrawal successful. New balance: " + balance);
    }

    private void validateAmount(BigDecimal amount, OperationType operationType) {
        if (amount == null) {
            throw new IllegalArgumentException(operationType + " amount must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(operationType + " amount must be greater than zero.");
        }
    }

    private void processDeposit(BigDecimal amount) {
        balance = balance.add(amount);
        addTransaction(OperationType.DEPOSIT, amount, "Deposit successful. New balance: " + balance);
    }

    private void addTransaction(OperationType operationType, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(operationType, amount, description);
        transactionHistory.add(transaction);
    }
}
