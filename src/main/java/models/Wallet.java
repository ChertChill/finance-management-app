package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс, представляющий кошелек пользователя с транзакциями и бюджетами.
 * Хранит баланс кошелька, список транзакций и бюджеты по категориям.
 */
public class Wallet implements Serializable {
    private BigDecimal balance;
    private List<Transaction> transactions;
    private Map<String, BigDecimal> budgets;

    /**
     * Конструктор по умолчанию для инициализации кошелька.
     * Баланс устанавливается на 0, и создаются пустые коллекции для транзакций и бюджета.
     */
    public Wallet() {
        /**
         * @param balance Начальный баланс кошелька. Устанавливается на 0.
         * @param transactions Список транзакций пользователя, инициализируется пустым.
         * @param budgets Карта для хранения бюджета по категориям, инициализируется пустой.
         */
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    // Геттер для получения текущего баланса кошелька.
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Метод для добавления дохода в кошелек.
     * Увеличивает баланс и добавляет новую транзакцию типа INCOME.
     * @param amount Сумма дохода.
     * @param category Категория дохода (например, "Зарплата").
     */
    public void addIncome(BigDecimal amount, String category) {
        balance = balance.add(amount);
        transactions.add(new Transaction(amount, LocalDateTime.now(), TransactionType.INCOME, category));
    }

    /**
     * Метод для добавления расхода в кошелек.
     * Уменьшает баланс и добавляет транзакцию типа EXPENSE.
     * Если средств недостаточно, выбрасывает исключение.
     * @param amount Сумма расхода.
     * @param category Категория расхода (например, "Продукты").
     * @throws IllegalArgumentException Если недостаточно средств для расхода.
     */
    public void addExpense(BigDecimal amount, String category) {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            transactions.add(new Transaction(amount, LocalDateTime.now(), TransactionType.EXPENSE, category));
        } else {
            throw new IllegalArgumentException("Недостаточно средств.");
        }
    }

    /**
     * Получение списка всех транзакций по определенной категории.
     * @param category Категория транзакции.
     * @return Список транзакций, которые принадлежат указанной категории.
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().equals(category)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Получение всех уникальных категорий транзакций и бюджетов.
     * @return Множество категорий, включающее все транзакции и бюджетные категории.
     */
    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Transaction t : transactions) {
            categories.add(t.getCategory());
        }
        categories.addAll(budgets.keySet());
        return categories;
    }

    /**
     * Получение состояния бюджета для определенной категории.
     * Показывает общий бюджет и потраченные средства.
     * @param category Категория для проверки бюджета.
     * @return Строка с информацией о бюджете для указанной категории.
     */
    public String getBudgetStatus(String category) {
        BigDecimal budget = budgets.getOrDefault(category, BigDecimal.ZERO);
        BigDecimal spent = transactions.stream()
                .filter(t -> t.getCategory().equals(category) && t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return "Бюджет для " + category + ": " + budget + ", потрачено: " + spent;
    }

    /**
     * Установка бюджета для определенной категории.
     * @param category Категория, для которой устанавливается бюджет.
     * @param amount Сумма бюджета.
     */
    public void setBudget(String category, BigDecimal amount) {
        budgets.put(category, amount);
    }

    /**
     * Получение оставшегося бюджета для категории.
     * Рассчитывает разницу между установленным бюджетом и потраченными средствами.
     * @param category Категория для вычисления оставшегося бюджета.
     * @return Оставшийся бюджет для категории.
     */

    public BigDecimal getRemainingBudget(String category) {
        BigDecimal budget = budgets.getOrDefault(category, BigDecimal.ZERO);
        BigDecimal spent = transactions.stream()
                .filter(t -> t.getCategory().equals(category) && t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return budget.subtract(spent);
    }
}