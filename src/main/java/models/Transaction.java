package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс, представляющий финансовую транзакцию.
 * Хранит данные о сумме, дате, типе и категории транзакции.
 */
public class Transaction implements Serializable {
    private BigDecimal amount;
    private String date;
    private TransactionType type;
    private String category;

    /**
     * Конструктор класса для инициализации всех полей.
     * @param amount Сумма транзакции.
     * @param type Тип транзакции.
     * @param category Категория транзакции.
     */
    public Transaction(BigDecimal amount, String date, TransactionType type, String category) {
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    // Геттер для получения суммы транзакции.
    public BigDecimal getAmount() {
        return amount;
    }

    // Геттер для получения времени транзакции.
    public String getDate() {
        return date;
    }

    // Геттер для получения категории транзакции.
    public String getCategory() {
        return category;
    }

    // Геттер для получения типа транзакции.
    public TransactionType getType() {
        return type;
    }
}
