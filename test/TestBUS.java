package test;

import BUS.BookBUS;
import DTO.BookDTO;
import java.util.ArrayList;

public class TestBUS {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("          TEST BUSINESS LOGIC LAYER (BUS LAYER)                ");
        System.out.println("================================================================\n");

        try {
            // ===== TEST 1: INITIALIZE AND LOAD DATA =====
            System.out.println("TEST 1: Initialize BUS and load data");
            System.out.println("------------------------------------------------------------");

            BookBUS bookBUS = new BookBUS();
            System.out.println("SUCCESS: BUS initialized!\n");

            // ===== TEST 2: GET ALL BOOKS =====
            System.out.println("TEST 2: Get all books from BUS");
            System.out.println("------------------------------------------------------------");

            ArrayList<BookDTO> books = bookBUS.getAllBooks();

            if (books.isEmpty()) {
                System.out.println("WARNING: Empty list!");
                return;
            }

            System.out.println("SUCCESS: " + books.size() + " books in memory\n");

            // Print first 5 books
            System.out.println("First 5 books:");
            printSimpleList(books, 5);

            // ===== TEST 3: GET BY ID =====
            System.out.println("\n\nTEST 3: Get book by ID");
            System.out.println("------------------------------------------------------------");

            int testId = books.get(0).getBookId();
            BookDTO book = bookBUS.getBookById(testId);

            if (book != null) {
                System.out.println("SUCCESS: Found book with ID = " + testId);
                System.out.println("   Title: " + book.getBookTitle());
                System.out.println("   Price: " + String.format("%,d", (int) book.getSellingPrice()) + " VND");
            } else {
                System.out.println("ERROR: Not found");
            }

            // Test with non-existent ID
            BookDTO notFound = bookBUS.getBookById(99999);
            System.out.println("\nTest with non-existent ID (99999): " +
                    (notFound == null ? "SUCCESS - Returns null" : "ERROR"));

            // ===== TEST 4: SEARCH =====
            System.out.println("\n\nTEST 4: Search books");
            System.out.println("------------------------------------------------------------");

            if (books.size() > 0) {
                String keyword = books.get(0).getBookTitle().split(" ")[0];
                System.out.println("Search with keyword: \"" + keyword + "\"");

                ArrayList<BookDTO> searchResult = bookBUS.searchBooks(keyword, null, null, null, null);
                System.out.println("SUCCESS: Found " + searchResult.size() + " books");

                if (searchResult.size() > 0 && searchResult.size() <= 3) {
                    for (BookDTO b : searchResult) {
                        System.out.println("   - " + b.getBookTitle());
                    }
                }
            }

            // Test search by price range
            System.out.println("\nSearch books with price from 50,000 - 200,000 VND");
            ArrayList<BookDTO> priceSearch = bookBUS.searchBooks(null, null, null, 50000.0, 200000.0);
            System.out.println("SUCCESS: Found " + priceSearch.size() + " books");

            // ===== TEST 5: LOW STOCK BOOKS =====
            System.out.println("\n\nTEST 5: Check low stock books");
            System.out.println("------------------------------------------------------------");

            ArrayList<BookDTO> lowStock = bookBUS.getLowStockBooks();
            System.out.println("Found " + lowStock.size() + " books with low stock:");

            if (lowStock.isEmpty()) {
                System.out.println("   SUCCESS: All books have sufficient stock!");
            } else {
                for (int i = 0; i < Math.min(lowStock.size(), 5); i++) {
                    BookDTO b = lowStock.get(i);
                    System.out.printf("   %d. %-40s | Stock: %3d | Min: %3d%n",
                            (i + 1), truncate(b.getBookTitle(), 40),
                            b.getStockQuantity(), b.getMinimumStock());
                }
                if (lowStock.size() > 5) {
                    System.out.println("   ... and " + (lowStock.size() - 5) + " more books");
                }
            }

            // ===== TEST 6: VALIDATION =====
            System.out.println("\n\nTEST 6: Test validation");
            System.out.println("------------------------------------------------------------");

            // Test add valid book
            System.out.println("Test add valid book:");
            BookDTO validBook = new BookDTO();
            validBook.setIsbn("9781234567890");
            validBook.setBookTitle("Test Book Valid");
            validBook.setPublisherId(1);
            validBook.setCategoryId(1);
            validBook.setPageCount(300);
            validBook.setLanguage("Vietnamese");
            validBook.setPublicationYear(2024);
            validBook.setCoverType("Hardcover");
            validBook.setImportPrice(50000);
            validBook.setSellingPrice(75000);
            validBook.setStockQuantity(100);
            validBook.setMinimumStock(10);
            validBook.setStatus("available");

            boolean addResult = bookBUS.addBook(validBook);
            System.out.println("   Result: " + (addResult ? "SUCCESS - Added" : "FAILED - Not added"));

            // Test add invalid book (empty title)
            System.out.println("\nTest add invalid book (empty title):");
            BookDTO invalidBook = new BookDTO();
            invalidBook.setIsbn("9780987654321");
            invalidBook.setBookTitle(""); // Empty
            invalidBook.setPublisherId(1);
            invalidBook.setCategoryId(1);
            invalidBook.setImportPrice(50000);
            invalidBook.setSellingPrice(75000);

            boolean invalidResult = bookBUS.addBook(invalidBook);
            System.out.println(
                    "   Result: " + (invalidResult ? "ERROR - Should not add" : "SUCCESS - Blocked correctly"));

            // Test duplicate ISBN
            if (books.size() > 0) {
                System.out.println("\nTest add book with duplicate ISBN:");
                BookDTO duplicateIsbn = new BookDTO();
                duplicateIsbn.setIsbn(books.get(0).getIsbn()); // Duplicate ISBN
                duplicateIsbn.setBookTitle("Duplicate ISBN Test");
                duplicateIsbn.setPublisherId(1);
                duplicateIsbn.setCategoryId(1);
                duplicateIsbn.setImportPrice(50000);
                duplicateIsbn.setSellingPrice(75000);

                boolean dupResult = bookBUS.addBook(duplicateIsbn);
                System.out.println(
                        "   Result: " + (dupResult ? "ERROR - Should not add" : "SUCCESS - Blocked correctly"));
            }

            // ===== TEST 7: UPDATE STOCK =====
            System.out.println("\n\nTEST 7: Update stock");
            System.out.println("------------------------------------------------------------");

            if (books.size() > 0) {
                BookDTO testBook = books.get(0);
                int oldStock = testBook.getStockQuantity();
                System.out.println("Test book: " + testBook.getBookTitle());
                System.out.println("   Current stock: " + oldStock);

                // Test increase stock
                System.out.println("\n   Test INCREASE by 10:");
                boolean updateResult = bookBUS.updateStock(testBook.getBookId(), 10);
                System.out.println("   Result: " + (updateResult ? "SUCCESS" : "FAILED"));

                BookDTO updated = bookBUS.getBookById(testBook.getBookId());
                if (updated != null) {
                    System.out.println("   New stock: " + updated.getStockQuantity() +
                            " (expected: " + (oldStock + 10) + ")");
                }

                // Test decrease stock
                System.out.println("\n   Test DECREASE by 5:");
                updateResult = bookBUS.updateStock(testBook.getBookId(), -5);
                System.out.println("   Result: " + (updateResult ? "SUCCESS" : "FAILED"));

                // Restore original stock
                bookBUS.updateStock(testBook.getBookId(), -(10 - 5));
            }

            // ===== OVERALL STATISTICS =====
            System.out.println("\n\nOVERALL STATISTICS");
            System.out.println("------------------------------------------------------------");
            printBUSStatistics(books, lowStock);

            System.out.println("\n================================================================");
            System.out.println("                    TEST BUS COMPLETED                          ");
            System.out.println("================================================================");

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== HELPER METHODS =====

    private static void printSimpleList(ArrayList<BookDTO> books, int limit) {
        int count = Math.min(books.size(), limit);
        for (int i = 0; i < count; i++) {
            BookDTO book = books.get(i);
            System.out.printf("   %d. %-45s | %,12.0f VND | Stock: %4d%n",
                    (i + 1),
                    truncate(book.getBookTitle(), 45),
                    book.getSellingPrice(),
                    book.getStockQuantity());
        }
        if (books.size() > limit) {
            System.out.println("   ... and " + (books.size() - limit) + " more books");
        }
    }

    private static void printBUSStatistics(ArrayList<BookDTO> books, ArrayList<BookDTO> lowStock) {
        if (books.isEmpty())
            return;

        double totalValue = 0;
        int totalStock = 0;
        int availableCount = 0;

        for (BookDTO book : books) {
            totalValue += book.getSellingPrice() * book.getStockQuantity();
            totalStock += book.getStockQuantity();
            if ("available".equals(book.getStatus())) {
                availableCount++;
            }
        }

        System.out.println("  Total book titles       : " + books.size() + " titles");
        System.out.println("  Available for sale      : " + availableCount + " titles");
        System.out.println("  Total books in stock    : " + totalStock + " books");
        System.out.println("  Total inventory value   : " + String.format("%,d", (int) totalValue) + " VND");
        System.out.println("  Books with low stock    : " + lowStock.size() + " titles");
        if (totalStock > 0) {
            System.out.println("  Average value per book  : " +
                    String.format("%,d", (int) (totalValue / totalStock)) + " VND");
        }
    }

    private static String truncate(String str, int length) {
        if (str == null)
            return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}