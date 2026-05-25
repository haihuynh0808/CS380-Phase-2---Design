package final_project;

/**
 * Represents one financial transaction in the Smart Expense Tracker application.
 * 
 * A transaction belongs to a user and stores the type, amount, category,
 * date, and description.
 */
public class Transaction {

    // The unique transaction ID.
    private int id;

    // The ID of the user who owns this transaction. 
    private int userId;

    // The transaction type, such as "Income" or "Expense". 
    private String type;

    // The amount of money in the transaction. 
    private double amount;

    // The category of the transaction, such as Food or Rent. 
    private String category;

    // The date of the transaction. 
    private String date;

    // A short description or note for the transaction. 
    private String description;

    /**
     * Creates a transaction with all fields, including the transaction ID.
     *
     * @param id the transaction ID
     * @param userId the user ID
     * @param type the transaction type
     * @param amount the amount
     * @param category the category
     * @param date the date
     * @param description the description
     */
    public Transaction(int id, int userId, String type, double amount,
                       String category, String date, String description) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /**
     * Creates a transaction without a transaction ID.
     * This is useful when creating a new transaction before the database assigns an ID.
     *
     * @param userId the user ID
     * @param type the transaction type
     * @param amount the amount
     * @param category the category
     * @param date the date
     * @param description the description
     */
    public Transaction(int userId, String type, double amount,
                       String category, String date, String description) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /**
     * Returns the transaction ID.
     *
     * @return the transaction ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the transaction ID.
     *
     * @param id the transaction ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the user ID.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the transaction type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the transaction type.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the transaction amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the transaction amount.
     *
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the transaction information as a string.
     *
     * @return a string version of the transaction
     */
    @Override
    public String toString() {
        return "Transaction ID: " + id
                + ", User ID: " + userId
                + ", Type: " + type
                + ", Amount: " + amount
                + ", Category: " + category
                + ", Date: " + date
                + ", Description: " + description;
    }
}
