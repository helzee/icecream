import java.sql.*;

public class Delete {

    public static int deleteOnCond(String table, String condition) {
        try {
            PreparedStatement delRows = App.conn
                    .prepareStatement("DELETE FROM ? WHERE ?;");
            delRows.setString(1, table);
            delRows.setString(2, condition);

            int count = delRows.executeUpdate();
            delRows.close();
            return count;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not delete from " + table
                    + " based on condition: " + condition);
            return 0;
        }
    }
}
