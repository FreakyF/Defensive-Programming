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
    private final Map<String, BankAccount> accounts = new ConcurrentHashMap<>();
    private final IFileStorage fileStorage;

    public BankAccountRepository(IFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }


    @PostConstruct
    public void init() {
        try {
            Path dataDir = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataDir)) {
                Files.createDirectory(dataDir);
            }
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dataDir, "*.json")) {
                for (Path path : directoryStream) {
                    try {
                        BankAccount account = fileStorage.load(path.toString(), BankAccount.class);
                        accounts.put(account.getPesel(), account);
                    } catch (IOException e) {
                        System.err.println("Błąd wczytywania konta z pliku " + path + ": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas inicjalizacji katalogu danych: " + e.getMessage());
        }
    }

    @Override
    public BankAccount getAccountByPesel(String pesel) {
        return accounts.get(pesel);
    }

    @Override
    public void addAccount(IBankAccount account) {
        accounts.put(account.getPesel(), account);
        saveAccount(account);
    }

    @Override
    public void updateAccount(IBankAccount account) {
        accounts.put(account.getPesel(), account);
        saveAccount(account);
    }


    private void saveAccount(IBankAccount account) {
        try {
            String filePath = DATA_DIRECTORY + "/" + account.getPesel() + ".json";
            fileStorage.save(filePath, account);
        } catch (IOException e) {
            System.err.println("Błąd zapisu konta " + account.getPesel() + ": " + e.getMessage());
        }
    }
}
