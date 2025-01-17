package services;

import models.*;
import java.math.BigDecimal;
import java.util.*;
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
    public void addIncome(User user, String category, BigDecimal amount) {
        user.getWallet().addIncome(category, amount);
    }

    /**
     * Добавляет расход в кошелек пользователя.
     *
     * @param user Пользователь, чей расход будет добавлен.
     * @param amount Сумма расхода.
     * @param category Категория расхода.
     */
    public void addExpense(User user, String category, BigDecimal amount) {
        user.getWallet().addExpense(category, amount);
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
     * Метод для перевода средств между пользователями.
     * Осуществляет перевод средств от одного пользователя к другому.
     * Фиксирует расход у отправителя и доход у получателя.
     *
     * @param sender Отправитель перевода (пользователь, который переводит средства)
     * @param recipientUsername Логин получателя перевода
     * @param amount Сумма перевода
     * @param authService Сервис для получения списка всех пользователей
     * @return true, если перевод успешен, иначе false
     */
    public boolean addTransfer(User sender, String recipientUsername, BigDecimal amount, AuthService authService) {
        // Проверяем, что не переводим сами себе
        if (sender.getUsername().equals(recipientUsername)) {
            throw new IllegalArgumentException("В качестве получателя указан текущий пользователь. Нельзя отправить перевод самому себе.");
        }

        // Проверяем, что сумма перевода положительная
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Вводимое значение должно быть положительным числом.");
        }

        // Проверяем, что у отправителя есть достаточно средств
        if (sender.getWallet().getBalance().compareTo(amount) < 0) {
            System.out.println("Ошибка: У вас недостаточно средств для перевода.");
            return false;
        }

        // Находим получателя через AuthService
        User recipient = authService.getAllUsers().get(recipientUsername);
        if (recipient == null) {
            System.out.println("Ошибка: Получатель с таким логином не найден.");
            return false;
        }

        // Выполняем перевод: уменьшение баланса отправителя и увеличение баланса получателя
        try {
            addExpense(sender, String.format("Перевод средств к %s", recipient.getUsername()), amount);  // Фиксируем расход у отправителя
            addIncome(recipient, String.format("Перевод средств от %s", sender.getUsername()), amount); // Фиксируем доход у получателя
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении перевода: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает полную информацию о финансах пользователя.
     * Включает баланс, суммы доходов/расходов и список операций.
     *
     * @param user Пользователь, для которого формируется отчет.
     * @return Строка с полной информацией о финансах пользователя.
     */
    public String getOverview(User user) {
        String balance = getBalance(user);
        String overviewIncome = getIncomeOverview(user);
        String overviewExpense = getExpenseOverview(user);

        return String.format(
                "%s\n---------------\n%s\n%s",
                balance, overviewIncome, overviewExpense
        );
    }

    /**
     * Формирует отчет по доходам пользователя.
     * Включает сводку по доходам, бюджетам и транзакциям.
     *
     * @param user Пользователь.
     * @return Строка с обзором доходов.
     */
    public String getIncomeOverview(User user) {
        String summaryIncome = getIncomeSummary(user);
        String budgetIncome = getIncomeBudget(user);
        String transactionsIncome = getIncomeTransactions(user);

        return String.format(
                "\nДОХОДЫ\n======\n%s\n\n%s\n\n%s",
                summaryIncome, budgetIncome, transactionsIncome
        );
    }

    /**
     * Формирует отчет по расходам пользователя.
     * Включает сводку по расходам, бюджетам и транзакциям.
     *
     * @param user Пользователь.
     * @return Строка с обзором расходов.
     */
    public String getExpenseOverview(User user) {
        String summaryExpense = getExpenseSummary(user);
        String budgetExpense = getExpenseBudget(user);
        String transactionsExpense = getExpenseTransactions(user);

        return String.format(
                "\nРАСХОДЫ\n=======\n%s\n\n%s\n\n%s",
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

    /**
     * Возвращает общую сумму доходов пользователя.
     *
     * @param user Пользователь.
     * @return Сводка по доходам.
     */
    public String getIncomeSummary(User user) {
        return getSummaryByType(user, TransactionType.INCOME);
    }

    /**
     * Возвращает общую сумму расходов пользователя.
     *
     * @param user Пользователь.
     * @return Сводка по расходам.
     */
    public String getExpenseSummary(User user) {
        return getSummaryByType(user, TransactionType.EXPENSE);
    }

    /**
     * Возвращает общую сумму по типу транзакции (доходы или расходы).
     *
     * @param user Пользователь.
     * @param type Тип транзакции (доходы или расходы).
     * @return Сводка по суммам доходов или расходов.
     */
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
     * Возвращает обзор всех бюджетов пользователя.
     * Включает статус бюджета для каждой категории (доходы и расходы).
     *
     * @param user Пользователь.
     * @return Строка с бюджетным обзором.
     */
    public String getBudget(User user) {
        return String.format(
                "%s\n\n%s", getIncomeBudget(user), getExpenseBudget(user)
        );
    }

    /**
     * Возвращает обзор бюджета по доходам.
     * Включает статус бюджета для каждой категории, связанных с доходами.
     *
     * @param user Пользователь, для которого получаем обзор бюджета.
     * @return Строка с бюджетным обзором для категорий доходов.
     */
    public String getIncomeBudget(User user) {
        return getBudgetByType(user, TransactionType.INCOME);
    }

    /**
     * Возвращает обзор бюджета по расходам.
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

        // Собираем категории, связанные с указанным типом транзакции
        Set<String> transactionCategories = wallet.getTransactions().stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getCategory)
                .collect(Collectors.toSet()); // Используем Set для категорий из транзакций

        // Собираем категории из бюджета, у которых нет транзакций, но есть лимит, и отображаем их только для expense
        Set<String> categoriesWithoutTransactions = new HashSet<>();
        if (type == TransactionType.EXPENSE) {
            categoriesWithoutTransactions = wallet.getBudgets().keySet().stream()
                    .filter(category -> wallet.getTransactions().stream()
                            .noneMatch(t -> t.getCategory().equals(category)))
                    .collect(Collectors.toSet()); // Фильтруем категории, у которых нет транзакций
        }

        // Объединяем категории с транзакциями и без транзакций для типа "расходы" (не для доходов)
        Set<String> allCategories = new HashSet<>(transactionCategories);
        allCategories.addAll(categoriesWithoutTransactions);  // Добавляем категории без транзакций только для расхода

        // Сортируем объединённые категории по алфавиту
        List<String> categories = new ArrayList<>(allCategories);
        Collections.sort(categories);

        // Проверяем, есть ли категории
        if (categories.isEmpty()) {
            overview.append("Нет доступных категорий для ").append(labels[0]).append(".");
        } else {
            // Обрабатываем каждую категорию
            overview.append(String.format("%s по всем категориям: %s\n", labels[1], categories));
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
    }

    /**
     * Возвращает список всех доходных транзакций пользователя.
     *
     * @param user Пользователь.
     * @return Строка со списком доходных транзакций.
     */
    public String getIncomeTransactions(User user) {
        return getTransactionsByType(user, TransactionType.INCOME);
    }

    /**
     * Возвращает список всех расходных транзакций пользователя.
     *
     * @param user Пользователь.
     * @return Строка со списком расходных транзакций.
     */
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
     * Получает информацию о состоянии бюджета и оставшемся лимите для указанных категорий.
     *
     * @param user Пользователь, для которого получаем данные.
     * @param categories Список категорий.
     * @return Строка с состоянием бюджета для указанных категорий.
     */
    public String getCategoryBudget(User user, List<String> categories) {
        Wallet wallet = user.getWallet();
        StringBuilder overview = new StringBuilder();

        if (categories.isEmpty()) {
            return "Не указаны категории.";
        }

        for (String category : categories) {
            // Проверяем, существует ли категория
            boolean categoryExists = wallet.getBudgets().containsKey(category) || wallet.getTransactions().stream()
                    .anyMatch(t -> t.getCategory().equals(category));

            if (!categoryExists) {
                overview.append(String.format("\nБюджет для категории \"%s\" отсутствует.\n", category));
                continue;
            }

            // Определяем тип категории на основе транзакций
            boolean isIncome = wallet.getTransactions().stream()
                    .anyMatch(t -> t.getCategory().equals(category) && t.getType() == TransactionType.INCOME);

            if (isIncome) {
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
                // Получаем информацию о бюджете для категории
                BigDecimal budget = wallet.getBudget(category);
                BigDecimal spent = wallet.getBudgetSpent(category);
                BigDecimal remain = wallet.getBudgetRemain(category);

                overview.append(
                        String.format(
                                "\nБюджет для категории: %s\n---------------------\nБюджет: %s, потрачено: %s\nОстаток бюджета: %s\n",
                                category, budget, spent, remain
                        )
                );
            }
        }

        return overview.toString().trim();
    }

    /**
     * Получает список транзакций для нескольких категорий с учетом типа транзакций.
     *
     * @param user Пользователь, для которого запрашиваются транзакции.
     * @param categories Список категорий.
     * @return Строка с описанием транзакций для указанных категорий.
     */
    public String getCategoryTransactions(User user, List<String> categories) {
        Wallet wallet = user.getWallet();
        StringBuilder transactionsList = new StringBuilder();

        if (categories.isEmpty()) {
            return "Не указаны категории.";
        }

        for (String category : categories) {
            // Фильтруем транзакции по категории
            List<Transaction> filteredTransactions = wallet.getTransactions().stream()
                    .filter(t -> t.getCategory().equals(category))
                    .toList();

            // Проверяем, есть ли транзакции
            if (filteredTransactions.isEmpty()) {
                transactionsList.append(String.format("\nТранзакции для категории \"%s\" отсутствуют.\n", category));
                continue;
            }

            transactionsList.append(String.format("\nТранзакции для категории: %s\n", category));
            transactionsList.append("-------------------------\n");

            for (Transaction t : filteredTransactions) {
                transactionsList.append(String.format(
                        "%s - %s: %s\n",
                        t.getDate(),
                        t.getType() == TransactionType.INCOME ? "Доход" : "Расход",
                        t.getAmount()
                ));
            }
        }

        return transactionsList.toString().trim();
    }

}