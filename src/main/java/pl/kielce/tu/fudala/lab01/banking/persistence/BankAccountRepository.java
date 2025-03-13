package pl.kielce.tu.fudala.lab01.banking.persistence;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import pl.kielce.tu.fudala.lab01.banking.model.account.BankAccount;
import pl.kielce.tu.fudala.lab01.banking.model.account.IBankAccount;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BankAccountRepository implements IBankAccountRepository {

    private static final String DATA_DIRECTORY = "data";
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
            System.err.println("Error while initializing data directory: " + e.getMessage());
        }
    }

    @Override
    public IBankAccount getAccountByPesel(String pesel) {
        return accounts.get(pesel);
    }

    @Override
    public void addAccount(IBankAccount account) {
        if (!(account instanceof BankAccount)) {
            throw new IllegalArgumentException("Unsupported account type. Expected BankAccount implementation.");
        }
        accounts.put(account.getPesel(), account);
        saveAccount((BankAccount) account);
    }

    @Override
    public void updateAccount(IBankAccount account) {
        if (!(account instanceof BankAccount)) {
            throw new IllegalArgumentException("Unsupported account type. Expected BankAccount implementation.");
        }
        accounts.put(account.getPesel(), account);
        saveAccount((BankAccount) account);
    }

    private void createDataDirectoryIfNotExists() throws IOException {
        Path dataDir = Paths.get(DATA_DIRECTORY);
        if (!Files.exists(dataDir)) {
            Files.createDirectory(dataDir);
        }
    }

    private void loadAllAccountsFromDataDirectory() throws IOException {
        Path dataDir = Paths.get(DATA_DIRECTORY);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dataDir, "*.json")) {
            for (Path path : directoryStream) {
                loadSingleAccount(path);
            }
        }
    }

    private void loadSingleAccount(Path path) {
        try {
            BankAccount account = fileStorage.load(path.toString(), BankAccount.class);
            accounts.put(account.getPesel(), account);
        } catch (IOException e) {
            System.err.println("Error reading account file " + path + ": " + e.getMessage());
        }
    }

    private void saveAccount(BankAccount account) {
        try {
            String filePath = DATA_DIRECTORY + "/" + account.getPesel() + ".json";
            fileStorage.save(filePath, account);
        } catch (IOException e) {
            System.err.println("Error saving account " + account.getPesel() + ": " + e.getMessage());
        }
    }
}
