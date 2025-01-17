package models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Класс, представляющий финансовую транзакцию.
 * Хранит данные о сумме, дате, типе и категории транзакции.
 */
public class Transaction implements Serializable {
    private final BigDecimal amount;
    private final String date;
    private final TransactionType type;
    private final String category;

    /**
     * Конструктор класса для инициализации всех полей.
     * Позволяет создать экземпляр транзакции с заданными параметрами.
     *
     * @param amount Сумма транзакции (BigDecimal).
     * @param date Дата транзакции в текстовом формате.
     * @param type Тип транзакции, указывающий на доход или расход (TransactionType).
     * @param category Категория, к которой относится транзакция.
     */
    public Transaction(BigDecimal amount, String date, TransactionType type, String category) {
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    /**
     * Геттер для получения суммы транзакции.
     * Возвращает значение суммы, связанное с текущей транзакцией.
     *
     * @return Сумма транзакции (BigDecimal).
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Геттер для получения даты транзакции.
     * Позволяет узнать, когда была произведена эта транзакция.
     *
     * @return Дата транзакции (String).
     */
    public String getDate() {
        return date;
    }

    /**
     * Геттер для получения категории транзакции.
     * Позволяет узнать категорию, к которой относится данная транзакция.
     *
     * @return Категория транзакции (String).
     */
    public String getCategory() {
        return category;
    }

    /**
     * Геттер для получения типа транзакции.
     * Определяет, является ли данная транзакция доходом или расходом.
     *
     * @return Тип транзакции (TransactionType).
     */
    public TransactionType getType() {
        return type;
    }
}
