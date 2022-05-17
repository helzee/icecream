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

        Connection conn;
        conn = DriverManager.getConnection(url, props);
        Statement st = conn.createStatement();

        conn.setAutoCommit(false);

        Scanner makeDB = new Scanner(new File("env/icecreamDB.txt"));
        makeDB.useDelimiter(";");

        while (makeDB.hasNext()) {
            String nextLine = makeDB.next() + ";";
            System.out.println(nextLine);
            st.execute(nextLine);
        }

        Scanner makePreloaded = new Scanner(
                new File("env/preloadedQueries.txt"));
        makePreloaded.useDelimiter(";");

        while (makePreloaded.hasNext()) {
            String nextLine = makePreloaded.next() + ";";
            System.out.println(nextLine);
            st.execute(nextLine);
        }

        ResultSet prepRS = st.executeQuery("EXECUTE getEmployees;");
        while (prepRS.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(
                    prepRS.getString(1) + " | " + prepRS.getString(2));
        }

        ResultSet rs = st.executeQuery("SELECT * FROM Employee");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1) + " | " + rs.getString(2));
        }

        conn.rollback();

        rs.close();
        st.close();
    }
}
