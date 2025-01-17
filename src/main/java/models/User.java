package models;

import java.io.Serializable;
import java.util.*;

/**
 * Класс, представляющий пользователя в системе.
 * Хранит данные о пользователе, такие как имя пользователя, хэш пароля и его кошелек.
 */
public class User implements Serializable {
    private final String username;
    private final String passwordHash;
    private final Wallet wallet;

    /**
     * Конструктор для создания нового пользователя с именем и хэшированным паролем.
     * Кошелек пользователя создается автоматически при инициализации.
     *
     * @param username Имя пользователя.
     * @param passwordHash Хэш пароля для безопасной аутентификации.
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.wallet = new Wallet();
    }

    /**
     * Геттер для получения имени пользователя.
     * Предоставляет доступ к имени пользователя, который был установлен при инициализации.
     *
     * @return Строка с именем пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Геттер для получения кошелька пользователя.
     * Позволяет получить доступ к финансовым данным и операциям, связанным с этим пользователем.
     *
     * @return Объект класса Wallet, принадлежащий пользователю.
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Метод для проверки пароля пользователя.
     * Сравнивает хэшированное значение введенного пароля и хранимый в объекте хэш пароля.
     *
     * @param password Введенный пароль для аутентификации.
     * @return Истина, если пароли совпадают, иначе ложь.
     */
    public boolean validatePassword(String password) {
        return Objects.equals(passwordHash, password.hashCode() + ""); // Simple hash for illustration
    }
}