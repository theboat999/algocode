import javax.swing.*;

public class AccountDetailsDialog extends JDialog {
    private UserAccount loggedInUser;

    public AccountDetailsDialog(UserAccount loggedInUser, JFrame parent) {
        this.loggedInUser = loggedInUser;
        initComponents();
    }


    private void initComponents() {
        StringBuilder accountDetails = new StringBuilder();
        accountDetails.append("First Name: ").append(loggedInUser.getFirstName()).append("\n");
        accountDetails.append("Last Name: ").append(loggedInUser.getLastName()).append("\n");
        accountDetails.append("Username: ").append(loggedInUser.getUsername()).append("\n");
        accountDetails.append("Birthdate: ").append(loggedInUser.getBirthdate()).append("\n");
        accountDetails.append("Email: ").append(loggedInUser.getEmail()).append("\n");
        accountDetails.append("Password: ").append(loggedInUser.getPassword());

        JOptionPane.showMessageDialog(null, accountDetails.toString(), "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }
}