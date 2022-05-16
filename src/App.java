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
        ResultSet rs = st.executeQuery("SELECT * FROM Employee");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1) + " | " + rs.getString(2));
        }
        rs.close();
        st.close();
    }
}
