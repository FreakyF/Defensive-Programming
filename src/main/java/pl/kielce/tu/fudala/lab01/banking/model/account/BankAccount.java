package pl.kielce.tu.fudala.lab01.banking.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.OperationType;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;
import pl.kielce.tu.fudala.lab01.banking.validator.email.ValidEmail;
import pl.kielce.tu.fudala.lab01.banking.validator.iban.ValidIban;
import pl.kielce.tu.fudala.lab01.banking.validator.pesel.ValidPesel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount implements IBankAccount {

    @JsonIgnore
    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private final List<Transaction> transactionHistory = new ArrayList<>();

    @Getter
    @NotNull
    private UUID accountId = UUID.randomUUID();

    @Getter
    @NotBlank
    private String firstName;

    @Getter
    @NotBlank
    private String lastName;

    @Getter
    @ValidIban
    @NotBlank
    private String accountNumber;

    @Getter
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;

    @Getter
    @ValidPesel
    @NotBlank
    private String pesel;

    @Getter
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

        BigDecimal fee = calculateTransferFee(amount, target.getAccountNumber());
        BigDecimal totalDeduction = amount.add(fee);

        BankAccount firstLock = this.accountNumber.compareTo(target.getAccountNumber()) < 0 ? this : target;
        BankAccount secondLock = this.accountNumber.compareTo(target.getAccountNumber()) < 0 ? target : this;

        try {
            lockBoth(firstLock, secondLock);
            ensureSufficientFunds(totalDeduction);
            deductFunds(fee, "Transfer fee to account " + target.getAccountNumber());
            deductFunds(amount, "Outgoing transfer to account " + target.getAccountNumber());
            target.addFunds(amount, "Incoming transfer from account " + this.getAccountNumber());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Transfer interrupted while waiting for locks.", e);
        } finally {
            unlockBoth(firstLock, secondLock);
        }
    }

    private BankAccount castToBankAccount(IBankAccount account) {
        if (!(account instanceof BankAccount)) {
            throw new IllegalArgumentException("Target account must be an instance of BankAccount.");
        }
        return (BankAccount) account;
    }

    private void lockBoth(BankAccount first, BankAccount second) throws InterruptedException {
        if (!first.lock.tryLock(1, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Could not acquire lock on account: " + first.getAccountNumber());
        }
        if (!second.lock.tryLock(1, TimeUnit.SECONDS)) {
            first.lock.unlock();
            throw new IllegalStateException("Could not acquire lock on account: " + second.getAccountNumber());
        }
    }

    private void unlockBoth(BankAccount first, BankAccount second) {
        if (second.lock.isHeldByCurrentThread()) {
            second.lock.unlock();
        }
        if (first.lock.isHeldByCurrentThread()) {
            first.lock.unlock();
        }
    }

    private BigDecimal calculateTransferFee(BigDecimal amount, String targetIban) {
        if (targetIban.startsWith("PL")) {
            return new BigDecimal("2.00");
        }
        BigDecimal fee = amount.multiply(new BigDecimal("0.15"));
        return fee.compareTo(new BigDecimal("10.00")) < 0 ? new BigDecimal("10.00") : fee;
    }

    private void ensureSufficientFunds(BigDecimal requiredAmount) {
        if (this.balance.compareTo(requiredAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds on source account. Required: " + requiredAmount +
                    ", available: " + this.balance);
        }
    }

    private void deductFunds(BigDecimal amount, String description) {
        this.balance = this.balance.subtract(amount);
        addTransaction(OperationType.TRANSFER, amount, description + ". New balance: " + this.balance);
    }

    private void addFunds(BigDecimal amount, String description) {
        this.balance = this.balance.add(amount);
        addTransaction(OperationType.TRANSFER, amount, description + ". New balance: " + this.balance);
    }

    private void processWithdrawal(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }
        this.balance = this.balance.subtract(amount);
        addTransaction(OperationType.WITHDRAWAL, amount, "Withdrawal successful. New balance: " + this.balance);
    }

    private void validateAmount(BigDecimal amount, OperationType operationType) {
        if (amount == null) {
            throw new IllegalArgumentException(operationType + " amount must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(operationType + " amount must be greater than zero.");
        }
    }

    private void validateTargetAccount(IBankAccount targetAccount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account must not be null.");
        }
        if (this.equals(targetAccount)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }
    }

    private void processDeposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        addTransaction(OperationType.DEPOSIT, amount, "Deposit successful. New balance: " + this.balance);
    }

    private void addTransaction(OperationType operationType, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(operationType, amount, description);
        transactionHistory.add(transaction);
    }
}
