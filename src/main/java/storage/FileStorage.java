package storage;

import java.io.*;

/**
 * Класс для сохранения и загрузки данных в/из файла.
 * Использует потоки ввода/вывода для сериализации объектов.
 */
public class FileStorage {

    /**
     * Сохраняет данные в файл.
     *
     * @param data Данные, которые нужно сохранить.
     * @param filePath Путь к файлу для сохранения данных.
     * @throws IOException Исключение, если произошла ошибка при записи в файл.
     */
    public static void saveData(Object data, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Загружает данные из файла.
     *
     * @param filePath Путь к файлу, из которого загружаются данные.
     * @return Загруженные данные.
     * @throws IOException Исключение, если произошла ошибка при чтении из файла.
     * @throws ClassNotFoundException Исключение, если не удаётся найти класс для десериализации.
     */
    public static Object loadData(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке данных из файла: " + e.getMessage());
            throw e;
        }
    }
}