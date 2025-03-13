package pl.kielce.tu.fudala.lab01.banking.serialization;

public interface IJsonMapper {
    String toJson(Object object);

    <T> T fromJson(String json, Class<T> clazz);
}
