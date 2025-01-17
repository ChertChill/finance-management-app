package ui;

import services.*;
import models.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Класс для обработки команд в консольном интерфейсе приложения.
 * Обрабатывает команды пользователя и выполняет соответствующие действия.
 */
public class CommandProcessor {
    private final AuthService authService;
    private final FinanceService financeService;
    private User currentUser;

    public CommandProcessor() {
        this.authService = new AuthService();
        this.financeService = new FinanceService();
    }

    /**
     * Запускает процесс обработки команд от пользователя.
     * Включает регистрацию, вход, добавление доходов/расходов, установка бюджета и отображение информации.
     */
    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Введите команду ('help' - просмотр списка команд):");
                System.out.print(">> ");
                String command = scanner.nextLine();
                String[] parts = command.split(" ");
                try {
                    switch (parts[0]) {
                        case "help":
                            System.out.println("=====================================================================");
                            System.out.println("Доступные команды: ");
                            System.out.println("help - Показать доступные команды");
                            System.out.println("register <username> <password> - Зарегистрировать нового пользователя");
                            System.out.println("login <username> <password> - Войти в систему");
                            System.out.println("logout - Выйти из учетной записи");
                            System.out.println("exit - Выйти из приложения");
                            System.out.println("\nКоманды для работы с кошельком:");
                            System.out.println("-------------------------------");
                            System.out.println("add-income <amount> <category> - Добавить доход");
                            System.out.println("add-expense <amount> <category> - Добавить расход");
                            System.out.println("set-budget <category> <amount> - Установить бюджет для категории");
                            System.out.println("add-transfer <recipientUsername> <amount> - Отправить перевод другому пользователю");
                            System.out.println("\nКоманды для вывода общей информации:");
                            System.out.println("------------------------------------");
                            System.out.println("show-overview - Показать обзор кошелька");
                            System.out.println("    show-balance - Показать текущий баланс");
                            System.out.println("    show-summary - Показать общую сумму доходов и расходов");
                            System.out.println("    show-budget - Показать обзор бюджета");
                            System.out.println("    show-transactions - Показать список всех операций");
                            System.out.println("    show-category-budget <category1> [category2] ... - Показать обзор бюджета по выбранным категориям");
                            System.out.println("    show-category-transactions <category1> [category2] ... - Показать список всех операций по выбранным категориям");
                            System.out.println("\nКоманды для вывода информации по доходам:");
                            System.out.println("-----------------------------------------");
                            System.out.println("show-overview-income - Показать обзор кошелька по доходам");
                            System.out.println("    show-summary-income - Показать общую сумму доходов");
                            System.out.println("    show-budget-income - Показать обзор бюджета по доходам");
                            System.out.println("    show-transactions-income - Показать список всех операций по доходам");
                            System.out.println("\nКоманды для вывода информации по расходам:");
                            System.out.println("------------------------------------------");
                            System.out.println("show-overview-expense - Показать обзор кошелька по расходам");
                            System.out.println("    show-summary-expense - Показать общую сумму расходов");
                            System.out.println("    show-budget-expense - Показать обзор бюджета по расходам");
                            System.out.println("    show-transactions-expense - Показать список всех операций по расходам");
                            break;

                        case "register":
                            validateAndExecute(parts, "couple", "register <username> <password>", () -> {
                                if (authService.register(parts[1], parts[2])) {
                                    System.out.println("Пользователь зарегистрирован успешно.");
                                } else {
                                    System.out.println("Пользователь с таким именем уже существует.");
                                }
                            });
                            break;

                        case "login":
                            validateAndExecute(parts, "couple", "login <username> <password>", () -> {
                                currentUser = authService.login(parts[1], parts[2]);
                                if (currentUser != null) {
                                    System.out.println("Вход выполнен успешно.");
                                } else {
                                    System.out.println("Неверные учетные данные.");
                                }
                            });
                            break;

                        case "logout":
                            validateAndExecute(parts, "single", "logout", () -> {
                                currentUser = null;
                                System.out.println("Вы вышли из системы.");
                            });
                            break;

                        case "exit":
                            if (parts.length != 1) {
                                System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: exit");
                            } else {
                                authService.saveUsers();
                                System.out.println("До свидания!");
                                return;
                            }
                            break;

                        /**
                         * Команды для работы с кошельком
                         */

                        case "add-income":
                            validateAndExecute(parts, "couple-wallet", "add-income <category> <amount>", () -> {
                                try {
                                    String category = parts[1];
                                    BigDecimal amount = new BigDecimal(parts[2]);
                                    financeService.addIncome(currentUser, category, amount);
                                    System.out.println("Доход добавлен.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка: Неверный формат введенного значения. Укажите число, например: 150.00");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Ошибка: " + e.getMessage());
                                }
                            });
                            break;

                        case "add-expense":
                            validateAndExecute(parts, "couple-wallet", "add-expense <category> <amount>", () -> {
                                try {
                                    String category = parts[1];
                                    BigDecimal amount = new BigDecimal(parts[2]);
                                    financeService.addExpense(currentUser, category, amount);
                                    System.out.println("Расход добавлен.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка: Неверный формат введенного значения. Укажите число, например: 200.50");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Ошибка: " + e.getMessage());
                                }
                            });
                            break;

                        case "set-budget":
                            validateAndExecute(parts, "couple-wallet", "set-budget <category> <amount>", () -> {
                                try {
                                    String category = parts[1];
                                    BigDecimal amount = new BigDecimal(parts[2]);
                                    financeService.setBudget(currentUser, category, amount);
                                    System.out.println("Бюджет установлен для категории \"" + category + "\".");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка: Неверный формат введенного значения. Укажите число, например: 500.00");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Ошибка: " + e.getMessage());
                                }
                            });
                            break;

                        case "add-transfer":
                            validateAndExecute(parts, "transfer", "transfer <recipientUsername> <amount>", () -> {
                                try {
                                    String recipientUsername = parts[1];
                                    BigDecimal amount = new BigDecimal(parts[2]);

                                    if (financeService.addTransfer(currentUser, recipientUsername, amount, authService)) {
                                        System.out.println("Перевод успешно выполнен.");
                                    } else {
                                        System.out.println("Ошибка: Не удается выполнить перевод. Проверьте наличие средств и правильность данных.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка: Неверный формат суммы. Используйте десятичное число, например: 150.00");
                                }
                            });
                            break;

                        /**
                         * Команды для вывода общей информации
                         */

                        case "show-overview":
                            validateAndExecute(parts, "single", "show-overview",
                                    () -> System.out.println(financeService.getOverview(currentUser)));
                            break;

                        case "show-balance":
                            validateAndExecute(parts, "single", "show-balance",
                                    () -> System.out.println(financeService.getBalance(currentUser)));
                            break;

                        case "show-summary":
                            validateAndExecute(parts, "single", "show-summary",
                                    () -> System.out.println(financeService.getSummary(currentUser)));
                            break;

                        case "show-budget":
                            validateAndExecute(parts, "single", "show-budget",
                                    () -> System.out.println(financeService.getBudget(currentUser)));
                            break;

                        case "show-transactions":
                            validateAndExecute(parts, "single", "show-transactions",
                                    () -> System.out.println(financeService.getAllTransactions(currentUser)));
                            break;

                        case "show-category-budget":
                            validateAndExecute(parts, "multiply", "show-category-budget <category1> [category2] ...", () -> {
                                // Получаем категории из аргументов
                                List<String> categories = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
                                String result = financeService.getCategoryBudget(currentUser, categories);
                                System.out.println(result);
                            });
                            break;

                        case "show-category-transactions":
                            validateAndExecute(parts, "multiply", "show-category-transactions <category1> [category2] ...", () -> {
                                // Получаем категории из аргументов
                                List<String> categories = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
                                String result = financeService.getCategoryTransactions(currentUser, categories);
                                System.out.println(result);
                            });
                            break;

                        /**
                         * Команды для вывода информации по доходам
                         */

                        case "show-overview-income":
                            validateAndExecute(parts, "single", "show-overview-income",
                                    () -> System.out.println(financeService.getIncomeOverview(currentUser)));
                            break;

                        case "show-summary-income":
                            validateAndExecute(parts, "single", "show-summary-income",
                                    () -> System.out.println(financeService.getIncomeSummary(currentUser)));
                            break;

                        case "show-budget-income":
                            validateAndExecute(parts, "single", "show-budget-income",
                                    () -> System.out.println(financeService.getIncomeBudget(currentUser)));
                            break;

                        case "show-transactions-income":
                            validateAndExecute(parts, "single", "show-transactions-income",
                                    () -> System.out.println(financeService.getIncomeTransactions(currentUser)));
                            break;

                        /**
                         * Команды для вывода информации по расходам
                         */

                        case "show-overview-expense":
                            validateAndExecute(parts, "single", "show-overview-expense",
                                    () -> System.out.println(financeService.getExpenseOverview(currentUser)));
                            break;

                        case "show-summary-expense":
                            validateAndExecute(parts, "single", "show-summary-expense",
                                    () -> System.out.println(financeService.getExpenseSummary(currentUser)));
                            break;

                        case "show-budget-expense":
                            validateAndExecute(parts, "single", "show-budget-expense",
                                    () -> System.out.println(financeService.getExpenseBudget(currentUser)));
                            break;

                        case "show-transactions-expense":
                            validateAndExecute(parts, "single", "show-transactions-expense",
                                    () -> System.out.println(financeService.getExpenseTransactions(currentUser)));
                            break;

                        default:
                            System.out.println("Неизвестная команда. Введите 'help' для вывода списка команд.");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Проверяет, залогинен ли пользователь.
     * Если нет, выводит сообщение.
     *
     * @return true, если пользователь залогинен, иначе false.
     */
    private boolean isUserLoggedIn() {
        if (currentUser == null) {
            System.out.println("Пожалуйста, войдите в систему.");
            return false;
        }
        return true;
    }

    /**
     * Вспомогательный метод для проверки и выполнения команды.
     * Проводит валидацию на основе типов аргументов, затем выполняет переданное действие.
     *
     * @param parts Введенные части команды.
     * @param expectedArgs Ожидаемое количество аргументов.
     * @param usage Правильный формат команды.
     * @param action Действие, выполняемое, если проверка пройдена.
     */
    private void validateAndExecute(String[] parts, String expectedArgs, String usage, Runnable action) {
        switch (expectedArgs) {
            case "single":
                // Случай, когда ожидается один аргумент
                if (isUserLoggedIn()) {
                    if (parts.length != 1) {
                        System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: " + usage);
                    } else action.run();
                }
                break;

            case "multiply":
                // Случай, когда ожидается несколько аргументов (один или более)
                if (isUserLoggedIn()) {
                    if (parts.length < 2) {
                        System.out.println("Ошибка: Укажите хотя бы одну категорию. Используйте: " + usage);
                    } else action.run();
                }
                break;

            case "couple":
                // Случай, когда ожидаются два аргумента: имя пользователя и пароль
                if (parts.length != 3) {
                    if (parts.length < 3) {
                        System.out.println("Ошибка: Укажите имя пользователя и пароль. Используйте: " + usage);
                    } else {
                        System.out.println("Ошибка: Слишком много аргументов. Используйте: " + usage);
                    }
                } else action.run();
                break;

            case "couple-wallet":
                // Случай для команд с аргументами категории и суммы (для работы с кошельком)
                if (isUserLoggedIn()) {
                    if (parts.length != 3) {
                        if (parts.length < 3) {
                            System.out.println("Ошибка: Укажите значения категории и суммы. Используйте: " + usage);
                        } else {
                            System.out.println("Ошибка: Слишком много аргументов. Используйте: " + usage);
                        }
                    } else action.run();
                }
                break;

            case "transfer":
                // Случай для команд с аргументами логин получателя и сумма (для перевода другому пользователю)
                if (isUserLoggedIn()) {
                    if (parts.length != 3) {
                        System.out.println("Ошибка: Команда должна содержать логин получателя и сумму. Используйте: " + usage);
                    } else action.run();
                }
                break;

            default:
                System.out.println("Невозможно проверить и исполнить команду. Выберите одну из модификаций - single, multiply, couple.");
        }
    }

}