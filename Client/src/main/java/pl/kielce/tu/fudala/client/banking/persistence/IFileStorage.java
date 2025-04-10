package pl.kielce.tu.fudala.client.banking.persistence;

import java.io.IOException;

public interface IFileStorage {
	<T> void save(String filePath, T data) throws IOException;

	<T> T load(String filePath, Class<T> clazz) throws IOException;
}
