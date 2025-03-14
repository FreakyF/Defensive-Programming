package pl.kielce.tu.fudala.lab01.banking.persistence;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.kielce.tu.fudala.lab01.banking.model.account.BankAccount;
import pl.kielce.tu.fudala.lab01.banking.model.account.IBankAccount;
import pl.kielce.tu.fudala.lab01.banking.model.transaction.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BankAccountRepository implements IBankAccountRepository {

    private static final Logger LOGGER = Logger.getLogger(BankAccountRepository.class.getName());


    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_EXTENSION = ".json";
    private static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account with the given PESEL does not exist.";
    private final Map<String, IBankAccount> accounts = new ConcurrentHashMap<>();
    private final IFileStorage fileStorage;

    public BankAccountRepository(IFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @PostConstruct
    public void init() {
        try {
            createDataDirectoryIfNotExists();
            loadAllAccountsFromDataDirectory();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while initializing data directory.", e);
        }
    }

    private void loadAllAccountsFromDataDirectory() throws IOException {
        Path dataDir = Paths.get(DATA_DIRECTORY);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dataDir, "*" + FILE_EXTENSION)) {
            for (Path path : directoryStream) {
                loadSingleAccount(path);
            }
        }
    }

    @Override
    public synchronized IBankAccount getAccountByPesel(String pesel) {
        return accounts.get(pesel);
    }

    @Override
    public synchronized void addAccount(IBankAccount account) {
        validateBankAccountImplementation(account);
        if (accounts.containsKey(account.getPesel())) {
            throw new IllegalArgumentException("An account with the given PESEL already exists.");
        }
        saveOrUpdateAccount(account);
    }

    @Override
    public synchronized void updateAccount(IBankAccount account) {
        validateBankAccountImplementation(account);
        if (!accounts.containsKey(account.getPesel())) {
            throw new IllegalArgumentException(ACCOUNT_NOT_EXIST_MESSAGE);
        }
        saveOrUpdateAccount(account);
    }

    public synchronized void createAccount(IBankAccount account) {
        if (accounts.containsKey(account.getPesel())) {
            throw new IllegalArgumentException("An account with the given PESEL already exists.");
        }
        addAccount(account);
    }

    public synchronized void deposit(String pesel, BigDecimal amount) {
        IBankAccount account = getAccountOrThrow(pesel);
        validatePositiveAmount(amount, "Deposit");
        account.deposit(amount);
        saveAccount(account);
    }

    public synchronized void withdraw(String pesel, BigDecimal amount) {
        IBankAccount account = getAccountOrThrow(pesel);
        validatePositiveAmount(amount, "Withdrawal");
        account.withdraw(amount);
        saveAccount(account);
    }

    public synchronized void transfer(String fromPesel, String toPesel, BigDecimal amount) {
        IBankAccount fromAccount = getAccountOrThrow(fromPesel);
        IBankAccount toAccount = getAccountOrThrow(toPesel);
        validatePositiveAmount(amount, "Transfer");

        fromAccount.transfer(toAccount, amount);

        saveAccount(fromAccount);
        saveAccount(toAccount);
    }

    public synchronized List<Transaction> getHistory(String pesel) {
        IBankAccount account = getAccountOrThrow(pesel);
        return account.getTransactionHistory();
    }

    public synchronized void removeAccount(String pesel) {
        IBankAccount removed = accounts.remove(pesel);
        if (removed == null) {
            throw new IllegalArgumentException(ACCOUNT_NOT_EXIST_MESSAGE);
        }
        deleteAccountFile(pesel);
    }

    public synchronized IBankAccount getAccountData(String pesel) {
        return getAccountByPesel(pesel);
    }

    public synchronized void updateAccountData(IBankAccount account) {
        updateAccount(account);
    }

    private void saveOrUpdateAccount(IBankAccount account) {
        accounts.put(account.getPesel(), account);
        saveAccount(account);
    }

    private IBankAccount getAccountOrThrow(String pesel) {
        IBankAccount account = accounts.get(pesel);
        if (account == null) {
            throw new IllegalArgumentException(ACCOUNT_NOT_EXIST_MESSAGE);
        }
        return account;
    }

    private void validatePositiveAmount(BigDecimal amount, String operationName) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(operationName + " amount must be greater than 0.");
        }
    }

    private void validateBankAccountImplementation(IBankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Unsupported account type. Expected BankAccount implementation.");
        }
    }

    private void deleteAccountFile(String pesel) {
        Path filePath = Paths.get(DATA_DIRECTORY, pesel + FILE_EXTENSION);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to delete file {0}", filePath);
        }
    }

    private void createDataDirectoryIfNotExists() throws IOException {
        Path dataDir = Paths.get(DATA_DIRECTORY);
        if (!Files.exists(dataDir)) {
            Files.createDirectory(dataDir);
        }
    }

    private void loadSingleAccount(Path path) {
        try {
            BankAccount account = fileStorage.load(path.toString(), BankAccount.class);
            accounts.put(account.getPesel(), account);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading account file \"{0}\"!", path);
            LOGGER.log(Level.SEVERE, "Reason:", e);
        }
    }

    private void saveAccount(IBankAccount account) {
        Path filePath = Paths.get(DATA_DIRECTORY, account.getPesel() + FILE_EXTENSION);
        try {
            fileStorage.save(filePath.toString(), account);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving account \"{0}\"!", account.getPesel());
            LOGGER.log(Level.SEVERE, "Reason:", e);
        }
    }
}
