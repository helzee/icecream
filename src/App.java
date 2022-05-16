import java.sql.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String url = "jdbc:postgresql://ls-cf9a0200fedbfc82507af960bd065ffa98f745d8.cpn0irbq7t2g.us-west-2.rds.amazonaws.com:5432/icecreamshop";
        Properties props = new Properties();
        props.setProperty("user", "icecreamadmin");
        props.setProperty("password", "cream");
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
