# Итоговый проект "Система управления личными финансами" (Магистратура МИФИ + Skillfactory)

## Описание

Это консольное приложение для управления личными финансами. Проект позволит пользователям добавлять доходы и расходы, устанавливать бюджеты на категории и получать статистику по своим финансам.

## Возможности приложения

Функционал приложения разделен на классы - командного процессора, сервисы, сущности и т.п.

* Регистрация и авторизация пользователей.
* Работа с несколькими кошельками.
* Переводы средств между кошельками.
* Создание и управление категориями бюджета (добавление доходов и расходов, установка лимитов с указанием категории).
* Отображение информации по кошельку в виде отчетов в консоли (общая информация, отдельно доходы и расходы).
* Оповещения о превышении лимитов бюджета или баланса кошелька.
* Валидация данных и удобное взаимодействие через команды (чтение команд в цикле).
* Сохранение данных и их загрузка при следующем запуске (с помощью сериализации/десериализации данных в users.dat).

## Установка и запуск

1. Требования:

* Java 20 и старше
* Maven 4.0.0

2. Склонировать репозиторий и перейти в каталог проекта: 

```sh
git clone https://github.com/ChertChill/finance-management-app.git
cd finance-management-app
```

3. Запустить JAR-файл:

```sh
java -jar finance-management-app.jar
```

4. Следовать инструкциям на экране:

* Ввести команду 'help' для просмотра списка команд:

``` java
help - Показать доступные команды
register <username> <password> - Зарегистрировать нового пользователя
login <username> <password> - Войти в систему
logout - Выйти из учетной записи
exit - Выйти из приложения

Команды для работы с кошельком:
-------------------------------
add-income <amount> <category> - Добавить доход
add-expense <amount> <category> - Добавить расход
set-budget <category> <amount> - Установить бюджет для категории
add-transfer <recipientUsername> <amount> - Отправить перевод другому пользователю

Команды для вывода общей информации:
------------------------------------
show-overview - Показать обзор кошелька
    show-balance - Показать текущий баланс
    show-summary - Показать общую сумму доходов и расходов
    show-budget - Показать обзор бюджета
    show-transactions - Показать список всех операций
    show-category-budget <category1> [category2] ... - Показать обзор бюджета по выбранным категориям
    show-category-transactions <category1> [category2] ... - Показать список всех операций по выбранным категориям

Команды для вывода информации по доходам:
-----------------------------------------
show-overview-income - Показать обзор кошелька по доходам
    show-summary-income - Показать общую сумму доходов
    show-budget-income - Показать обзор бюджета по доходам
    show-transactions-income - Показать список всех операций по доходам

Команды для вывода информации по расходам:
------------------------------------------
show-overview-expense - Показать обзор кошелька по расходам
    show-summary-expense - Показать общую сумму расходов
    show-budget-expense - Показать обзор бюджета по расходам
    show-transactions-expense - Показать список всех операций по расходам
```

* Зарегистрироваться с помощью команды 'register' и авторизироваться через 'login'. После этого будет доступен вызов всех остальных команд.
* Вызвать выбранную команду, указав необходимые переменные (если они есть).
* Сообщение после вызова команды отобразит состояние её выполнения (или отчет) и, при необходимости, укажет на ошибку ввода или логики работы. 