import java.sql.*;
import java.util.*;
import java.io.*;

public class App {

    public static final int maxColSize = 20;
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

        Exec.executeFile(st, "env/icecreamDB.txt");
        Exec.executeFile(st, "env/populateData.txt");
        Exec.executeFile(st, "env/preparedQueries.txt");

        // PREPARED STATEMENT VERSION
        Exec.printRS(getEmployee("Griffin", "%"));

        Insert.insertEmployee("Y432", "jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");

        Exec.printQuery(st, "EXECUTE getEmployee(\'" + "j%" + "\');");
        Exec.printQuery(st, "SELECT * FROM Employee;");

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
