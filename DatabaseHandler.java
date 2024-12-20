// DatabaseHandler.java
import java.sql.*;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:resources/budget-expenses.db";

    // Connect to SQLite database
    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            return connection;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return null;
        }
    }

    // Initialize database table if not exists
    public static void initializeDatabase() {
        try (Connection conn = connect()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "date TEXT NOT NULL)";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // Method to insert an expense into the database
    public static void insertExpense(Expense expense) {
        String sql = "INSERT INTO expenses(category, amount, description, date) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, expense.getCategory());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setString(4, expense.getDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting expense: " + e.getMessage());
        }
    }

    // Method to get all expenses from the database
    public static ResultSet getAllExpenses() {
        String sql = "SELECT * FROM expenses";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error retrieving expenses: " + e.getMessage());
            return null;
        }
    }

    // Method to delete an expense by ID
    public static void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting expense: " + e.getMessage());
        }
    }

    // Method to update an expense
    public static void updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET category = ?, amount = ?, description = ?, date = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, expense.getCategory());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setString(4, expense.getDate());
            pstmt.setInt(5, expense.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating expense: " + e.getMessage());
        }
    }
}

