package pl.kielce.tu.fudala.server.banking.serialization;

public interface IJsonMapper {
    String toJson(Object object);

    <T> T fromJson(String json, Class<T> clazz);
}
