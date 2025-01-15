package models;

import java.io.Serializable;
import java.util.*;

/**
 * Класс, представляющий пользователя в системе.
 * Хранит данные о пользователе, такие как имя пользователя, хэш пароля и его кошелек.
 */
public class User implements Serializable {
    private String username;
    private String passwordHash;
    private Wallet wallet;

    /**
     * Конструктор для создания нового пользователя с именем и хэшированным паролем.
     * Кошелек пользователя создается автоматически при инициализации.
     * @param username Имя пользователя.
     * @param passwordHash Хэш пароля для безопасной аутентификации.
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.wallet = new Wallet();
    }

    // Геттер для получения имени пользователя.
    public String getUsername() {
        return username;
    }

    // Геттер для получения кошелька пользователя.
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Метод для проверки пароля пользователя.
     * Сравнивает введенный пароль с хэшом, сохраненным в системе.
     * @param password Введенный пароль для проверки.
     * @return Истина, если пароли совпадают, иначе ложь.
     */
    public boolean validatePassword(String password) {
        return Objects.equals(passwordHash, password.hashCode() + ""); // Simple hash for illustration
    }
}