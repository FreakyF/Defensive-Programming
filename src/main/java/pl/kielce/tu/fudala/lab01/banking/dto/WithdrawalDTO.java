package pl.kielce.tu.fudala.lab01.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalDTO {
    @NotNull
    @Positive
    private BigDecimal amount;
}
