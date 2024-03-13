import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookListReader {

    public static List<Book> readBookList() {
        List<Book> bookList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("book_list.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(",", 5);
                if (bookData.length == 5) {
                    bookList.add(new Book(bookData[0].trim(), bookData[1].trim(), bookData[2].trim(), Integer.parseInt(bookData[3].trim()), bookData[4].trim()));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return bookList;
    }

    public static List<ImageIcon> loadBookImages(List<Book> books, int width, int height) {
        List<ImageIcon> bookIcons = new ArrayList<>();

        for (Book book : books) {
            try {
                ImageIcon originalIcon = new ImageIcon(book.getImagePath());
                Image resizedImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);
                bookIcons.add(resizedIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bookIcons;
    }
}
