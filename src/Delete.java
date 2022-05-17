import java.sql.*;

public class Delete {

    public static int deleteOnCond(String table, String condition) {
        try {
            PreparedStatement delRows = App.conn.prepareStatement(
                    "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                            + "VALUES (?,?,?,?,?)");
            return delRows == null ? 0 : 0;

        } catch (SQLException e) {
            System.out.println("Could not delete from " + table
                    + " based on condition: " + condition);
            return 0;
        }
    }
}
