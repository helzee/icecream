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

        exec.executeFile(st, "env/icecreamDB.txt");
        exec.executeFile(st, "env/populateData.txt");
        exec.executeFile(st, "env/preparedQueries.txt");

        exec.printQuery(st, "EXECUTE getEmployee(\'" + "j%" + "\');");
        exec.printQuery(st, "SELECT * FROM Employee;");

        // PREPARED STATEMENT VERSION
        PreparedStatement getEmpByName = conn.prepareStatement(
                "SELECT firstName, lastName FROM Employee WHERE firstName ILIKE ? AND lastName ILIKE ? ORDER BY firstName, lastName");

        getEmpByName.setString(1, "Josh");
        getEmpByName.setString(2, "Helzerman");
        exec.printRS(getEmpByName.executeQuery());

        getEmpByName.setString(1, "Griffin");
        getEmpByName.setString(2, "Detracy");
        exec.printRS(getEmpByName.executeQuery());

        // we could hide prepared statements in functions like this
        exec.printRS(getEmployee("Alex", "Lambert"));

        // make methods for all CRUD commands. This sanitizes inputs and minimizes the
        // code required. We could even make a badgeNumber generator function for unique
        // badge numbers.
        Insert.insertEmployee("Y432", "jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");

        exec.printQuery(st, "SELECT * FROM Employee;");

        conn.rollback();
        st.close();
    }

    public static ResultSet getEmployee(String firstName, String lastName)
            throws SQLException {
        PreparedStatement getEmpByName = conn.prepareStatement(
                "SELECT firstName, lastName FROM Employee WHERE firstName ILIKE ? AND lastName ILIKE ? ORDER BY firstName, lastName");
        getEmpByName.setString(1, firstName);
        getEmpByName.setString(2, lastName);
        return getEmpByName.executeQuery();

    }
}
