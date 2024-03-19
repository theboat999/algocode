import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookViewer extends JFrame {
    private static final int BOOK_IMAGE_WIDTH = 355;
    private static final int BOOK_IMAGE_HEIGHT = 405;
    private static final int NUM_COLUMNS = 5;
    
    private List<Book> books;
    private UserAccount loggedInUser; // Add this variable to hold the logged-in user information
    private List<ImageIcon> bookIcons; // Add this variable to hold book icons
    private JPanel booksPanel; // Add this variable to hold the panel for displaying books

    public BookViewer(UserAccount loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("Book List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize bookIcons and booksPanel
        bookIcons = new ArrayList<>();
        booksPanel = new JPanel(new GridLayout(0, NUM_COLUMNS, 6, 6));

        // Display a loading message while images are being loaded
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        Object[] message = {
                "Please wait. Loading books...",
                progressBar
        };

        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null
        );

        JDialog loadingDialog = optionPane.createDialog(this, "Loading");
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                books = getBookListFromDatabase();
                loadBookImages();
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                if (!bookIcons.isEmpty()) {
                    for (int i = 0; i < bookIcons.size(); i++) {
                        JLabel bookLabel = new JLabel(bookIcons.get(i));
                        int finalI = i;
                        bookLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                if (JOptionPane.showConfirmDialog(null, "Do you want to borrow this book?", "Borrow Book", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                    borrowBook(finalI);
                                }
                            }
                        });
                        booksPanel.add(bookLabel);
                    }

                    JScrollPane scrollPane = new JScrollPane(booksPanel);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                    Dimension preferredSize = new Dimension(
                            NUM_COLUMNS * (BOOK_IMAGE_WIDTH + 6),
                            ((bookIcons.size() + NUM_COLUMNS - 1) / NUM_COLUMNS) * (BOOK_IMAGE_HEIGHT + 6)
                    );
                    scrollPane.setPreferredSize(preferredSize);

                    add(scrollPane, BorderLayout.CENTER);

                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    JLabel noBooksLabel = new JLabel("No books available");
                    add(noBooksLabel, BorderLayout.CENTER);
                }

                revalidate();
                repaint();
            }
        };

        worker.execute();

        loadingDialog.setVisible(true);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void borrowBook(int index) {
        if (index >= 0 && index < books.size()) {
            Book selectedBook = books.get(index);
            // Calculate return date (7 days from today)
            LocalDate currentDate = LocalDate.now();
            LocalDate returnDate = currentDate.plusDays(7);

            // Insert borrowed book information into the database
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6692695";
                final String USER = "sql6692695";
                final String PASS = "y4jarIZXey";

                try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "INSERT INTO BorrowedBooks (Name, Username, Email, BookBorrowed, DateBorrowed, ReturnDate) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
                        preparedStatement.setString(2, loggedInUser.getUsername());
                        preparedStatement.setString(3, loggedInUser.getEmail());
                        preparedStatement.setString(4, selectedBook.getTitle());
                        preparedStatement.setString(5, currentDate.format(DateTimeFormatter.ISO_DATE));
                        preparedStatement.setString(6, returnDate.format(DateTimeFormatter.ISO_DATE));
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error borrowing book", "Error", JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(null, "Book borrowed successfully. Return by: " + returnDate, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid book selection", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Book> getBookListFromDatabase() {
        List<Book> bookList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6692695";
            final String USER = "sql6692695";
            final String PASS = "y4jarIZXey";

            try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String query = "SELECT * FROM books"; // Assuming 'books' is the table name
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Book book = new Book(
                                    resultSet.getString("image_path"),
                                    resultSet.getString("title"),
                                    resultSet.getString("author"),
                                    resultSet.getInt("year"),
                                    resultSet.getString("genre")
                            );
                            bookList.add(book);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        return bookList;
    }

    private void loadBookImages() {
        for (Book book : books) {
            try {
                ImageIcon originalIcon = new ImageIcon(book.getImagePath());
                Image resizedImage = originalIcon.getImage().getScaledInstance(BOOK_IMAGE_WIDTH, BOOK_IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);
                bookIcons.add(resizedIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
