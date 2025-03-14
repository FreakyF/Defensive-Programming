package pl.kielce.tu.fudala.lab01.banking.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kielce.tu.fudala.lab01.banking.dto.CreateBankAccountDTO;
import pl.kielce.tu.fudala.lab01.banking.dto.DepositDTO;
import pl.kielce.tu.fudala.lab01.banking.dto.GetBankAccountDTO;
import pl.kielce.tu.fudala.lab01.banking.dto.TransferDTO;
import pl.kielce.tu.fudala.lab01.banking.dto.UpdateAccountDTO;
import pl.kielce.tu.fudala.lab01.banking.dto.WithdrawalDTO;
import pl.kielce.tu.fudala.lab01.banking.model.account.BankAccount;
import pl.kielce.tu.fudala.lab01.banking.model.account.IBankAccount;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;
import pl.kielce.tu.fudala.lab01.banking.persistence.IBankAccountRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final IBankAccountRepository bankAccountRepository;

    public BankAccountController(IBankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }


    @PostMapping
    public ResponseEntity<CreateBankAccountDTO> createAccount(@Valid @RequestBody CreateBankAccountDTO createBankAccountDTO) {
        BankAccount bankAccount = new BankAccount(UUID.randomUUID(), createBankAccountDTO.getFirstName(),
                createBankAccountDTO.getLastName(), createBankAccountDTO.getAccountNumber(),
                createBankAccountDTO.getBalance(), createBankAccountDTO.getPesel(), createBankAccountDTO.getEmail());
        bankAccountRepository.createAccount(bankAccount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/{pesel}/deposit")
    public ResponseEntity<DepositDTO> deposit(@PathVariable String pesel, @RequestBody DepositDTO depositDTO) {
        bankAccountRepository.deposit(pesel, depositDTO.getAmount());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{pesel}/withdraw")
    public ResponseEntity<WithdrawalDTO> withdraw(@PathVariable String pesel,
                                                  @RequestBody WithdrawalDTO withdrawalDTO) {
        bankAccountRepository.withdraw(pesel, withdrawalDTO.getAmount());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/transfer")
    public ResponseEntity<TransferDTO> transfer(@RequestBody TransferDTO transferDTO) {
        bankAccountRepository.transfer(transferDTO.getFromPesel(), transferDTO.getToPesel(), transferDTO.getAmount());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{pesel}/history")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable String pesel) {
        List<Transaction> history = bankAccountRepository.getHistory(pesel);
        return ResponseEntity.ok(history);
    }


    @DeleteMapping("/{pesel}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String pesel) {
        bankAccountRepository.removeAccount(pesel);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{pesel}")
    public ResponseEntity<GetBankAccountDTO> getAccountData(@PathVariable String pesel) {
        IBankAccount account = bankAccountRepository.getAccountData(pesel);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        GetBankAccountDTO dto = new GetBankAccountDTO(account.getAccountId(), account.getFirstName(),
                account.getLastName(), account.getAccountNumber(), account.getBalance(), account.getPesel(),
                account.getEmail(), account.getTransactionHistory());


        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{pesel}")
    public ResponseEntity<UpdateAccountDTO> updateAccountData(@PathVariable String pesel,
                                                              @Valid @RequestBody UpdateAccountDTO updateRequest) {
        IBankAccount existingAccount = bankAccountRepository.getAccountData(pesel);
        if (existingAccount == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateProvided(updateRequest.getFirstName())) {
            existingAccount.setFirstName(updateRequest.getFirstName());
        }
        if (updateProvided(updateRequest.getLastName())) {
            existingAccount.setLastName(updateRequest.getLastName());
        }
        if (updateProvided(updateRequest.getEmail())) {
            existingAccount.setEmail(updateRequest.getEmail());
        }

        bankAccountRepository.updateAccountData(existingAccount);

        UpdateAccountDTO response = new UpdateAccountDTO();
        response.setFirstName(existingAccount.getFirstName());
        response.setLastName(existingAccount.getLastName());
        response.setEmail(existingAccount.getEmail());

        return ResponseEntity.ok(response);
    }

    private boolean updateProvided(String value) {
        return value != null && !value.isBlank();
    }
}
