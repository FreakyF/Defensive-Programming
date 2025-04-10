package pl.kielce.tu.fudala.server.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositDTO {
    @NotNull
    @Positive
    private BigDecimal amount;
}
