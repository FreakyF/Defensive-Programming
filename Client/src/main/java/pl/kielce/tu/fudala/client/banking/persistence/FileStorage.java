package pl.kielce.tu.fudala.client.banking.persistence;

import org.springframework.stereotype.Component;
import pl.kielce.tu.fudala.client.banking.exception.FileOperationException;
import pl.kielce.tu.fudala.client.banking.serialization.IJsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class FileStorage implements IFileStorage {

	private final IJsonMapper jsonMapper;

	public FileStorage(IJsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	@Override
	public <T> void save(String filePath, T data) {
		try {
			String json = jsonMapper.toJson(data);
			Files.writeString(Paths.get(filePath), json);
		} catch (IOException e) {
			throw new FileOperationException("Error saving file: " + filePath, e);
		}
	}

	@Override
	public <T> T load(String filePath, Class<T> clazz) {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(filePath));
			String json = new String(bytes, StandardCharsets.UTF_8);
			return jsonMapper.fromJson(json, clazz);
		} catch (IOException e) {
			throw new FileOperationException("Error loading file: " + filePath, e);
		}
	}
}
