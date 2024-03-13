import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JLabel errorLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some spacing

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20); // Set the preferred width
        passwordField = new JPasswordField(20); // Set the preferred width

        JButton submitButton = new JButton("Submit");
        JButton newUserButton = new JButton("New User?");

        submitButton.addActionListener(new SubmitButtonListener(this));
        newUserButton.addActionListener(new NewUserButtonListener(this, new JFrame())); // Provide a JFrame instance here


        Font labelFont = new Font("Helvetica", Font.BOLD, 18); // Adjust the font and size as needed
        Font buttonFont = new Font("Helvetica", Font.BOLD, 16); // Adjust the font and size as needed

        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        submitButton.setFont(buttonFont);
        newUserButton.setFont(buttonFont);

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED); // Set the error text color
        errorLabel.setFont(new Font("Helvetica", Font.BOLD, 16)); // Adjust the font and size as needed

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(errorLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;  // Change gridwidth to 1
        gbc.anchor = GridBagConstraints.EAST;  // Keep the anchor to the right
        add(submitButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;  // Change gridwidth to 1
        gbc.anchor = GridBagConstraints.WEST;  // Keep the anchor to the left
        add(newUserButton, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(submitButton);
        buttonsPanel.add(newUserButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonsPanel, gbc);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void setError(String error) {
        errorLabel.setText(error);
    }
}