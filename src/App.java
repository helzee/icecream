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
        conn.setAutoCommit(false); // make sure not auto commit changes (allows for rollback and commit)
        Statement st = conn.createStatement();

        executeFile(st, "env/icecreamDB.txt");
        executeFile(st, "env/preparedQueries.txt");

<<<<<<< HEAD
        System.out.println(runQuery(st, "EXECUTE getEmployees;"));
        System.out.println(runQuery(st, "SELECT * FROM Employee;"));
=======
        ResultSet rs1 = runQuery(st,
                "EXECUTE getEmployee(\'" + "jo%" + "\');");
        ResultSet rs2 = runQuery(st, "SELECT * FROM Employee;");
>>>>>>> a96cef6454cd93db8e9c20e5339f5f46aec1b3cd

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
                System.out.println(nextLine);
                st.execute(nextLine);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to execute :\n" + nextLine);
            System.exit(1);
        }
    }

    public static String runQuery(Statement st, String query) {
        String toReturn = "";

        try {
            ResultSet rs = st.executeQuery("SELECT * FROM Employee");
            while (rs.next()) {
                toReturn += rs.getString(1) + " | " + rs.getString(2);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Failed to execute :\n" + query);
            System.exit(1);
        }

        return toReturn;
    }
}
