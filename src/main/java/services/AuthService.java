package services;

import models.*;
import java.util.HashMap;
import java.util.Map;
import storage.FileStorage;
import java.io.IOException;

/**
 * Класс для обработки аутентификации пользователей.
 * Реализует регистрацию и вход пользователей в систему.
 */
public class AuthService {
    private Map<String, User> users;
    private static final String STORAGE_FILE = "users.dat";

    /**
     * Конструктор для инициализации сервиса аутентификации.
     * При инициализации пытаемся загрузить данные о пользователях из файла.
     * Если загрузка не удалась, инициализируем пустую коллекцию пользователей.
     */
    @SuppressWarnings("unchecked")
    public AuthService() {
        try {
            Object data = FileStorage.loadData(STORAGE_FILE);
            this.users = (Map<String, User>) data;
        } catch (IOException | ClassNotFoundException e) {
            this.users = new HashMap<>();
        }
    }

    /**
     * Регистрация нового пользователя в системе.
     * Проверяет, существует ли уже пользователь с таким именем.
     * Если нет, сохраняет его в коллекции.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @return true, если регистрация прошла успешно, иначе false.
     */
    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Пользователь уже существует
        }

        // Добавляем нового пользователя с хэшированным паролем
        users.put(username, new User(username, Integer.toString(password.hashCode())));
        saveUsers();
        return true;
    }

    /**
     * Вход пользователя в систему.
     * Проверяет, существует ли пользователь с данным именем и правильно ли введен пароль.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @return объект пользователя, если аутентификация успешна, иначе null.
     */

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            return user;
        }
        return null;
    }

    /**
     * Сохраняет список пользователей в файл.
     * Использует класс FileStorage для сериализации коллекции пользователей.
     * Если происходит ошибка при сохранении, выводится сообщение в консоль.
     */
    public void saveUsers() {
        try {
            FileStorage.saveData(users, STORAGE_FILE);
        } catch (IOException e) {
            System.out.println("Не удалось сохранить пользователей.");
        }
    }

    /**
     * Геттер для получения списка всех зарегистрированных пользователей.
     *
     * @return Список всех пользователей
     */
    public Map<String, User> getAllUsers() {
        return users;
    }
}
