import java.sql.*;
import java.util.*;
import java.io.*;

public class App {

    private static int maxColSize = 20;
    private static Connection conn;

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

        executeFile(st, "env/icecreamDB.txt");
        executeFile(st, "env/populateData.txt");
        executeFile(st, "env/preparedQueries.txt");

        printRS(runQuery(st, "EXECUTE getEmployee(\'" + "j%" + "\');"));
        printRS(runQuery(st, "SELECT * FROM Employee;"));

        // PREPARED STATEMENT VERSION
        PreparedStatement getEmpByName = conn.prepareStatement(
                "SELECT firstName, lastName FROM Employee WHERE firstName ILIKE ? AND lastName ILIKE ? ORDER BY firstName, lastName");

        getEmpByName.setString(1, "Josh");
        getEmpByName.setString(2, "Helzerman");
        printRS(getEmpByName.executeQuery());

        getEmpByName.setString(1, "Griffin");
        getEmpByName.setString(2, "Detracy");
        printRS(getEmpByName.executeQuery());

        // we could hide prepared statements in functions like this
        printRS(getEmployee("Alex", "Lambert"));

        // make methods for all CRUD commands. This sanitizes inputs and minimizes the
        // code required. We could even make a badgeNumber generator function for unique
        // badge numbers.
        insertEmployee("Y432", "jimmy", "bob", "222-333-4444",
                "jimmybob@bob.com");

        printRS(runQuery(st, "SELECT * FROM Employee;"));

        conn.rollback();
        st.close();
    }

    public static int insertEmployee(String badgeNumber, String firstName,
            String lastName, String phoneNumber, String email)
            throws SQLException {
        PreparedStatement insertEmployee = conn.prepareStatement(
                "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                        + "VALUES (?,?,?,?,?)");
        insertEmployee.setString(1, badgeNumber);
        insertEmployee.setString(2, firstName);
        insertEmployee.setString(3, lastName);
        insertEmployee.setString(4, phoneNumber);
        insertEmployee.setString(5, email);
        return insertEmployee.executeUpdate();
    }

    public static ResultSet getEmployee(String firstName, String lastName)
            throws SQLException {
        PreparedStatement getEmpByName = conn.prepareStatement(
                "SELECT firstName, lastName FROM Employee WHERE firstName ILIKE ? AND lastName ILIKE ? ORDER BY firstName, lastName");
        getEmpByName.setString(1, firstName);
        getEmpByName.setString(2, lastName);
        return getEmpByName.executeQuery();

    }

    public static void executeFile(Statement st, String filePath) {
        String nextLine = "";

        try {

            Scanner fileScanner = new Scanner(new File(filePath));
            fileScanner.useDelimiter(";");

            while (fileScanner.hasNext()) {
                nextLine = fileScanner.next() + ";";
                // System.out.println(nextLine);
                st.execute(nextLine);
            }

            System.out.println("Finished executing file \'" + filePath + "\'");
            fileScanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + filePath);
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Failed to execute:\n" + nextLine);
            System.exit(1);
        }
    }

    public static ResultSet runQuery(Statement st, String query) {
        try {
            return st.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("Failed to execute :\n" + query);
            System.exit(1);
        }

        return null;
    }

    public static void printRS(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData();

            int cols = md.getColumnCount();
            int[] colWidths = printColNames(md);
            int totalWidth = colWidths[0];
            String toPrint;
            int colWidth;

            while (rs.next()) {
                for (int col = 1; col <= cols; col++) {
                    toPrint = rs.getString(col);
                    colWidth = Math.min(colWidths[col], toPrint.length());

                    System.out.print(toPrint.substring(0, colWidth));
                    System.out.print(
                            " ".repeat(colWidths[col] - colWidth) + " | ");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Could not print ResultSet");
        }
    }

    // Returns the total width of the table
    private static int[] printColNames(ResultSetMetaData md)
            throws SQLException {
        int cols = md.getColumnCount();
        String colName;
        int colWidth, colNameWidth;
        int[] colWidths = new int[cols + 1];
        int totalWidth = 0;

        System.out.println();

        for (int col = 1; col <= cols; col++) {

            colWidth = Math.min(md.getColumnDisplaySize(col), maxColSize);
            colWidths[col] = colWidth;

            colNameWidth = Math.min(md.getColumnLabel(col).length(), colWidth);
            colName = md.getColumnLabel(col).substring(0, colNameWidth);
            totalWidth += colWidth + 3;

            System.out.print(colName);
            System.out.print(" ".repeat(colWidth - colNameWidth) + " | ");
        }

        System.out.println("\n" + "-".repeat(totalWidth));

        colWidths[0] = totalWidth;
        return colWidths;
    }
}
