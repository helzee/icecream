import java.sql.*;
import java.util.*;

public class Format {
    public static String[] rsToArray(ResultSet rs) {
        try {
            LinkedList<String> vals = new LinkedList<String>();

            String val;
            while (rs.next()) {
                val = rs.getString(1);
                vals.add(val == null ? "" : val);
            }

            String[] toReturn = new String[vals.size()];
            int i = 0;
            for (String row : vals)
                toReturn[i++] = row;

            return toReturn;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[][] rsTo2DArray(ResultSet rs) {
        try {
            int columns = rs.getMetaData().getColumnCount();
            LinkedList<String>[] vals = new LinkedList[columns];
            for (int i = 0; i < columns; i++)
                vals[i] = new LinkedList<String>();

            String val;
            while (rs.next()) {
                for (int col = 0; col < columns; col++) {
                    val = rs.getString(col + 1);
                    vals[col].add(val == null ? "" : val);
                }
            }

            String[][] toReturn = new String[columns][vals[0].size()];
            int i;
            for (int col = 0; col < columns; col++) {
                i = 0;
                for (String val2 : vals[col])
                    toReturn[col][i++] = val2;
            }

            return toReturn;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
