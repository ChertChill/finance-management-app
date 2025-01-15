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
    private NotificationService notificationService;
    private User currentUser;

    public CommandProcessor() {
        this.authService = new AuthService();
        this.financeService = new FinanceService();
        this.notificationService = new NotificationService();
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
                            if (authService.register(parts[1], parts[2])) {
                                System.out.println("Пользователь успешно зарегистрирован");
                            } else {
                                System.out.println("Пользователь уже существует.");
                            }
                            break;

                        case "login":
                            currentUser = authService.login(parts[1], parts[2]);
                            if (currentUser != null) {
                                System.out.println("Успешный вход в систему.");
                            } else {
                                System.out.println("Неверные данные учетной записи.");
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
                            System.out.println("show-balance - Показать текущий баланс");
                            System.out.println("show-overview - Показать обзор бюджета");
                            System.out.println("logout - Выйти из учетной записи");
                            System.out.println("help - Показать доступные команды");
                            System.out.println("exit - Выйти из приложения");
                            System.out.println("=====================================================================");
                            break;

                        case "add-income":
                            if (isUserLoggedIn()) {
                                financeService.addIncome(currentUser, new BigDecimal(parts[1]), parts[2]);
                                System.out.println("Доход добавлен.");
                            }
                            break;

                        case "add-expense":
                            if (isUserLoggedIn()) {
                                financeService.addExpense(currentUser, new BigDecimal(parts[1]), parts[2]);
                                System.out.println("Расход добавлен.");
                            }
                            break;

                        case "set-budget":
                            if (isUserLoggedIn()) {
                                financeService.setBudget(currentUser, parts[1], new BigDecimal(parts[2]));
                                System.out.println("Бюджет установлен.");
                            }
                            break;

                        case "show-balance":
                            if (isUserLoggedIn()) {
                                System.out.println("Баланс: " + currentUser.getWallet().getBalance());
                            }
                            break;

                        case "show-overview":
                            if (isUserLoggedIn()) {
                                System.out.println(financeService.getBudgetOverview(currentUser));
                            }
                            break;

                        case "logout":
                            currentUser = null;
                            System.out.println("Выход из учетной записи.");
                            break;

                        case "exit":
                            authService.saveUsers();
                            System.out.println("До свидания!");
                            return;

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