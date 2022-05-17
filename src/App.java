import java.sql.*;
import java.util.*;
import java.io.*;

public class App {

    private static int maxColSize = 20;

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

        Connection conn = DriverManager.getConnection(url, props);

        // make sure not auto commit changes (allows for rollback and commit)
        conn.setAutoCommit(false);

        Statement st = conn.createStatement();

        executeFile(st, "env/icecreamDB.txt");
        executeFile(st, "env/populateData.txt");
        executeFile(st, "env/preparedQueries.txt");

        printRS(runQuery(st, "EXECUTE getEmployee(\'" + "j%" + "\');"));
        printRS(runQuery(st, "SELECT * FROM Employee;"));

        conn.rollback();
        st.close();
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
            printColNames(md);

            int cols = md.getColumnCount();

        } catch (SQLException e) {
            System.out.println("Could not print ResultSet");
        }
    }

    // Returns the total width of the table
    private static int printColNames(ResultSetMetaData md)
            throws SQLException {
        int cols = md.getColumnCount();
        String colName;
        int colWidth, colNameWidth;
        int totalWidth = 0;

        System.out.println();

        for (int col = 1; col < cols; col++) {

            colWidth = Math.min(md.getColumnDisplaySize(col), maxColSize);
            colNameWidth = Math.min(md.getColumnLabel(col).length(), colWidth);
            colName = md.getColumnLabel(col).substring(0, colNameWidth);
            totalWidth += colWidth + 3;

            System.out.print(colName);
            System.out.print(" ".repeat(colWidth - colNameWidth) + " | ");
        }

        // Print the last column name
        colWidth = Math.min(md.getColumnDisplaySize(cols), maxColSize);
        colNameWidth = Math.min(md.getColumnLabel(cols).length(), colWidth);
        colName = md.getColumnLabel(cols).substring(0, colNameWidth);
        totalWidth += colWidth;

        System.out.println(colName);
        System.out.println("-".repeat(totalWidth));
        return totalWidth;
    }
}
