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
    private final List<Transaction> transactions;
    private final Map<String, BigDecimal> budgets;

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

    /**
     * Геттер для получения текущего баланса кошелька.
     *
     * @return Текущий баланс (BigDecimal).
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Геттер для получения списка транзакций кошелька.
     *
     * @return Список транзакций (List<Transaction>).
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Проверяет, используется ли указанная категория для доходов.
     *
     * @param category Название категории.
     * @return true, если категория связана с доходами, иначе false.
     */
    public boolean isIncomeCategory(String category) {
        return transactions.stream()
                .anyMatch(t -> t.getCategory().equals(category) && t.getType() == TransactionType.INCOME);
    }

    /**
     * Проверяет, является ли сумма положительным числом.
     *
     * @param amount Сумма для проверки.
     * @throws IllegalArgumentException Если сумма отрицательная или равна нулю.
     */
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Вводимое значение должно быть положительным числом.");
        }
    }

    /**
     * Проверяет, существует ли указанная категория.
     *
     * @param category Название категории.
     * @return true, если категория существует, иначе false.
     */
    public boolean doesCategoryExist(String category) {
        // Проверяем в бюджете и среди транзакций
        return budgets.containsKey(category) ||
                transactions.stream()
                        .anyMatch(t -> t.getCategory().equals(category));
    }

    /**
     * Метод для добавления дохода в кошелек.
     * Увеличивает баланс и добавляет новую транзакцию типа INCOME.
     *
     * @param category Категория дохода (например, "Зарплата").
     * @param amount Сумма дохода.
     * @throws IllegalArgumentException Если категория используется для расходов.
     */
    public void addIncome(String category, BigDecimal amount) {
        validatePositiveAmount(amount);

        // Проверка на существование расходной категории
        if (doesCategoryExist(category) && !isIncomeCategory(category) ) {
            throw new IllegalArgumentException("Категория \"" + category + "\" используется для учёта расходов. В неё нельзя добавлять доходы.");
        }

        balance = balance.add(amount);
        transactions.add(new Transaction(amount, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), TransactionType.INCOME, category));
    }

    /**
     * Метод для добавления расхода в кошелек.
     * Уменьшает баланс и создает транзакцию типа EXPENSE.
     *
     * @param category Категория расхода (например, "Продукты").
     * @param amount Сумма расхода.
     * @throws IllegalArgumentException Если недостаточно средств для расхода.
     */
    public void addExpense(String category, BigDecimal amount) {
        validatePositiveAmount(amount);

        // Проверка на существование доходной категории
        if (doesCategoryExist(category) && isIncomeCategory(category)) {
            throw new IllegalArgumentException("Категория \"" + category + "\" используется для учёта доходов. В неё нельзя добавлять расходы.");
        }

        // Если категории нет, автоматически создаем ее с бюджетом 0
        if (!doesCategoryExist(category)) {
            budgets.put(category, BigDecimal.ZERO);
        }

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
     * Устанавливает бюджет для категории.
     * Если категория уже существует, проверяет совместимость по типу транзакций.
     * Если категория не существует, она создается как категория расходов (EXPENSE).
     *
     * @param category Название категории.
     * @param amount Сумма бюджета.
     * @throws IllegalArgumentException Если категория используется как доходная.
     */
    public void setBudget(String category, BigDecimal amount) {
        validatePositiveAmount(amount);

        if (doesCategoryExist(category)) {
            // Проверяем, используется ли категория как доходная
            if (isIncomeCategory(category)) {
                throw new IllegalArgumentException("Категория \"" + category + "\" используется для учёта доходов. Для неё нельзя устанавливать бюджет.");
            }
        } else {
            // Если категории нет, добавляем ее как категорию расходов
            budgets.put(category, BigDecimal.ZERO);
        }

        // Устанавливаем бюджет
        budgets.put(category, amount);
    }

    /**
     * Возвращает общий бюджет для определенной категории.
     *
     * @param category Категория для проверки бюджета.
     * @return Числовое значение общего бюджета для указанной категории.
     */
    public BigDecimal getBudget(String category) {
        return budgets.getOrDefault(category, BigDecimal.ZERO);
    }

    /**
     * Возвращает копию карты всех бюджетов.
     *
     * @return Карта всех бюджетов (Map<String, BigDecimal>).
     */
    public Map<String, BigDecimal> getBudgets() {
        return new HashMap<>(budgets);
    }

    /**
     * Возвращает информацию о потраченном бюджете для определенной категории.
     *
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
     * Возвращает оставшийся бюджет для указанной категории.
     *
     * @param category Категория для вычисления оставшегося бюджета.
     * @return Оставшийся бюджет для категории (BigDecimal).
     */
    public BigDecimal getBudgetRemain(String category) {
        BigDecimal budget = getBudget(category);
        BigDecimal spent = getBudgetSpent(category);
        return budget.subtract(spent);
    }
}