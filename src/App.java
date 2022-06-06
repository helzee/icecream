import java.sql.*;
import java.util.*;
import java.io.*;

public class App {

    public static final int maxColSize = 30;
    public static Connection conn;
    public static Statement st;

    public static void main(String[] args) throws Exception {
        setUpConn();

        // Do not need to run, it is permanent now
        /* Execute.executeFile(st, "env/icecreamDB.psql"); */

        // Disappears after run time, must run every time
        Execute.executeFile("env/preparedQueries.psql");
        // killEmptyTransactions();

        // basicTest1();
        Gui.build();
    }

    private static void killEmptyTransactions() throws SQLException {
        Execute.printQuery(
                "SELECT * FROM Transaction T LEFT JOIN TransactionProduct TP ON TP.transactionID = T.ID WHERE TP.ID IS NULL;");
        st.executeUpdate(
                "DELETE FROM Transaction WHERE ID IN (SELECT T.ID FROM Transaction T LEFT JOIN TransactionProduct TP ON TP.transactionID = T.ID WHERE TP.ID IS NULL);");

        Execute.printQuery(
                "SELECT * FROM Transaction T LEFT JOIN TransactionProduct TP ON TP.transactionID = T.ID WHERE TP.ID IS NULL;");
        conn.commit();

    }

    private static void setUpConn() throws SQLException {
        Scanner creds = null;
        try {
            creds = new Scanner(new File("env/credentials"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String url = creds.nextLine();
        Properties props = new Properties();
        props.setProperty("user", creds.nextLine());
        props.setProperty("password", creds.nextLine());

        conn = DriverManager.getConnection(url, props);

        // make sure not auto commit changes (allows for rollback and commit)
        conn.setAutoCommit(false);

        st = conn.createStatement();
    }

    private static void populateDB() {
        populateEmployees();
        populateItems();

    }

    private static void populateEmployees() {
        try {
            Scanner input = new Scanner(new File("env/employees.txt"));
            while (input.hasNext()) {
                Insert.insertEmployee(input.next(), input.next(), input.next(),
                        input.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // populate items from txt file
    private static void populateItems() {
        try {
            Scanner input = new Scanner(new File("env/items.txt"));

            input.useDelimiter("\\s*;\\s*");

            while (input.hasNext()) {
                Insert.insertItem(input.next(), input.next(),
                        input.nextBoolean(), input.nextDouble());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void basicTest1() throws SQLException {
        populateDB();

        int jimmyID = Insert.insertEmployee("jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");
        int vanillaID = Insert.insertItem("Vanilla", "desc", true, 1.5);
        int sprinkleID = Insert.insertItem("Sprinkles", "desc2", true, 0.001);
        int modID = Insert.insertMenuMod(sprinkleID, 0.7, 0.01, "Sprinkles");
        int sundaeID = Insert.insertMenuCategory("Sundae");
        int van1Scoop = Insert.insertMenuProduct(sundaeID, 10.4,
                "Vanilla Sundae", "desc3");
        int van2Scoop = Insert.insertMenuProduct(sundaeID, 12.4,
                "Vanilla Sundae 2 scp", "desc3");
        Insert.insertProductIngredient(van1Scoop, vanillaID, 4.7);

        Transaction newTx = new Transaction(jimmyID);
        int txProd = newTx.addProduct(van1Scoop);
        newTx.addProductModification(txProd, modID);
        newTx.removeProductIngredient(txProd, vanillaID);
        newTx.finishTransaction();
        System.out.println(newTx.getReceipt());

        // conn.rollback();

    }
}
