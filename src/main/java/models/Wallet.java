package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // Геттер для получения списка транзакций
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Метод для добавления дохода в кошелек.
     * Увеличивает баланс и добавляет новую транзакцию типа INCOME.
     * @param amount Сумма дохода.
     * @param category Категория дохода (например, "Зарплата").
     */
    public void addIncome(BigDecimal amount, String category) {
        balance = balance.add(amount);
        transactions.add(new Transaction(amount, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), TransactionType.INCOME, category));
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
        // Проверка превышения лимита бюджета по категории
        BigDecimal remainingBudget = getBudgetRemain(category);
        if (remainingBudget.compareTo(amount) < 0) {
            System.out.println("Превышен лимит бюджета для категории: " + category);
        }

        // Проверка превышения баланса
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            transactions.add(new Transaction(amount, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), TransactionType.EXPENSE, category));
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
     * Установка бюджета для определенной категории.
     * @param category Категория, для которой устанавливается бюджет.
     * @param amount Сумма бюджета.
     */
    public void setBudget(String category, BigDecimal amount) {
        budgets.put(category, amount);
    }

    /**
     * Получение информации о бюджете для определенной категории.
     * Показывает общий бюджет.
     * @param category Категория для проверки бюджета.
     * @return Числовое значение общего бюджета для указанной категории.
     */
    public BigDecimal getBudget(String category) {
        return budgets.getOrDefault(category, BigDecimal.ZERO);
    }

    public Map<String, BigDecimal> getBudgets() {
        return new HashMap<>(budgets);
    }

    /**
     * Получение информации о потраченном бюджете для определенной категории.
     * Показывает потраченный бюджет.
     * @param category Категория для проверки бюджета.
     * @return Числовое значение потраченного бюджета для указанной категории.
     */
    public BigDecimal getBudgetSpent(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equals(category) && t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получение оставшегося бюджета для категории.
     * Рассчитывает разницу между установленным бюджетом и потраченными средствами.
     * @param category Категория для вычисления оставшегося бюджета.
     * @return Оставшийся бюджет для категории.
     */
    public BigDecimal getBudgetRemain(String category) {
        BigDecimal budget = getBudget(category);
        BigDecimal spent = getBudgetSpent(category);
        return budget.subtract(spent);
    }
}