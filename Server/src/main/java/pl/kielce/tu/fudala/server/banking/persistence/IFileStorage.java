package pl.kielce.tu.fudala.server.banking.persistence;

public interface IFileStorage {
	<T> void save(String filePath, T data);

	<T> T load(String filePath, Class<T> clazz);
}
