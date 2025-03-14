package pl.kielce.tu.fudala.lab01.banking.model.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @NotNull
    private UUID transactionId;

    @NotNull
    private OperationType operationType;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime dateTimeOfTransaction;

    @Size(max = 255)
    private String description;

    public Transaction(OperationType operationType, BigDecimal amount, String description) {
        this.transactionId = UUID.randomUUID();
        this.operationType = operationType;
        this.amount = amount;
        this.dateTimeOfTransaction = LocalDateTime.now();
        this.description = description;
    }
}
