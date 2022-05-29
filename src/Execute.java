import java.sql.*;
import java.util.*;
import java.io.*;

public class Execute {

    public static void executeFile(String filePath) {
        String nextLine = "";

        try {

            Scanner fileScanner = new Scanner(new File(filePath));
            fileScanner.useDelimiter(";");

            while (fileScanner.hasNext()) {
                nextLine = fileScanner.next() + ";";
                // System.out.println(nextLine);
                App.st.execute(nextLine);
            }

            System.out.println("Finished executing file \'" + filePath + "\'");
            fileScanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("Could not find file: " + filePath);
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Failed to execute:\n" + nextLine);
            System.exit(1);
        }
    }

    public static ResultSet runQuery(String query) {
        try {
            Statement st = App.conn.createStatement();
            return st.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("Failed to execute :\n" + query);
            e.printStackTrace();
            // System.exit(1);
        }

        return null;
    }

    public static void printRS(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData();

            int cols = md.getColumnCount();
            int[] colWidths = printColNames(md);
            int totalWidth = colWidths[0];
            String toPrint;
            int colWidth;

            while (rs.next()) {
                System.out.print(" | ");
                for (int col = 1; col <= cols; col++) {
                    toPrint = rs.getString(col);

                    if (toPrint != null) {
                        colWidth = Math.min(colWidths[col], toPrint.length());
                        System.out.print(toPrint.substring(0, colWidth));
                    } else
                        colWidth = 0;

                    System.out.print(
                            " ".repeat(colWidths[col] - colWidth) + " | ");
                }
                System.out.println();
            }
            System.out.println("-".repeat(totalWidth));

        } catch (SQLException e) {
            System.out.println("Could not print ResultSet");
        }
    }

    // Returns the total width of the table
    private static int[] printColNames(ResultSetMetaData md)
            throws SQLException {
        int cols = md.getColumnCount();
        String colName;
        int colWidth, colNameWidth;
        int[] colWidths = new int[cols + 1];
        int totalWidth = 3;

        System.out.print("\n | ");

        for (int col = 1; col <= cols; col++) {

            colWidth = Math.min(md.getColumnDisplaySize(col), App.maxColSize);
            colWidths[col] = colWidth;

            colNameWidth = Math.min(md.getColumnLabel(col).length(), colWidth);
            colName = md.getColumnLabel(col).substring(0, colNameWidth);
            totalWidth += colWidth + 3;

            System.out.print(colName);
            System.out.print(" ".repeat(colWidth - colNameWidth) + " | ");
        }

        System.out.println("\n" + "-".repeat(totalWidth));

        colWidths[0] = totalWidth;
        return colWidths;
    }

    public static void printQuery(String query) {
        printRS(runQuery(query));
    }

    public static int execAndFetchID(PreparedStatement ps)
            throws SQLException {
        if (!ps.execute()) {
            ps.close();
            throw new SQLException();
        }

        ResultSet rs = ps.getResultSet();
        rs.next();
        int newID = rs.getInt(1);
        rs.close();
        ps.close();

        return newID;
    }

}
