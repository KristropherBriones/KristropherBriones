// ExpenseManager.java
public class ExpenseManager {

    public static void addExpense(String category, double amount, String description, String date) {
        Expense expense = new Expense(0, category, amount, description, date);
        DatabaseHandler.insertExpense(expense);
    }

    public static void removeExpense(int id) {
        DatabaseHandler.deleteExpense(id);
    }

    public static void editExpense(int id, String category, double amount, String description, String date) {
        Expense expense = new Expense(id, category, amount, description, date);
        DatabaseHandler.updateExpense(expense);
    }
}
