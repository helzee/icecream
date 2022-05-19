import java.sql.*;

public class Update {

    public static int updateOnCond(String table, String colName, String newVal,
            String condition) {
        try {
            PreparedStatement updateRows = App.conn
                    .prepareStatement("UPDATE ? SET ? = ? WHERE ?");
            updateRows.setString(1, table);
            updateRows.setString(2, colName);
            updateRows.setString(3, newVal);
            updateRows.setString(4, condition);

            int count = updateRows.executeUpdate();
            updateRows.close();
            return count;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not update " + table
                    + " based on condition: " + condition);
            return 0;
        }
    }
}
