import java.sql.*;
import java.util.*;
import java.io.*;

public class App {
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
        conn.setAutoCommit(false);
        Statement st = conn.createStatement();

        executeFile(st, "env/icecreamDB.txt");
        executeFile(st, "env/preparedQueries.txt");

        ResultSet rs1 = runQuery(st,
                "EXECUTE getEmployee(\'" + "jo%" + "\');");
        ResultSet rs2 = runQuery(st, "SELECT * FROM Employee;");

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
            System.out.println("Failed to execute:\n" + query);
            System.exit(1);
        }

        return null;
    }
}
