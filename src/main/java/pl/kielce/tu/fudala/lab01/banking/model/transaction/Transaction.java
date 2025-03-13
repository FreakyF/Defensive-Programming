package pl.kielce.tu.fudala.lab01.banking.model.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction implements ITransaction {
    @NotNull
    private final UUID transactionId;

    @NotNull
    private final OperationType operationType;

    @NotNull
    @Positive
    private final BigDecimal amount;

    @NotNull
    private final LocalDateTime dateTimeOfTransaction;

    @Size(max = 255)
    private final String description;

    public Transaction(OperationType operationType, BigDecimal amount, String description) {
        this.transactionId = UUID.randomUUID();
        this.operationType = operationType;
        this.amount = amount;
        this.dateTimeOfTransaction = LocalDateTime.now();
        this.description = description;
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public LocalDateTime getDateTimeOfTransaction() {
        return dateTimeOfTransaction;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
