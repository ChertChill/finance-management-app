package services;

/**
 * Класс для отправки уведомлений пользователю.
 * Используется для вывода сообщений на экран.
 */
public class NotificationService {

    /**
     * Отправляет уведомление пользователю.
     * @param message Сообщение для уведомления.
     */
    public void notify(String message) {
        System.out.println("Уведомление: " + message);
    }
}
