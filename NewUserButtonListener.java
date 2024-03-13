import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewUserButtonListener implements ActionListener {
    private final LoginPanel loginPanel;
    private JTextField firstNameTextField;
    private JTextField surnameTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> dateComboBox;
    private JComboBox<String> yearComboBox;
    private JFrame accountCreationFrame; // Declare the variable here

    static final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6689518";
    static final String USER = "sql6689518";
    static final String PASS = "z1lwebFgAc";

    public NewUserButtonListener(LoginPanel loginPanel, JFrame accountCreationFrame) {
        this.loginPanel = loginPanel;
        this.accountCreationFrame = accountCreationFrame; // Initialize the variable
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame accountCreationFrame = new JFrame("Account Creation");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        addComponent(panel, createLabel("First Name:"), gbc, 0, 0);
        firstNameTextField = createTextField();
        addComponent(panel, firstNameTextField, gbc, 1, 0);

        addComponent(panel, createLabel("Surname:"), gbc, 0, 1);
        surnameTextField = createTextField();
        addComponent(panel, surnameTextField, gbc, 1, 1);

        addComponent(panel, createLabel("Username:"), gbc, 0, 2);
        usernameTextField = createTextField();
        addComponent(panel, usernameTextField, gbc, 1, 2);

        addComponent(panel, createLabel("Password:"), gbc, 0, 3);
        passwordField = createPasswordField();
        addComponent(panel, passwordField, gbc, 1, 3);

        addComponent(panel, createLabel("Email:"), gbc, 0, 4);
        emailField = createTextField();
        addComponent(panel, emailField, gbc, 1, 4);

        addComponent(panel, createLabel("Birthdate:"), gbc, 0, 5);
        JPanel birthdatePanel = createBirthdatePanel();
        addComponent(panel, birthdatePanel, gbc, 1, 5);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logic for account creation
                String firstName = firstNameTextField.getText().trim();
                String lastName = surnameTextField.getText().trim();
                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                String birthdate = createBirthdateString();

                if (firstName.isEmpty() || lastName.isEmpty()) {
                    showErrorDialog("First name and last name cannot be blank!");
                } else if (!isValidEmail(email)) {
                    showErrorDialog("Email should contain a valid domain!");
                } else if (username.length() < 5) {
                    showErrorDialog("USERNAME MUST BE AT LEAST 5 CHARACTERS!");
                } else if (!isPasswordValid(password)) {
                    showErrorDialog("PASSWORD MUST CONTAIN AT LEAST 2 SPECIAL CHARACTERS AND 8 CHARACTERS");
                } else {
                    saveAccountToDatabase(firstName, lastName, birthdate, email, username, password);
                    setError("");
                    accountCreationFrame.dispose();
                }
            }
        });
        addComponent(panel, submitButton, gbc, 1, 6);

        accountCreationFrame.add(panel);
        accountCreationFrame.pack();
        accountCreationFrame.setLocationRelativeTo(null);
        accountCreationFrame.setVisible(true);
    }

    private void addComponent(JPanel panel, JComponent component, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(component, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.RIGHT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        return passwordField;
    }

    private JPanel createBirthdatePanel() {
        JPanel birthdatePanel = new JPanel(new FlowLayout());

        String[] months = {
                "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"
        };
        monthComboBox = new JComboBox<>(months);
        birthdatePanel.add(monthComboBox);

        String[] dates = new String[31];
        for (int i = 0; i < 31; i++) {
            dates[i] = Integer.toString(i + 1);
        }
        dateComboBox = new JComboBox<>(dates);
        birthdatePanel.add(dateComboBox);

        String[] years = new String[124];
        for (int i = 0; i < 124; i++) {
            years[i] = Integer.toString(1900 + i);
        }
        yearComboBox = new JComboBox<>(years);
        birthdatePanel.add(yearComboBox);

        return birthdatePanel;
    }

    private String createBirthdateString() {
        return (String) monthComboBox.getSelectedItem() + " " +
                (String) dateComboBox.getSelectedItem() + " " +
                (String) yearComboBox.getSelectedItem();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.replaceAll("[^!@#$%^&*(),.?\":{}|<>]", "").length() >= 2;
    }

    private void saveAccountToDatabase(String firstName, String lastName, String birthdate, String email, String username, String password) {
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        createTableIfNotExists(conn); // Create table if not exists

        if (isUsernameAvailable(conn, username) && isEmailAvailable(conn, email)) {
            insertUserData(conn, firstName, lastName, birthdate, email, username, password);
            setError("");
            JOptionPane.showMessageDialog(loginPanel, "Account Created Successfully!");
            accountCreationFrame.dispose();
        } else {
            showErrorDialog("Username or email already exists. Please choose a different one.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        showErrorDialog("Error saving user data to the database.");
    }
}

private boolean isUsernameAvailable(Connection conn, String username) throws SQLException {
    String query = "SELECT COUNT(*) FROM users WHERE BINARY username=?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
        preparedStatement.setString(1, username);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0; // If count is 0, username is available
            }
        }
    }
    return false; // Default to false if an error occurs
}

private boolean isEmailAvailable(Connection conn, String email) throws SQLException {
    String query = "SELECT COUNT(*) FROM users WHERE BINARY email=?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
        preparedStatement.setString(1, email);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0; // If count is 0, email is available
            }
        }
    }
    return false; // Default to false if an error occurs
}


    private void createTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "first_name VARCHAR(50) NOT NULL," +
                "last_name VARCHAR(50) NOT NULL," +
                "birthdate VARCHAR(20) NOT NULL," +
                "email VARCHAR(50) NOT NULL," +
                "username VARCHAR(50) NOT NULL," +
                "password VARCHAR(50) NOT NULL);";

        try (PreparedStatement preparedStatement = conn.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        }
    }

    private void insertUserData(Connection conn, String firstName, String lastName, String birthdate, String email, String username, String password) throws SQLException {
        String insertDataSQL = "INSERT INTO users (first_name, last_name, birthdate, email, username, password) VALUES (?, ?, ?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insertDataSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, birthdate);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, password);

            preparedStatement.executeUpdate();
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setError(String error) {
        loginPanel.setError(error);
    }
}
