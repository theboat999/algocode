import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
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
        System.out.println("Borrow Books");
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
          Connection connection = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6689518", "sql6689518", "z1lwebFgAc");

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
        Connection connection = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6689518", "sql6689518", "z1lwebFgAc");

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




}