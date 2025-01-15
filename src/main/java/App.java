import ui.CommandProcessor;

/**
 * Точка входа в приложение управления финансами.
 * Запускает интерфейс командной строки.
 */
public class App {
    public static void main(String[] args) {
        // Инициализация процессора команд
        CommandProcessor processor = new CommandProcessor();

        // Запуск обработки команд
        processor.start();
    }
}