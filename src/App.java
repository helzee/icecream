import java.sql.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String url = "jdbc:postgresql://localhost/db03";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "css475");
        Connection conn;

        conn = DriverManager.getConnection(url, props);

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Employee");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1) + " | " + rs.getString(2));
        }
        rs.close();
        st.close();
    }
}
