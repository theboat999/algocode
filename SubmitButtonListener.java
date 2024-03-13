import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;

public class SubmitButtonListener implements ActionListener {
    private final LoginPanel loginPanel;
    private List<Book> allBooks;

    public SubmitButtonListener(LoginPanel loginPanel) {
        if (loginPanel == null) {
            throw new IllegalArgumentException("loginPanel must not be null");
        }

        this.loginPanel = loginPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = loginPanel.getUsernameField().getText();
        char[] enteredPassword = loginPanel.getPasswordField().getPassword();

        UserAccount loggedInUser = getUserDetails(enteredUsername, new String(enteredPassword));

        if (loggedInUser != null) {
            // Login successful
            showElibraryMenu(loggedInUser);
        } else {
            // Login failed
            JOptionPane.showMessageDialog(loginPanel, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private UserAccount getUserDetails(String enteredUsername, String enteredPassword) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6689518";
            final String USER = "sql6689518";
            final String PASS = "z1lwebFgAc";

            try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String query = "SELECT * FROM users WHERE BINARY username=? AND BINARY password=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, enteredUsername);
                    preparedStatement.setString(2, enteredPassword);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return new UserAccount(
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    enteredUsername,
                                    enteredPassword,
                                    resultSet.getString("email"),
                                    resultSet.getString("birthdate")
                            );
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return null; // Login failed
    }

    private void showElibraryMenu(UserAccount loggedInUser) {
    ElibraryMenu elibraryMenu = new ElibraryMenu(loggedInUser);
    elibraryMenu.setVisible(true);
}
} 
