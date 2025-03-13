package pl.kielce.tu.fudala.lab01.banking.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ITransaction {
    UUID getTransactionId();

    OperationType getOperationType();

    BigDecimal getAmount();

    LocalDateTime getDateTimeOfTransaction();

    String getDescription();
}
