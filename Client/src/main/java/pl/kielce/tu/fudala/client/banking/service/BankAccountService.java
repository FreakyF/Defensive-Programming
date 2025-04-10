package pl.kielce.tu.fudala.client.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kielce.tu.fudala.client.banking.dto.CreateBankAccountDTO;
import pl.kielce.tu.fudala.client.banking.dto.DepositDTO;
import pl.kielce.tu.fudala.client.banking.dto.TransferDTO;
import pl.kielce.tu.fudala.client.banking.dto.WithdrawalDTO;
import pl.kielce.tu.fudala.client.banking.exception.BadRequestException;
import pl.kielce.tu.fudala.client.banking.exception.InsufficientFundsException;
import pl.kielce.tu.fudala.client.banking.exception.ResourceNotFoundException;
import pl.kielce.tu.fudala.client.banking.model.account.BankAccount;
import pl.kielce.tu.fudala.client.banking.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class BankAccountService {
	private final RestTemplate restTemplate;

	private static final String BASE_URL = "http://localhost:8080/api/accounts";

	@Autowired
	public BankAccountService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void createAccount(CreateBankAccountDTO account) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CreateBankAccountDTO> request = new HttpEntity<>(account, headers);

			ResponseEntity<CreateBankAccountDTO> response = restTemplate.postForEntity(
					BASE_URL, request, CreateBankAccountDTO.class);

			response.getBody();
		} catch (Exception e) {
			throw new BadRequestException("Failed to create account: " + e.getMessage());
		}
	}

	public BankAccount getAccount(String pesel) {
		try {
			ResponseEntity<BankAccount> response = restTemplate.getForEntity(
					BASE_URL + "/" + pesel, BankAccount.class);
			return response.getBody();
		} catch (Exception e) {
			throw new ResourceNotFoundException("Account with PESEL " + pesel + " not found");
		}
	}

	public void deposit(String pesel, BigDecimal amount) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			DepositDTO depositDTO = new DepositDTO();
			depositDTO.setAmount(amount);

			HttpEntity<DepositDTO> request = new HttpEntity<>(depositDTO, headers);

			String url = BASE_URL + "/" + pesel + "/deposit";
			restTemplate.postForEntity(url, request, Void.class);

		} catch (Exception e) {
			throw new BadRequestException("Failed to make deposit: " + e.getMessage());
		}
	}

	public void withdraw(String pesel, BigDecimal amount) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			WithdrawalDTO withdrawalDTO = new WithdrawalDTO();
			withdrawalDTO.setAmount(amount);

			HttpEntity<WithdrawalDTO> request = new HttpEntity<>(withdrawalDTO, headers);

			String url = BASE_URL + "/" + pesel + "/withdraw";
			restTemplate.postForEntity(url, request, Void.class);
		} catch (Exception e) {
			throw new InsufficientFundsException("Insufficient funds or invalid amount");
		}
	}

	public void transfer(String sourcePesel, String targetPesel, BigDecimal amount) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			TransferDTO transferDTO = new TransferDTO();
			transferDTO.setFromPesel(sourcePesel);
			transferDTO.setToPesel(targetPesel);
			transferDTO.setAmount(amount);

			HttpEntity<TransferDTO> request = new HttpEntity<>(transferDTO, headers);

			restTemplate.postForEntity(BASE_URL + "/transfer", request, Void.class);
		} catch (Exception e) {
			throw new InsufficientFundsException("Insufficient funds or invalid transaction details");
		}
	}

	public List<Transaction> getTransactionHistory(String pesel) {
		try {
			ResponseEntity<Transaction[]> response = restTemplate.getForEntity(
					BASE_URL + "/" + pesel + "/history", Transaction[].class);

			return Arrays.asList(Objects.requireNonNull(response.getBody()));
		} catch (Exception e) {
			throw new ResourceNotFoundException("Account with PESEL " + pesel + " not found");
		}
	}

}