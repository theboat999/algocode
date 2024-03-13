import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private LoginPanel loginPanel;

    public LoginForm() {
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginPanel = new LoginPanel();

        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("HELVETICA", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        pack(); // Adjusts the frame size based on its components
        setLocationRelativeTo(null); // Centers the frame on the screen
        setVisible(true);
    }
}
