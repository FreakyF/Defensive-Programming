package pl.kielce.tu.fudala.client.banking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.kielce.tu.fudala.client.banking.dto.CreateBankAccountDTO;
import pl.kielce.tu.fudala.client.banking.model.account.BankAccount;
import pl.kielce.tu.fudala.client.banking.model.transaction.Transaction;
import pl.kielce.tu.fudala.client.banking.service.BankAccountService;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class BankController {
	private final BankAccountService bankAccountService;
	private String currentPesel;

	private static final String REGISTER = "register";
	private static final String ACCOUNT = "account";
	private static final String REDIRECT_ACCOUNT = "redirect:/account";
	private static final String SUCCESS_MESSAGE = "successMessage";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String REDIRECT_LOGIN = "redirect:/login";

	@Autowired
	public BankController(BankAccountService bankAccountService) {
		this.bankAccountService = bankAccountService;
	}

	@GetMapping("/")
	public String showHomePage() {
		return "index";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute(ACCOUNT, new CreateBankAccountDTO());
		return REGISTER;
	}

	@PostMapping("/register")
	public String registerAccount(@Valid @ModelAttribute(ACCOUNT) CreateBankAccountDTO account,
	                              BindingResult result,
	                              RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return REGISTER;
		}

		try {
			bankAccountService.createAccount(account);
			currentPesel = account.getPesel();
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Account created successfully!");
			return REDIRECT_ACCOUNT;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
			return REGISTER;
		}
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String pesel, RedirectAttributes redirectAttributes) {
		try {
			bankAccountService.getAccount(pesel);
			currentPesel = pesel;
			return REDIRECT_ACCOUNT;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
			return REDIRECT_LOGIN;
		}
	}

	@GetMapping("/account")
	public String showAccountDetails(Model model) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		BankAccount account = bankAccountService.getAccount(currentPesel);
		model.addAttribute(ACCOUNT, account);
		return ACCOUNT;
	}

	@PostMapping("/deposit")
	public String deposit(@RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		try {
			bankAccountService.deposit(currentPesel, amount);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Deposit successful!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
		}

		return REDIRECT_ACCOUNT;
	}

	@PostMapping("/withdraw")
	public String withdraw(@RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		try {
			bankAccountService.withdraw(currentPesel, amount);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Withdrawal successful!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
		}

		return REDIRECT_ACCOUNT;
	}

	@GetMapping("/transfer")
	public String showTransferForm(Model model) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		model.addAttribute("sourceAccount", bankAccountService.getAccount(currentPesel));
		return "transfer";
	}

	@PostMapping("/transfer")
	public String transfer(@RequestParam String targetPesel,
	                       @RequestParam BigDecimal amount,
	                       RedirectAttributes redirectAttributes) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		try {
			bankAccountService.transfer(currentPesel, targetPesel, amount);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Transfer successful!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
		}

		return REDIRECT_ACCOUNT;
	}

	@GetMapping("/transactions")
	public String showTransactions(Model model) {
		if (currentPesel == null) {
			return REDIRECT_LOGIN;
		}

		List<Transaction> transactions = bankAccountService.getTransactionHistory(currentPesel);
		model.addAttribute("transactions", transactions);
		return "transactions";
	}

	@GetMapping("/logout")
	public String logout(RedirectAttributes redirectAttributes) {
		currentPesel = null;
		redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Successfully logged out.");
		return "redirect:/";
	}

}
