package com.codecrafters.expensetracker.model;
 
import java.time.LocalDate;
 
/**
 * Represents a single financial transaction belonging to a user.
 *
 * @author  Kaltoum Mai Moussa Chetima
 */
public class Transaction {
 
    /** Unique database-assigned identifier for this transaction. */
    private int id;
 
    /** The owning user's ID (foreign key to users table). */
    private int userId;
 
    /** Transaction type: either "Income" or "Expense". */
    private String type;
 
    /** Monetary amount; must be greater than zero. */
    private double amount;
 
    /** Category label (e.g., "Food", "Rent", "Salary"). */
    private String category;
 
    /** Date on which the transaction occurred. */
    private LocalDate date;
 
    /** Optional free-text note about the transaction. May be null. */
    private String description;
 
    /**
     * Constructs a fully populated Transaction.
     *
     * @param id          the database-assigned unique ID
     * @param userId      the ID of the owning user
     * @param type        "Income" or "Expense"
     * @param amount      positive monetary value
     * @param category    spending or income category
     * @param date        date of the transaction
     * @param description optional note; may be {@code null}
     */
    public Transaction(int id, int userId, String type, double amount,
                       String category, LocalDate date, String description) {
        this.id          = id;
        this.userId      = userId;
        this.type        = type;
        this.amount      = amount;
        this.category    = category;
        this.date        = date;
        this.description = description;
    }
 
    /** @return the unique transaction ID */
    public int getId()          { return id; }
 
    /** @return the owning user's ID */
    public int getUserId()      { return userId; }
 
    /** @return "Income" or "Expense" */
    public String getType()     { return type; }
 
    /** @return the transaction amount */
    public double getAmount()   { return amount; }
 
    /** @return the category label */
    public String getCategory() { return category; }
 
    /** @return the transaction date */
    public LocalDate getDate()  { return date; }
 
    /** @return the optional description, or {@code null} */
    public String getDescription() { return description; }
 
    /** @param amount the new amount; must be greater than zero */
    public void setAmount(double amount)      { this.amount = amount; }
 
    /** @param type the new type: "Income" or "Expense" */
    public void setType(String type)          { this.type = type; }
 
    /** @param category the new category label */
    public void setCategory(String category)  { this.category = category; }
 
    /** @param date the new transaction date */
    public void setDate(LocalDate date)       { this.date = date; }
 
    /** @param description the new description; may be {@code null} */
    public void setDescription(String description) { this.description = description; }
}
