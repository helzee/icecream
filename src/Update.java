import java.math.BigDecimal;
import java.sql.*;

public class Update {

    public static int updateOnCond(String table, String colName, String newVal,
            String condition) {
        try {
            PreparedStatement updateRows = App.conn
                    .prepareStatement("UPDATE ? SET ? = ? WHERE ?");
            updateRows.setString(1, table);
            updateRows.setString(2, "\'" + colName + "\'");
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

    public static void updateString(String entity, String column,
            String update, int id) throws SQLException {
        PreparedStatement updateRows = App.conn.prepareStatement(
                "UPDATE " + entity + " SET " + column + " = ? WHERE id = ? ;");

        updateRows.setString(1, update);
        updateRows.setInt(2, id);

        updateRows.executeUpdate();
        updateRows.close();
    }

    public static void updateBoolean(String entity, String column,
            String update, int id) throws SQLException {
        if (update.toLowerCase().contains("t")) {
            update = "true";
        } else {
            update = "false";
        }

        PreparedStatement updateRows = App.conn.prepareStatement(
                "UPDATE " + entity + " SET " + column + " = ? WHERE id = ? ;");

        updateRows.setBoolean(1, Boolean.parseBoolean(update));
        updateRows.setInt(2, id);

        updateRows.executeUpdate();
        updateRows.close();
    }

    public static void updateNumeric(String entity, String column,
            BigDecimal update, int id) throws SQLException {

        PreparedStatement updateRows = App.conn.prepareStatement(
                "UPDATE " + entity + " SET " + column + " = ? WHERE id = ? ;");

        updateRows.setBigDecimal(1, update);
        updateRows.setInt(2, id);

        updateRows.executeUpdate();
        updateRows.close();
    }

    public static void updateInt(String entity, String column, int update,
            int id) throws SQLException {

        PreparedStatement updateRows = App.conn.prepareStatement(
                "UPDATE " + entity + " SET " + column + " = ? WHERE id = ? ;");

        updateRows.setInt(1, update);
        updateRows.setInt(2, id);

        updateRows.executeUpdate();
        updateRows.close();
    }

}
