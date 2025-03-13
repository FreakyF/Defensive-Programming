package pl.kielce.tu.fudala.lab01.banking.persistence;

import java.io.IOException;

public interface IFileStorage {
    <T> void save(String filePath, T data) throws IOException;

    <T> T load(String filePath, Class<T> clazz) throws IOException;
}
