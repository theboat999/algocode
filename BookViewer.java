import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookViewer extends JFrame {
    private static final int BOOK_IMAGE_WIDTH = 355;
    private static final int BOOK_IMAGE_HEIGHT = 405;
    private static final int NUM_COLUMNS = 5;

    private List<Book> books;

    public BookViewer(UserAccount loggedInUser) {
        setTitle("Book List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

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

        SwingWorker<List<ImageIcon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ImageIcon> doInBackground() {
                books = getBookListFromDatabase();
                return loadBookImages();
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    List<ImageIcon> bookIcons = get();
                    if (!bookIcons.isEmpty()) {
                        JPanel booksPanel = new JPanel(new GridLayout(0, NUM_COLUMNS, 6, 6));

                        for (int i = 0; i < bookIcons.size(); i++) {
                            JLabel bookLabel = new JLabel(bookIcons.get(i));
                            int finalI = i;
                            bookLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                    showBookDetails(finalI);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading book list", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();

        loadingDialog.setVisible(true);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private List<ImageIcon> loadBookImages() {
        List<ImageIcon> bookIcons = new ArrayList<>();

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

        return bookIcons;
    }

    private List<Book> getBookListFromDatabase() {
        List<Book> bookList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6689518";
            final String USER = "sql6689518";
            final String PASS = "z1lwebFgAc";

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

    private void showBookDetails(int index) {
        if (index >= 0 && index < books.size()) {
            Book selectedBook = books.get(index);
            String details = "Title: " + selectedBook.getTitle() + "\n" +
                    "Author: " + selectedBook.getAuthor() + "\n" +
                    "Year: " + selectedBook.getYear() + "\n" +
                    "Genre: " + selectedBook.getGenre();
            JOptionPane.showMessageDialog(this, details, "Book Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid book selection", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
