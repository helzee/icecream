import java.sql.*;
import java.util.*;
import java.io.*;

public class App {

    public static final int maxColSize = 30;
    public static Connection conn;

    public static void main(String[] args) throws Exception {
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
        Statement st = conn.createStatement();

        Execute.executeFile(st, "env/icecreamDB.psql");
        Execute.executeFile(st, "env/populateData.psql");
        Execute.executeFile(st, "env/preparedQueries.psql");

        int jimmyID = Insert.insertEmployee("jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");
        int vanillaID = Insert.insertItem("Vanilla", "desc", true, 1.5);
        int sprinkleID = Insert.insertItem("Sprinkles", "desc2", true, 0.001);

        int modID = Insert.insertMenuMod(sprinkleID, 0.7, 0.01, "Sprinkles",
                "desc2");
        Execute.printQuery(st,
                "SELECT * FROM MenuModification WHERE ID = " + modID + ";");

        int sundaeID = Insert.insertMenuCategory("Sundae");
        int van1Scoop = Insert.insertMenuProduct(sundaeID, 1.4,
                "Vanilla Sundae", "desc3");
        Insert.insertProductIngredient(van1Scoop, vanillaID, 4.7);

        Transaction newTx = new Transaction(jimmyID);
        newTx.addProduct(van1Scoop);
        Execute.printQuery(st, "SELECT * FROM Transaction;");
        Execute.printQuery(st, "SELECT * FROM TransactionProduct;");

        conn.rollback();
        st.close();

        Gui.build();
    }
}
