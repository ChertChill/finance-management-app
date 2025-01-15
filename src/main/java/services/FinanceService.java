package services;

import models.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Класс для управления финансовыми операциями пользователей.
 * Включает добавление дохода, расхода, установку бюджета и получение статистики.
 */
public class FinanceService {

    /**
     * Добавляет доход в кошелек пользователя.
     * @param user Пользователь, чей доход будет добавлен.
     * @param amount Сумма дохода.
     * @param category Категория дохода.
     */
    public void addIncome(User user, BigDecimal amount, String category) {
        user.getWallet().addIncome(amount, category);
    }

    /**
     * Добавляет расход в кошелек пользователя.
     * @param user Пользователь, чей расход будет добавлен.
     * @param amount Сумма расхода.
     * @param category Категория расхода.
     */
    public void addExpense(User user, BigDecimal amount, String category) {
        user.getWallet().addExpense(amount, category);
    }

    /**
     * Устанавливает бюджет для категории.
     * @param user Пользователь, для которого устанавливается бюджет.
     * @param category Категория, для которой устанавливается бюджет.
     * @param amount Сумма бюджета.
     */
    public void setBudget(User user, String category, BigDecimal amount) {
        user.getWallet().setBudget(category, amount);
    }

    /**
     * Получает статистику по операциям пользователя.
     * Включает баланс и список всех транзакций по категории "Все".
     * @param user Пользователь, для которого получаем статистику.
     * @return Строка с балансом и списком всех транзакций.
     */
//    public String getStatistics(User user) {
//        Wallet wallet = user.getWallet();
//        StringBuilder stats = new StringBuilder();
//
//        // Добавляем информацию о балансе
//        stats.append("Баланс: ").append(wallet.getBalance()).append("\n");
//
//        // Для каждой транзакции добавляем информацию о типе, сумме и категории
//        for (Transaction t : wallet.getTransactionsByCategory("Все")) {
//            stats.append(t.getType()).append(" ").append(t.getAmount()).append(" на ").append(t.getCategory()).append("\n");
//        }
//
//        // Возвращаем сформированную строку с статистикой
//        return stats.toString();
//    }

    /**
     * Получает обзор всех бюджетов пользователя.
     * Включает статус бюджета для каждой категории.
     * @param user Пользователь, для которого получаем обзор бюджета.
     * @return Строка с бюджетным обзором для всех категорий.
     */
    public String getBudgetOverview(User user) {
        Wallet wallet = user.getWallet();
        StringBuilder overview = new StringBuilder();

//        // Добавляем статус бюджета для каждой категории пользователя
//        for (String category : wallet.getAllCategories()) {
//            overview.append(wallet.getBudgetStatus(category)).append("\n");
//        }

        // Получаем все категории пользователя
        Set<String> categories = wallet.getAllCategories();

        // Проверяем, есть ли категории
        if (categories.isEmpty()) {
            overview.append("Нет доступных категорий для бюджета.");
        } else {
            // Добавляем статус бюджета для каждой категории пользователя
            for (String category : categories) {
                overview.append(wallet.getBudgetStatus(category)).append("\n");
            }
        }

        // Возвращаем строку с полным обзором бюджета
        return overview.toString();
    }
}