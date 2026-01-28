package test;

import DAO.BookDAO;
import DTO.BookDTO;
import java.util.ArrayList;

public class TestDAO {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("          TEST LOAD DATA FROM DATABASE (DAO LAYER)             ");
        System.out.println("================================================================\n");

        try {
            BookDAO bookDAO = new BookDAO();

            // ===== TEST 1: SELECT ALL =====
            System.out.println("TEST 1: SELECT ALL - Get all books from database");
            System.out.println("------------------------------------------------------------");

            ArrayList<BookDTO> books = bookDAO.selectAll();

            if (books == null || books.isEmpty()) {
                System.out.println("WARNING: No data in database!");
                return;
            }

            System.out.println("SUCCESS: Loaded " + books.size() + " books\n");

            // Print table
            printTableHeader();
            for (int i = 0; i < books.size(); i++) {
                BookDTO book = books.get(i);
                printTableRow(i + 1, book);
            }
            printTableFooter(books.size());

            // ===== TEST 2: SELECT BY ID =====
            System.out.println("\n\nTEST 2: SELECT BY ID - Get book by ID");
            System.out.println("------------------------------------------------------------");

            if (books.size() > 0) {
                int testId = books.get(0).getBookId();
                System.out.println("Searching for book with ID = " + testId + "...\n");

                BookDTO book = bookDAO.selectById(testId);
                if (book != null) {
                    System.out.println("SUCCESS: Book found!\n");
                    printBookDetail(book);
                } else {
                    System.out.println("ERROR: Book with ID = " + testId + " not found");
                }
            }

            // ===== TEST 3: SEARCH =====
            System.out.println("\n\nTEST 3: SEARCH - Search books");
            System.out.println("------------------------------------------------------------");

            String keyword = "";
            ArrayList<BookDTO> searchResult = bookDAO.search(keyword, null, null, null, null);
            System.out.println("SUCCESS: Found " + searchResult.size() + " books");

            if (books.size() > 0) {
                String testKeyword = books.get(0).getBookTitle().substring(0,
                        Math.min(3, books.get(0).getBookTitle().length()));
                System.out.println("\nSearch with keyword: \"" + testKeyword + "\"");
                searchResult = bookDAO.search(testKeyword, null, null, null, null);
                System.out.println("SUCCESS: Found " + searchResult.size() + " books");

                if (searchResult.size() > 0 && searchResult.size() <= 3) {
                    System.out.println("\nSearch results:");
                    for (BookDTO b : searchResult) {
                        System.out.println("  - " + b.getBookTitle());
                    }
                }
            }

            // ===== STATISTICS =====
            System.out.println("\n\nSTATISTICS");
            System.out.println("------------------------------------------------------------");
            printStatistics(books);

            System.out.println("\n================================================================");
            System.out.println("                    TEST DAO COMPLETED                          ");
            System.out.println("================================================================");

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== HELPER METHODS =====

    private static void printTableHeader() {
        System.out.println(
                "+-----+-----------------+----------------------------------+----------------------+-----------------+--------------+----------+");
        System.out.printf("| %-3s | %-15s | %-32s | %-20s | %-15s | %-12s | %-8s |%n",
                "No", "ISBN", "Book Title", "Publisher", "Category", "Price", "Stock");
        System.out.println(
                "+-----+-----------------+----------------------------------+----------------------+-----------------+--------------+----------+");
    }

    private static void printTableRow(int stt, BookDTO book) {
        System.out.printf("| %-3d | %-15s | %-32s | %-20s | %-15s | %,12.0f | %8d |%n",
                stt,
                truncate(book.getIsbn(), 15),
                truncate(book.getBookTitle(), 32),
                truncate(book.getPublisherName(), 20),
                truncate(book.getCategoryName(), 15),
                book.getSellingPrice(),
                book.getStockQuantity());
    }

    private static void printTableFooter(int total) {
        System.out.println(
                "+-----+-----------------+----------------------------------+----------------------+-----------------+--------------+----------+");
        System.out.println("Total: " + total + " books");
    }

    private static void printBookDetail(BookDTO book) {
        System.out.println("+--- BOOK DETAILS --------------------------------------------------+");
        System.out.println("| Book ID         : " + book.getBookId());
        System.out.println("| ISBN            : " + book.getIsbn());
        System.out.println("| Title           : " + book.getBookTitle());
        System.out.println("| Publisher       : " + book.getPublisherName());
        System.out.println("| Category        : " + book.getCategoryName());
        System.out.println("| Pages           : " + book.getPageCount());
        System.out.println("| Language        : " + book.getLanguage());
        System.out.println("| Publication Year: " + book.getPublicationYear());
        System.out.println("| Cover Type      : " + book.getCoverType());
        System.out.println("| Import Price    : " + String.format("%,d", (int) book.getImportPrice()) + " VND");
        System.out.println("| Selling Price   : " + String.format("%,d", (int) book.getSellingPrice()) + " VND");
        System.out.println("| Stock           : " + book.getStockQuantity() + " books");
        System.out.println("| Minimum Stock   : " + book.getMinimumStock() + " books");
        System.out.println("| Status          : " + book.getStatus());

        if (book.getAuthorNames() != null && !book.getAuthorNames().isEmpty()) {
            System.out.println("| Authors         : " + String.join(", ", book.getAuthorNames()));
        }

        System.out.println("+-------------------------------------------------------------------+");
    }

    private static void printStatistics(ArrayList<BookDTO> books) {
        if (books.isEmpty())
            return;

        double totalValue = 0;
        int totalStock = 0;
        double maxPrice = books.get(0).getSellingPrice();
        double minPrice = books.get(0).getSellingPrice();

        for (BookDTO book : books) {
            totalValue += book.getSellingPrice() * book.getStockQuantity();
            totalStock += book.getStockQuantity();
            if (book.getSellingPrice() > maxPrice)
                maxPrice = book.getSellingPrice();
            if (book.getSellingPrice() < minPrice)
                minPrice = book.getSellingPrice();
        }

        System.out.println("  Total book titles    : " + books.size() + " titles");
        System.out.println("  Total books in stock : " + totalStock + " books");
        System.out.println("  Total inventory value: " + String.format("%,d", (int) totalValue) + " VND");
        System.out.println("  Highest price        : " + String.format("%,d", (int) maxPrice) + " VND");
        System.out.println("  Lowest price         : " + String.format("%,d", (int) minPrice) + " VND");
        System.out.println(
                "  Average price        : " + String.format("%,d", (int) (totalValue / totalStock)) + " VND/book");
    }

    private static String truncate(String str, int length) {
        if (str == null)
            return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}