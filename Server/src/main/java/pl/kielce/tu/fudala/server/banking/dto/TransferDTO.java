package pl.kielce.tu.fudala.server.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {
    @NotBlank
    private String fromPesel;

    @NotBlank
    private String toPesel;

    @NotNull
    @Positive
    private BigDecimal amount;
}
