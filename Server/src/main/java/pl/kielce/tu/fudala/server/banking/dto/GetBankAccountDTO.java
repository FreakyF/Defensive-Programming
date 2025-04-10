package pl.kielce.tu.fudala.server.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.kielce.tu.fudala.server.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GetBankAccountDTO {
    private UUID accountId;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private BigDecimal balance;
    private String pesel;
    private String email;
    private List<Transaction> transactionHistory;
}
