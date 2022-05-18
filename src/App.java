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

        System.out.println("Hello, World!");

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

        // PREPARED STATEMENT VERSION
        Execute.printRS(getEmployee("Griffin", "%"));

        Insert.insertEmployee("jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");

        Execute.printQuery(st, "EXECUTE getEmployee(\'" + "j%" + "\');");
        Execute.printQuery(st, "SELECT * FROM Employee;");

        Transaction nexTx = new Transaction("X12345");
        nexTx.finishTransaction();
        Execute.printQuery(st, "SELECT * FROM Transaction;");

        Insert.insertItem("Vanilla", "desc", true, 1.5);
        int sprinkleID = Insert.insertItem("Sprinkles", "desc2", true, 0.001);
        int modID = Insert.insertMenuMod(sprinkleID, 0.7, 0.01, "Sprinkles",
                "desc2");
        Execute.printQuery(st, "SELECT * FROM Item;");
        Execute.printQuery(st,
                "SELECT * FROM MenuModification WHERE ID = " + modID + ";");

        int sundaeID = Insert.insertMenuCategory("Sundae");
        Execute.printQuery(st,
                "SELECT * FROM MenuCategory WHERE ID = " + sundaeID + ";");

        conn.rollback();
        st.close();
    }

    public static ResultSet getEmployee(String firstName, String lastName) {
        try {
            PreparedStatement getEmpByName = conn.prepareStatement(
                    "SELECT firstName, lastName FROM Employee WHERE firstName ILIKE ? AND lastName ILIKE ? ORDER BY firstName, lastName");
            getEmpByName.setString(1, firstName);
            getEmpByName.setString(2, lastName);
            return getEmpByName.executeQuery();

        } catch (SQLException e) {
            System.out.println(
                    "Failed to getEmployee " + firstName + "  " + lastName);
            return null;
        }
    }
}
