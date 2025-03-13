package pl.kielce.tu.fudala.lab01.banking.persistence;

import pl.kielce.tu.fudala.lab01.banking.serialization.IJsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStorage implements IFileStorage {

    private final IJsonMapper jsonMapper;

    public FileStorage(IJsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public <T> void save(String filePath, T data) throws IOException {
        String json = jsonMapper.toJson(data);
        Files.writeString(Paths.get(filePath), json);
    }

    @Override
    public <T> T load(String filePath, Class<T> clazz) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String json = new String(bytes, StandardCharsets.UTF_8);
        return jsonMapper.fromJson(json, clazz);
    }
}
