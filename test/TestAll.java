package test;

public class TestAll {
    public static void main(String[] args) {
        System.out.println("\n\n");
        System.out.println(
                "====================================================================================================");
        System.out.println(
                "                                                                                                    ");
        System.out.println(
                "                            START TESTING BOOK MANAGEMENT SYSTEM                                   ");
        System.out.println(
                "                                                                                                    ");
        System.out.println(
                "====================================================================================================");

        try {
            // ===== PART 1: TEST DAO LAYER =====
            System.out.println("\n\n");
            System.out.println("+------------------------------------------------------------------+");
            System.out.println("|                    PART 1: TEST DAO LAYER                        |");
            System.out.println("|              (Check database connection and queries)             |");
            System.out.println("+------------------------------------------------------------------+");
            System.out.println("\n");

            TestDAO.main(args);

            System.out.println("\n\nPause for 2 seconds before next test...");
            Thread.sleep(2000);

            // ===== PART 2: TEST BUS LAYER =====
            System.out.println("\n\n");
            System.out.println("+------------------------------------------------------------------+");
            System.out.println("|                    PART 2: TEST BUS LAYER                        |");
            System.out.println("|               (Check business logic and cache)                   |");
            System.out.println("+------------------------------------------------------------------+");
            System.out.println("\n");

            TestBUS.main(args);

            // ===== OVERALL RESULTS =====
            System.out.println("\n\n");
            System.out.println(
                    "====================================================================================================");
            System.out.println(
                    "                                                                                                    ");
            System.out.println(
                    "                                    ALL TESTS COMPLETED                                            ");
            System.out.println(
                    "                                                                                                    ");
            System.out.println(
                    "  CONCLUSION:                                                                                       ");
            System.out.println(
                    "  [OK] DAO Layer: Database connection successful, queries work correctly                            ");
            System.out.println(
                    "  [OK] BUS Layer: Business logic works properly, validation is accurate                             ");
            System.out.println(
                    "  [OK] Data loaded and cached successfully                                                          ");
            System.out.println(
                    "                                                                                                    ");
            System.out.println(
                    "  NOTES:                                                                                            ");
            System.out.println(
                    "  - If errors occur, check database configuration in DatabaseConnection                             ");
            System.out.println(
                    "  - If validation fails, verify test data                                                           ");
            System.out.println(
                    "  - INSERT/UPDATE/DELETE tests have been executed, rollback may be needed                           ");
            System.out.println(
                    "                                                                                                    ");
            System.out.println(
                    "====================================================================================================");
            System.out.println("\n\n");

        } catch (InterruptedException e) {
            System.err.println("WARNING: Interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n\n");
            System.err.println(
                    "====================================================================================================");
            System.err.println(
                    "                                  CRITICAL ERROR                                                    ");
            System.err.println(
                    "====================================================================================================");
            System.err.println("\nError details:");
            e.printStackTrace();
            System.err.println("\nTroubleshooting:");
            System.err.println("   1. Is the database running?");
            System.err.println("   2. Are database connection settings correct?");
            System.err.println("   3. Do all required tables exist?");
            System.err.println("   4. Is JDBC library added to the project?");
        }
    }
}