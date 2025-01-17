package services;

import models.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для управления финансовыми операциями пользователей.
 * Включает добавление дохода, расхода, установку бюджета и получение статистики.
 */
public class FinanceService {

    /**
     * Добавляет доход в кошелек пользователя.
     *
     * @param user Пользователь, чей доход будет добавлен.
     * @param amount Сумма дохода.
     * @param category Категория дохода.
     */
    public void addIncome(User user, BigDecimal amount, String category) {
        user.getWallet().addIncome(amount, category);
    }

    /**
     * Добавляет расход в кошелек пользователя.
     *
     * @param user Пользователь, чей расход будет добавлен.
     * @param amount Сумма расхода.
     * @param category Категория расхода.
     */
    public void addExpense(User user, BigDecimal amount, String category) {
        user.getWallet().addExpense(amount, category);
    }

    /**
     * Устанавливает бюджет для категории.
     *
     * @param user Пользователь, для которого устанавливается бюджет.
     * @param category Категория, для которой устанавливается бюджет.
     * @param amount Сумма бюджета.
     */
    public void setBudget(User user, String category, BigDecimal amount) {
        user.getWallet().setBudget(category, amount);
    }

    /**
     * Возвращает полную информацию о финансах пользователя.
     * Включает баланс, суммы доходов/расходов и список операций.
     *
     * @param user Пользователь, для которого формируется отчет.
     * @return Строка с полной информацией о финансах пользователя.
     */
    public String getOverview(User user) {
//        Wallet wallet = user.getWallet();
//
//        // Баланс
//        BigDecimal balance = wallet.getBalance();
//
//        // Общая сумма доходов
//        BigDecimal totalIncome = wallet.getTransactions().stream()
//                .filter(t -> t.getType() == TransactionType.INCOME)
//                .map(Transaction::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Общая сумма расходов
//        BigDecimal totalExpenses = wallet.getTransactions().stream()
//                .filter(t -> t.getType() == TransactionType.EXPENSE)
//                .map(Transaction::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Список операций
//        StringBuilder transactionsList = new StringBuilder("Список операций:\n");
//        for (Transaction t : wallet.getTransactions()) {
//            transactionsList.append(t.getDate())
//                    .append(" - ")
//                    .append(t.getType() == TransactionType.INCOME ? "Доход: +" : "Расход: -")
//                    .append(t.getAmount())
//                    .append(" (Категория: ")
//                    .append(t.getCategory())
//                    .append(")\n");
//        }

        String balance = getBalance(user);

        String summaryIncome = getIncomeSummary(user);
        String budgetIncome = getIncomeBudget(user);
        String transactionsIncome = getIncomeTransactions(user);

        String summaryExpense = getExpenseSummary(user);
        String budgetExpense = getExpenseBudget(user);
        String transactionsExpense = getExpenseTransactions(user);


        return String.format(
                "%s\n---------------\n\nДОХОДЫ\n======\n%s\n\n%s\n\n%s\n\nРАСХОДЫ\n=======\n%s\n\n%s\n\n%s",
                balance,
                summaryIncome, budgetIncome, transactionsIncome,
                summaryExpense, budgetExpense, transactionsExpense
        );
    }

    /**
     * Возвращает текущий баланс пользователя.
     *
     * @param user Пользователь.
     * @return Баланс пользователя.
     */
    public String getBalance(User user) {
        Wallet wallet = user.getWallet();
        return "Текущий баланс: " + wallet.getBalance();
    }

    /**
     * Возвращает общую сумму доходов и расходов.
     *
     * @param user Пользователь.
     * @return Сводка с общей суммой доходов и расходов.
     */
    public String getSummary(User user) {
        return String.format(
                "%s\n%s", getIncomeSummary(user), getExpenseSummary(user)
        );
    }

    // Возвращает общую сумму доходов
    public String getIncomeSummary(User user) {
        return getSummaryByType(user, TransactionType.INCOME);
    }

    // Возвращает общую сумму расходов
    public String getExpenseSummary(User user) {
        return getSummaryByType(user, TransactionType.EXPENSE);
    }

    // Возвращает общую сумму по типу транзакции
    public String getSummaryByType(User user, TransactionType type) {
        Wallet wallet = user.getWallet();
        String label = type == TransactionType.INCOME ? "доходов" : "расходов";

        BigDecimal totalExpenses = wallet.getTransactions().stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format(
                "Общая сумма %s: %s", label, totalExpenses
        );
    }

    /**
     * Получает обзор всех бюджетов пользователя.
     * Включает статус бюджета для каждой категории.
     *
     * @param user Пользователь, для которого получаем обзор бюджета.
     * @return Строка с бюджетным обзором для всех категорий.
     */
    public String getBudget(User user) {
//        Wallet wallet = user.getWallet();
//        StringBuilder overview = new StringBuilder();
//
//        // Собираем категории из бюджета и транзакций
//        Set<String> categories = new HashSet<>(wallet.getBudgets().keySet());
//        wallet.getTransactions().stream()
//                .map(Transaction::getCategory)
//                .forEach(categories::add);
//
//        // Проверяем, есть ли категории
//        if (categories.isEmpty()) {
//            overview.append("Нет доступных категорий для бюджета.");
//        } else {
//            // Обрабатываем каждую категорию
//            overview.append("Бюджет по всем категориям:\n");
//            overview.append("--------------------------");
//            for (String category : categories) {
//                BigDecimal budget = wallet.getBudget(category);
//                BigDecimal spent = wallet.getBudgetSpent(category);
//                BigDecimal remain = wallet.getBudgetRemain(category);
//
//                overview.append(
//                        String.format(
//                                "\nКатегория: %s\n----------\nБюджет: %s, потрачено: %s\nОстаток бюджета: %s\n",
//                                category, budget, spent, remain
//                        )
//                );
//            }
//        }
//
//        String str = overview.toString();
//        return str.substring(0, str.length() - 1);

        return String.format(
                "%s\n\n%s", getIncomeBudget(user), getExpenseBudget(user)
        );
    }

    /**
     * Получает обзор бюджета по доходам.
     * Включает статус бюджета для каждой категории, связанных с доходами.
     *
     * @param user Пользователь, для которого получаем обзор бюджета.
     * @return Строка с бюджетным обзором для категорий доходов.
     */
    public String getIncomeBudget(User user) {
        return getBudgetByType(user, TransactionType.INCOME);
    }

    /**
     * Получает обзор бюджета по расходам.
     * Включает статус бюджета для каждой категории, связанных с расходами.
     *
     * @param user Пользователь, для которого получаем обзор бюджета.
     * @return Строка с бюджетным обзором для категорий расходов.
     */
    public String getExpenseBudget(User user) {
        return getBudgetByType(user, TransactionType.EXPENSE);
    }

    /**
     * Универсальный метод для получения бюджета по типу транзакций.
     *
     * @param user Пользователь, для которого получаем данные.
     * @param type Тип транзакции (доходы или расходы).
     * @return Строка с бюджетным обзором для заданного типа транзакций.
     */
    public String getBudgetByType(User user, TransactionType type) {
        Wallet wallet = user.getWallet();
        StringBuilder overview = new StringBuilder();

        String label = type == TransactionType.INCOME ? "доходов, Доходы" : "расходов, Бюджет";
        String[] labels = label.split(", ");

//        // Собираем категории из бюджета и транзакций
//        Set<String> categories = new HashSet<>(wallet.getBudgets().keySet());
//        wallet.getTransactions().stream()
//                .filter(t -> t.getType() == type)
//                .map(Transaction::getCategory)
//                .forEach(categories::add);

        // Собираем категории, связанные с указанным типом транзакции
        Set<String> categories = wallet.getTransactions().stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getCategory)
                .collect(Collectors.toCollection(TreeSet::new)); // TreeSet для сортировки по алфавиту

        // Проверяем, есть ли категории
        if (categories.isEmpty()) {
            overview.append("Нет доступных категорий для ").append(labels[0]).append(".");
        } else {
            // Обрабатываем каждую категорию
            overview.append(labels[1]).append(" по всем категориям:\n");
            overview.append("--------------------------");
            for (String category : categories) {
                if (type == TransactionType.INCOME) {
                    // Сумма доходов по категории
                    BigDecimal income = wallet.getTransactions().stream()
                            .filter(t -> t.getType() == TransactionType.INCOME && t.getCategory().equals(category))
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    overview.append(
                            String.format(
                                    "\nКатегория: %s\n----------\nДоход: %s\n",
                                    category, income
                            )
                    );
                } else {
                    BigDecimal budget = wallet.getBudget(category);
                    BigDecimal spent = wallet.getBudgetSpent(category);
                    BigDecimal remain = wallet.getBudgetRemain(category);

                    overview.append(
                            String.format(
                                    "\nКатегория: %s\n----------\nБюджет: %s, потрачено: %s\nОстаток бюджета: %s\n",
                                    category, budget, spent, remain
                            )
                    );
                }
            }
        }

        String str = overview.toString();
        return str.substring(0, str.length() - 1);
    }

    /**
     * Возвращает список всех транзакций пользователя.
     *
     * @param user Пользователь.
     * @return Строка со списком всех транзакций.
     */
    public String getAllTransactions(User user) {
        Wallet wallet = user.getWallet();
        StringBuilder transactionsList = new StringBuilder();

        List<Transaction> transactions = wallet.getTransactions();

        if (transactions.isEmpty()) {
            return "Операций не найдено.";
        }

        transactionsList.append("Список всех операций:\n");
        transactionsList.append("---------------------\n");
        for (Transaction t : transactions) {
            transactionsList.append(
                    String.format(
                            "%s - %s: %s (Категория: %s)\n",
                            t.getDate(),
                            t.getType() == TransactionType.INCOME ? "Доход" : "Расход",
                            t.getAmount(),
                            t.getCategory()
                    )
            );
        }

        String str = transactionsList.toString();
        return str.substring(0, str.length() - 1);

//        return String.format(
//                "Список всех операций:\n---------------------\n%s\n\n%s",
//                getIncomeTransactions(user), getExpenseTransactions(user)
//        );
    }

    public String getIncomeTransactions(User user) {
        return getTransactionsByType(user, TransactionType.INCOME);
    }

    public String getExpenseTransactions(User user) {
        return getTransactionsByType(user, TransactionType.EXPENSE);
    }

    /**
     * Возвращает список транзакций пользователя указанного типа.
     *
     * @param user Пользователь.
     * @param type Тип транзакции (INCOME или EXPENSE).
     * @return Строка со списком транзакций указанного типа.
     */
    public String getTransactionsByType(User user, TransactionType type) {
        Wallet wallet = user.getWallet();
        StringBuilder transactionsList = new StringBuilder();

        String label = type == TransactionType.INCOME ? "доход" : "расход";

        List<Transaction> filteredTransactions = wallet.getTransactions().stream()
                .filter(t -> t.getType() == type)
                .toList();

        if (filteredTransactions.isEmpty()) {
            return String.format("Операций %sа не найдено.", label);
        }

        transactionsList.append(String.format("Список всех %sов:\n", label));
        transactionsList.append("---------------------\n");

        for (Transaction t : filteredTransactions) {
            transactionsList.append(
                    String.format(
                            "%s - %s: %s (Категория: %s)\n",
                            t.getDate(),
                            type == TransactionType.INCOME ? "Доход" : "Расход",
                            t.getAmount(),
                            t.getCategory()
                    )
            );
        }

        return transactionsList.toString().trim();
    }


    /**
     * Получает информацию о состоянии бюджета и оставшемся лимите для конкретной категории.
     *
     * @param user Пользователь, для которого получаем данные.
     * @param category Название категории.
     * @return Строка с состоянием бюджета и оставшимся лимитом для категории, или сообщение, что категории нет.
     */
    public String getCategoryBudget(User user, String category) {
        Wallet wallet = user.getWallet();

        // Проверяем, существует ли категория
        boolean categoryExists = wallet.getBudgets().containsKey(category) || wallet.getTransactions().stream()
                .anyMatch(t -> t.getCategory().equals(category));

        if (!categoryExists) {
            return "Бюджет для категории \"" + category + "\" отсутствует.";
        }

        // Получаем информацию о бюджете для категории
        BigDecimal budget = wallet.getBudget(category);
        BigDecimal spent = wallet.getBudgetSpent(category);
        BigDecimal remain = wallet.getBudgetRemain(category);

        String str = String.format(
            "Бюджет для категории: %s\n---------------------\nБюджет: %s, потрачено: %s\nОстаток бюджета: %s\n",
            category, budget, spent, remain
        );

        return str.substring(0, str.length() - 1);
    }

    /**
     * Получает список транзакций по заданной категории.
     *
     * @param user Пользователь, для которого запрашиваются транзакции.
     * @param category Категория, по которой ищутся транзакции.
     * @return Строка с описанием транзакций или сообщение, что транзакций нет.
     */
    public String getCategoryTransactions(User user, String category) {
        Wallet wallet = user.getWallet();

        // Фильтруем транзакции по заданной категории
        List<Transaction> transactions = wallet.getTransactionsByCategory(category);

        // Проверяем, есть ли транзакции
        if (transactions.isEmpty()) {
            return "Транзакции для категории \"" + category + "\" отсутствуют.";
        }

        // Формируем строку с транзакциями
        StringBuilder result = new StringBuilder("Транзакции для категории: " + category + "\n");
        result.append("-------------------------\n");
        for (Transaction t : transactions) {
            result.append(String.format(
                    "%s - %s: %s\n",
                    t.getDate(),
                    t.getType() == TransactionType.INCOME ? "Доход" : "Расход",
                    t.getAmount()
            ));
        }

        String str = result.toString();
        return str.substring(0, str.length() - 1);
    }

}