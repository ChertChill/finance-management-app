package ui;

import services.*;
import models.*;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Класс для обработки команд в консольном интерфейсе приложения.
 * Обрабатывает команды пользователя и выполняет соответствующие действия.
 */
public class CommandProcessor {
    private AuthService authService;
    private FinanceService financeService;
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
                        case "register":
                            if (parts.length != 3) {
                                if (parts.length < 3) {
                                    System.out.println("Ошибка: Укажите имя пользователя и пароль. Используйте: register <username> <password>");
                                } else {
                                    System.out.println("Ошибка: Слишком много аргументов. Используйте: register <username> <password>");
                                }
                            } else {
                                if (authService.register(parts[1], parts[2])) {
                                    System.out.println("Пользователь зарегистрирован успешно.");
                                } else {
                                    System.out.println("Пользователь с таким именем уже существует.");
                                }
                            }
                            break;

                        case "login":
                            if (parts.length != 3) {
                                if (parts.length < 3) {
                                    System.out.println("Ошибка: Укажите имя пользователя и пароль. Используйте: login <username> <password>");
                                } else {
                                    System.out.println("Ошибка: Слишком много аргументов. Используйте: login <username> <password>");
                                }
                            } else {
                                currentUser = authService.login(parts[1], parts[2]);
                                if (currentUser != null) {
                                    System.out.println("Вход выполнен успешно.");
                                } else {
                                    System.out.println("Неверные учетные данные.");
                                }
                            }
                            break;

                        case "help":
                            System.out.println("=====================================================================");
                            System.out.println("Доступные команды: ");
                            System.out.println("register <username> <password> - Зарегистрировать нового пользователя");
                            System.out.println("login <username> <password> - Войти в систему");
                            System.out.println("add-income <amount> <category> - Добавить доход");
                            System.out.println("add-expense <amount> <category> - Добавить расход");
                            System.out.println("set-budget <category> <amount> - Установить бюджет для категории");
                            System.out.println("show-overview - Показать обзор кошелька");
                            System.out.println("    show-balance - Показать текущий баланс");
                            System.out.println("    show-summary - Показать общую сумму доходов и расходов");
                            System.out.println("    show-budget - Показать обзор бюджета");
                            System.out.println("    show-transactions - Показать список всех операций");
                            System.out.println("    show-category-budget <category> - Показать обзор конкретной категории бюджета");
                            System.out.println("    show-category-transactions <category> - Показать список всех операций конкретной категории бюджета");
                            System.out.println("logout - Выйти из учетной записи");
                            System.out.println("help - Показать доступные команды");
                            System.out.println("exit - Выйти из приложения");
                            break;

                        case "add-income":
                            if (isUserLoggedIn()) {
                                if (parts.length != 3) {
                                    if (parts.length < 3) {
                                        System.out.println("Ошибка: Укажите сумму и категорию дохода. Используйте: add-income <amount> <category>");
                                    } else {
                                        System.out.println("Ошибка: Слишком много аргументов. Используйте: add-income <amount> <category>");
                                    }
                                } else {
                                    try {
                                        BigDecimal amount = new BigDecimal(parts[1]);
                                        String category = parts[2];
                                        financeService.addIncome(currentUser, amount, category);
                                        System.out.println("Доход добавлен.");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Ошибка: Неверный формат суммы. Укажите число, например: 150.00");
                                    }
                                }
                            }
                            break;

                        case "add-expense":
                            if (isUserLoggedIn()) {
                                if (parts.length != 3) {
                                    if (parts.length < 3) {
                                        System.out.println("Ошибка: Укажите сумму и категорию расхода. Используйте: add-expense <amount> <category>");
                                    } else {
                                        System.out.println("Ошибка: Слишком много аргументов. Используйте: add-expense <amount> <category>");
                                    }
                                } else {
                                    try {
                                        BigDecimal amount = new BigDecimal(parts[1]);
                                        String category = parts[2];
                                        financeService.addExpense(currentUser, amount, category);
                                        System.out.println("Расход добавлен.");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Ошибка: Неверный формат суммы. Укажите число, например: 200.50");
                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Ошибка: " + e.getMessage());
                                    }
                                }
                            }
                            break;

                        case "set-budget":
                            if (isUserLoggedIn()) {
                                if (parts.length != 3) {
                                    if (parts.length < 3) {
                                        System.out.println("Ошибка: Укажите категорию и сумму бюджета. Используйте: set-budget <category> <amount>");
                                    } else {
                                        System.out.println("Ошибка: Слишком много аргументов. Используйте: set-budget <category> <amount>");
                                    }
                                } else {
                                    try {
                                        BigDecimal amount = new BigDecimal(parts[2]);
                                        String category = parts[1];
                                        financeService.setBudget(currentUser, category, amount);
                                        System.out.println("Бюджет установлен для категории \"" + category + "\".");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Ошибка: Неверный формат суммы. Укажите число, например: 500.00");
                                    }
                                }
                            }
                            break;

                        case "show-overview":
                            if (isUserLoggedIn()) {
                                if (parts.length != 1) {
                                    System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: show-overview");
                                } else {
                                    System.out.println(financeService.getOverview(currentUser));
                                }
                            }
                            break;

                        case "show-balance":
                            if (isUserLoggedIn()) {
                                if (parts.length != 1) {
                                    System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: show-balance");
                                } else {
                                    System.out.println(financeService.getBalance(currentUser));
                                }
                            }
                            break;

                        case "show-summary":
                            if (isUserLoggedIn()) {
                                if (parts.length != 1) {
                                    System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: show-summary.");
                                } else {
                                    System.out.println(financeService.getSummary(currentUser));
                                }
                            }
                            break;

                        case "show-budget":
                            if (isUserLoggedIn()) {
                                if (parts.length != 1) {
                                    System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: show-budget.");
                                } else {
                                    System.out.println(financeService.getBudget(currentUser));
                                }
                            }
                            break;

                        case "show-transactions":
                            if (isUserLoggedIn()) {
                                if (parts.length != 1) {
                                    System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: show-transactions.");
                                } else {
                                    System.out.println(financeService.getAllTransactions(currentUser));
                                }
                            }
                            break;

                        case "show-category-budget":
                            if (isUserLoggedIn()) {
                                if (parts.length != 2) {
                                    if (parts.length < 2) {
                                        System.out.println("Ошибка: Укажите название категории. Используйте: show-category-budget <category>");
                                    } else {
                                        System.out.println("Ошибка: Слишком много аргументов. Используйте: show-category-budget <category>");
                                    }
                                } else {
                                    String category = parts[1];
                                    System.out.println(financeService.getCategoryBudget(currentUser, category));
                                }
                            }
                            break;

                        case "show-category-transactions":
                            if (isUserLoggedIn()) {
                                if (parts.length != 2) {
                                    if (parts.length < 2) {
                                        System.out.println("Ошибка: Укажите название категории. Используйте: show-category-transactions <category>");
                                    } else {
                                        System.out.println("Ошибка: Слишком много аргументов. Используйте: show-category-transactions <category>");
                                    }
                                } else {
                                    String category = parts[1];
                                    System.out.println(financeService.getCategoryTransactions(currentUser, category));
                                }
                            }
                            break;


                        case "logout":
                            if (parts.length != 1) {
                                System.out.println("Ошибка: Команда не должна содержать аргументы. Используйте: logout");
                            } else {
                                currentUser = null;
                                System.out.println("Вы вышли из системы.");
                            }
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
     * @return true, если пользователь залогинен, иначе false.
     */
    private boolean isUserLoggedIn() {
        if (currentUser == null) {
            System.out.println("Пожалуйста, войдите в систему.");
            return false;
        }
        return true;
    }
}