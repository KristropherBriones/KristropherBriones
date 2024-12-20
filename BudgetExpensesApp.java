import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BudgetExpensesApp {
    
    // Database constants
    private static final String DB_URL = "jdbc:sqlite:budget_expenses.db";

    // Swing components
    private static JFrame frame;
    private static DefaultListModel<String> expenseListModel;
    
    // Main method to run the app
    public static void main(String[] args) {
        // Initialize the database
        initializeDatabase();

        // Initialize the Swing UI components
        frame = new JFrame("Budget Expense Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddExpenseDialog();
            }
        });

        panel.add(addButton);

        expenseListModel = new DefaultListModel<>();
        JList<String> expenseList = new JList<>(expenseListModel);
        JScrollPane scrollPane = new JScrollPane(expenseList);
        panel.add(scrollPane);

        JButton removeButton = new JButton("Remove Expense");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedExpense(expenseList);
            }
        });

        panel.add(removeButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        loadExpenses();
    }

    // Initialize the database and create table if not exists
    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
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

    // Load expenses from the database and display them
    private static void loadExpenses() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses")) {

            while (rs.next()) {
                String expense = "ID: " + rs.getInt("id") + " | Category: " + rs.getString("category") +
                                 " | Amount: " + rs.getDouble("amount") + " | Description: " + rs.getString("description") +
                                 " | Date: " + rs.getString("date");
                expenseListModel.addElement(expense);
            }
        } catch (SQLException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    // Show dialog to add a new expense
    private static void showAddExpenseDialog() {
        JTextField categoryField = new JTextField(10);
        JTextField amountField = new JTextField(10);
        JTextField descriptionField = new JTextField(10);
        JTextField dateField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Add New Expense", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String category = categoryField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            String date = dateField.getText();

            // Insert expense into the database
            insertExpense(category, amount, description, date);

            // Add expense to the list view
            expenseListModel.addElement("Category: " + category + " | Amount: " + amount + " | Description: " + description + " | Date: " + date);
        }
    }

    // Insert a new expense into the database
    private static void insertExpense(String category, double amount, String description, String date) {
        String sql = "INSERT INTO expenses (category, amount, description, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setString(4, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting expense: " + e.getMessage());
        }
    }

    // Remove selected expense from the list and database
    private static void removeSelectedExpense(JList<String> expenseList) {
        int selectedIndex = expenseList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedExpense = expenseList.getSelectedValue();
            int id = Integer.parseInt(selectedExpense.split(" ")[1]);

            // Delete the expense from the database
            deleteExpense(id);

            // Remove the expense from the list view
            expenseListModel.remove(selectedIndex);
        }
    }

    // Delete expense from the database by its ID
    private static void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting expense: " + e.getMessage());
        }
    }
}
