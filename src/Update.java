import java.sql.*;

public class Update {

    public static int updateOnCond(String table, String colName, String newVal,
            String condition) {
        try {
            PreparedStatement updateTable = App.conn.prepareStatement(
                    "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                            + "VALUES (?,?,?,?,?)");
            return updateTable == null ? 0 : 0;

        } catch (SQLException e) {
            System.out.println("Could not update " + table
                    + " based on condition: " + condition);
            return 0;
        }
    }
}
