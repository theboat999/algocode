public class Book {

    private String imagePath;
    private String title;
    private String author;
    private int year;
    private String genre;

    public Book(String imagePath, String title, String author, int year, String genre) {
        this.imagePath = imagePath;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }
}
