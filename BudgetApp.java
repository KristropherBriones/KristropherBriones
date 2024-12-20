// BudgetApp.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetApp {
    private static JFrame frame;
    private static DefaultListModel<String> expenseListModel;

    public static void main(String[] args) {
        DatabaseHandler.initializeDatabase();
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

    private static void loadExpenses() {
        ResultSet rs = DatabaseHandler.getAllExpenses();
        try {
            while (rs != null && rs.next()) {
                String expenseInfo = "ID: " + rs.getInt("id") + " | Category: " + rs.getString("category") +
                        " | Amount: " + rs.getDouble("amount") + " | Description: " + rs.getString("description") +
                        " | Date: " + rs.getString("date");
                expenseListModel.addElement(expenseInfo);
            }
        } catch (SQLException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

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
            ExpenseManager.addExpense(category, amount, description, date);
            expenseListModel.addElement("Category: " + category + " | Amount: " + amount + " | Description: " + description + " | Date: " + date);
        }
    }

    private static void removeSelectedExpense(JList<String> expenseList) {
        int selectedIndex = expenseList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedExpense = expenseList.getSelectedValue();
            int id = Integer.parseInt(selectedExpense.split(" ")[1]);
            ExpenseManager.removeExpense(id);
            expenseListModel.remove(selectedIndex);
        }
    }
}
