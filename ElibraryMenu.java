import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.awt.image.BufferedImage;

public class ElibraryMenu extends JFrame {
  private UserAccount loggedInUser;

  public ElibraryMenu(UserAccount loggedInUser) {
    this.loggedInUser = loggedInUser;

    setTitle("E-LIBRARY");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JButton viewAccountDetailsButton = new JButton("View Account Details");
    JButton viewBooksButton = new JButton("View Books");
    JButton searchBooksButton = new JButton("Search Books");
    JButton borrowBooksButton = new JButton("Borrow Books");
    JButton viewBorrowedBooksButton = new JButton("View Borrowed Books");
    JButton returnBooksButton = new JButton("Return Books");
    JButton exitButton = new JButton("Exit");

    JPanel elibraryMenu = new JPanel(new GridLayout(7, 1, 10, 10));
    elibraryMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    elibraryMenu.add(viewAccountDetailsButton);
    elibraryMenu.add(viewBooksButton);
    elibraryMenu.add(searchBooksButton);
    elibraryMenu.add(borrowBooksButton);
    elibraryMenu.add(viewBorrowedBooksButton);
    elibraryMenu.add(returnBooksButton);
    elibraryMenu.add(exitButton);

    add(elibraryMenu);
    pack();
    setLocationRelativeTo(null);

    // VIEW ACCOUNTS
    viewAccountDetailsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showAccountDetails();
      }
    });

    // VIEW BOOKS
    viewBooksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showBooks();
      }
    });

    // SEARCH BOOKS
    searchBooksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String searchTerm = JOptionPane.showInputDialog("Enter your search term:");
        if (searchTerm != null && !searchTerm.isEmpty()) {
          performSearch(searchTerm);
        } else {
          ///
        }
      }
    });

    // BORROW BOOKS
    borrowBooksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Add your borrow functionality here
        borrowBooks();
      }
    });

    // VIEW BORROWED BOOKS
    viewBorrowedBooksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Add your view borrowed books functionality here
        System.out.println("View Borrowed Books");
      }
    });

    // RETURN BOOKS
    returnBooksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Add your return books functionality here
        System.out.println("Return Books");
      }
    });

    // EXIT
    exitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Add your exit functionality here
        System.exit(0);
      }
    });
  }

  private void showAccountDetails() {
    AccountDetailsDialog dialog = new AccountDetailsDialog(loggedInUser, this);
  }

  private void showBooks() {
    // Create a JFrame to contain the progress bar
    JFrame progressFrame = new JFrame("Loading Books");
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true); // Use an indeterminate progress bar

    // Add the progress bar to the progress frame
    progressFrame.add(progressBar);
    progressFrame.setSize(300, 100);
    progressFrame.setLocationRelativeTo(this);
    progressFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    progressFrame.setVisible(true);

    SwingWorker < Void, Void > worker = new SwingWorker < Void, Void > () {
      @Override
      protected Void doInBackground() {
        // Assuming you have a JPanel to display the images
        JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bookPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
          // Establish a database connection
          Connection connection = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6692695", "sql6692695", "y4jarIZXey");

          // Create a statement
          Statement statement = connection.createStatement();

          // Execute a query to retrieve book details including the image data
          ResultSet resultSet = statement.executeQuery("SELECT * FROM books");

          // Fixed size for the images
          int fixedWidth = 300;
          int fixedHeight = 350;

          // Iterate through the result set
          while (resultSet.next()) {
            // Retrieve book details
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            String genre = resultSet.getString("genre");
            String year = resultSet.getString("year");

            // Retrieve image data as a byte array
            byte[] imageData = resultSet.getBytes("image_path");

            // Convert the byte array to a BufferedImage (you may need to adjust this based on your image format)
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            Image originalImage = ImageIO.read(bis);

            // Close the input stream
            bis.close();

            // Create a BufferedImage with the fixed size
            BufferedImage resizedImage = new BufferedImage(fixedWidth, fixedHeight, BufferedImage.TYPE_INT_ARGB);

            // Create a Graphics2D object and draw the original image onto the resized image
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, fixedWidth, fixedHeight, null);
            g2d.dispose();

            // Create a JButton to display the image and book details
            JButton bookButton = new JButton(new ImageIcon(resizedImage));

            // Add ActionListener to the bookButton
            bookButton.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                // Display book details when the button is clicked
                showBookDetails(title, author, genre, year);
              }
            });

            // Add the JButton to the bookPanel
            bookPanel.add(bookButton);
          }

          // Close the result set, statement, and connection
          resultSet.close();
          statement.close();
          connection.close();
        } catch (SQLException | IOException e) {
          e.printStackTrace();
        }

        // Create a JScrollPane to scroll through the books if there are many
        JScrollPane scrollPane = new JScrollPane(bookPanel);

        // Create a new JFrame to display the books
        JFrame booksFrame = new JFrame("Books");
        booksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        booksFrame.add(scrollPane);
        booksFrame.pack();
        booksFrame.setLocationRelativeTo(ElibraryMenu.this);
        booksFrame.setVisible(true);

        return null;
      }

      @Override
      protected void done() {
        // Close the progress frame when the loading is complete
        progressFrame.dispose();
      }
    };

    // Execute the SwingWorker to load books in the background
    worker.execute();
  }

  // Method to show book details
  private void showBookDetails(String title, String author, String genre, String year) {
    String message = "Book Title: " + title + "\nAuthor: " + author + "\nGenre: " + genre + "\nYear: " + year;
    JOptionPane.showMessageDialog(this, message, "Book Details", JOptionPane.INFORMATION_MESSAGE);
  }

  private void performSearch(String searchTerm) {
    try {
      // Establish a database connection
      Connection connection = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6692695", "sql6692695", "y4jarIZXey");

      // Create a statement
      Statement statement = connection.createStatement();

      // Construct a SQL query for searching books based on various fields
      String query = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR LOWER(genre) LIKE ? OR year LIKE ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        // Set the parameters for the prepared statement
        preparedStatement.setString(1, "%" + searchTerm.toLowerCase() + "%");
        preparedStatement.setString(2, "%" + searchTerm.toLowerCase() + "%");
        preparedStatement.setString(3, "%" + searchTerm.toLowerCase() + "%");
        preparedStatement.setString(4, "%" + searchTerm + "%");

        // Execute the query
        ResultSet resultSet = preparedStatement.executeQuery();

        // Process the result set and display the search results with images
        if (resultSet.next()) {
          showSearchResults(resultSet);
        } else {
          JOptionPane.showMessageDialog(ElibraryMenu.this, "No matching books found.", "Search Results", JOptionPane.ERROR_MESSAGE);
        }
      }

      // Close the statement and connection
      statement.close();
      connection.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(ElibraryMenu.this, "Error performing search.", "Search Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void showSearchResults(ResultSet resultSet) throws SQLException {
    // Create a JFrame to contain the search results
    JFrame searchResultsFrame = new JFrame("Search Results");
    JPanel searchResultsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    searchResultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Fixed size for the images
    int fixedWidth = 300;
    int fixedHeight = 350;

    do {
      // Retrieve book details
      String title = resultSet.getString("title");
      String author = resultSet.getString("author");
      String genre = resultSet.getString("genre");
      String year = resultSet.getString("year");

      // Retrieve image data as a byte array
      byte[] imageData = resultSet.getBytes("image_path");

      try {
        // Convert the byte array to a BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        Image originalImage = ImageIO.read(bis);

        // Close the input stream
        bis.close();

        // Create a BufferedImage with the fixed size
        BufferedImage resizedImage = new BufferedImage(fixedWidth, fixedHeight, BufferedImage.TYPE_INT_ARGB);

        // Create a Graphics2D object and draw the original image onto the resized image
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, fixedWidth, fixedHeight, null);
        g2d.dispose();

        // Create a JButton to display the image and book details
        JButton bookButton = new JButton(new ImageIcon(resizedImage));

        // Add ActionListener to the bookButton
        bookButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Display book details when the button is clicked
            showBookDetails(title, author, genre, year);
          }
        });

        // Add the JButton to the searchResultsPanel
        searchResultsPanel.add(bookButton);
      } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(ElibraryMenu.this, "Error loading image for book: " + title, "Image Error", JOptionPane.ERROR_MESSAGE);
      }
    } while (resultSet.next());

    // Create a JScrollPane to scroll through the search results if there are many
    JScrollPane scrollPane = new JScrollPane(searchResultsPanel);

    // Add the scrollPane to the searchResultsFrame
    searchResultsFrame.add(scrollPane);
    searchResultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    searchResultsFrame.pack();
    searchResultsFrame.setLocationRelativeTo(ElibraryMenu.this);
    searchResultsFrame.setVisible(true);
  }

  // Method to handle borrowing books
  // Method to handle borrowing books
  private void borrowBooks() {
    try {
      // Establish database connection
      Connection connection = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6692695", "sql6692695", "y4jarIZXey");

      // Get the book to borrow using search functionality
      String searchTerm = JOptionPane.showInputDialog("Enter the book title to borrow:");
      if (searchTerm != null && !searchTerm.isEmpty()) {
        // Perform search and borrow the book if found
        performSearchAndBorrow(searchTerm, connection);
      } else {
        JOptionPane.showMessageDialog(null, "Please enter a valid book title to borrow.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
      }

      connection.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to borrow the book. Please try again.", "Borrow Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void performSearchAndBorrow(String searchTerm, Connection connection) {
    try {
      // Create a statement to search for the book
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE LOWER(title) LIKE ?");
      preparedStatement.setString(1, "%" + searchTerm.toLowerCase() + "%");
      ResultSet resultSet = preparedStatement.executeQuery();

      // Process the search results and borrow the book if found
      if (resultSet.next()) {
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        String genre = resultSet.getString("genre");
        String year = resultSet.getString("year");

        // Display book details
        showBookDetails(title, author, genre, year);

        // Borrow the book directly without checking conditions
        borrowBookFromDatabase(connection, title);
      } else {
        JOptionPane.showMessageDialog(null, "Book not found.", "Search Results", JOptionPane.ERROR_MESSAGE);
      }

      preparedStatement.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error performing search and borrow.", "Search Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Helper method to check if the user has already borrowed the selected book
  private String getSelectedBook(Connection connection) throws SQLException {
    String selectedBook = null;

    // Get the selected book title
    JComboBox < String > bookComboBox = getAvailableBooksComboBox(connection);
    int option = JOptionPane.showConfirmDialog(null, bookComboBox, "Select a book to borrow:", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      selectedBook = (String) bookComboBox.getSelectedItem();

      // Check if the selected book is already borrowed
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BorrowedBooks WHERE Username = ? AND BookBorrowed = ?");
      preparedStatement.setString(1, loggedInUser.getUsername());
      preparedStatement.setString(2, selectedBook);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        // Book is already borrowed by the user
        selectedBook = resultSet.getString("BookBorrowed");
      } else {
        // Book is not borrowed by the user
        selectedBook = null;
      }
      preparedStatement.close();
    }

    return selectedBook;
  }

  // Helper method to get a combo box with available books
  private JComboBox < String > getAvailableBooksComboBox(Connection connection) throws SQLException {
    // Create a statement to retrieve available books
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT title FROM books");

    // Create a list to store available book titles
    DefaultComboBoxModel < String > bookModel = new DefaultComboBoxModel < > ();
    while (resultSet.next()) {
      bookModel.addElement(resultSet.getString("title"));
    }
    statement.close();

    // Create a combo box to display available books
    return new JComboBox < > (bookModel);
  }

  // Helper method to get the count of books borrowed by the user
  private int getBorrowedBooksCount(Connection connection) throws SQLException {
    int borrowedBooksCount = 0;

    PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS Count FROM BorrowedBooks WHERE Username = ?");
    preparedStatement.setString(1, loggedInUser.getUsername());
    ResultSet resultSet = preparedStatement.executeQuery();
    if (resultSet.next()) {
      borrowedBooksCount = resultSet.getInt("Count");
    }
    preparedStatement.close();

    return borrowedBooksCount;
  }

  // Helper method to insert borrowing record into the database
  // Helper method to borrow the book from the database
  private void borrowBookFromDatabase(Connection connection, String bookTitle) {
    try {
      // Check if the user has already borrowed a book
      if (isUserAlreadyBorrowing(connection)) {
        JOptionPane.showMessageDialog(null, "You cannot borrow more than one book at a time", "Borrowing Limit Exceeded", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Calculate return date (7 days from today)
      LocalDate currentDate = LocalDate.now();
      LocalDate returnDate = currentDate.plusDays(7);

      // Insert borrowing record into database
      String insertQuery = "INSERT INTO BorrowedBooks (Name, Username, Email, BookBorrowed, DateBorrowed, ReturnDate) VALUES (?, ?, ?, ?, ?, ?)";
      try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
        preparedStatement.setString(1, loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
        preparedStatement.setString(2, loggedInUser.getUsername());
        preparedStatement.setString(3, loggedInUser.getEmail());
        preparedStatement.setString(4, bookTitle);
        preparedStatement.setDate(5, Date.valueOf(currentDate));
        preparedStatement.setDate(6, Date.valueOf(returnDate));
        preparedStatement.executeUpdate();

        JOptionPane.showMessageDialog(null, "You have successfully borrowed the book: " + bookTitle, "Borrow Success", JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error borrowing book", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean isUserAlreadyBorrowing(Connection connection) throws SQLException {
    int borrowedBooksCount = getBorrowedBooksCount(connection);
    return borrowedBooksCount > 0;
  }
}