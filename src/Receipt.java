import java.sql.*;
import java.util.LinkedList;

public class Receipt {
    private static final int recWidth = 40;
    private static final String header = "\nFrozen Rock Ice Cream Shop\n"
            + "7643 5th Ave. Bothell, WA, 98011-8246\n734-848-6321\n\n";

    public static String getReceipt(String txNumber) {
        String empName = Format.rsToString(
                Execute.runQuery("EXECUTE getEmpNameForTx(\'"
                        + txNumber + "\');"));
        String date = Format.rsToString(Execute.runQuery(
                "EXECUTE getTxDate(\'" + txNumber + "\');"));
        String time = Format.rsToString(Execute.runQuery(
                "EXECUTE getTxTime(\'" + txNumber + "\');"));

        String toReturn = header + "Transaction Number: " + txNumber
                + "\n\nHost: " + empName + "\n" + date
                + " ".repeat(recWidth - 18) + time + "\n"
                + "-".repeat(recWidth) + "\nItem"
                + " ".repeat(recWidth - 10) + " Price\n";

        int nameWidth;
        String nameTemp;
        LinkedList<txProd> prods = new LinkedList<txProd>();
        ResultSet rs;

        try {
            // Get all the TransactionProducts
            rs = Execute.runQuery("EXECUTE getTxProds(\'"
                    + txNumber + "\');");
            while (rs.next()) {
                prods.add(new txProd(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)));
            }

            for (txProd tp : prods) {
                // Print the TransactionProduct
                nameWidth = Math.min(tp.name.length(),
                        recWidth - 8);
                toReturn += "\n" + tp.name.substring(0,
                        nameWidth)
                        + " ".repeat(recWidth - 8
                                - nameWidth)
                        + " " + tp.price + "\n";

                // Find and print all of its Modifications
                rs = Execute.runQuery("EXECUTE getMods(\'"
                        + tp.txProdID + "\');");
                while (rs.next()) {
                    nameTemp = rs.getString(1);
                    nameWidth = Math.min(nameTemp.length(),
                            recWidth - 12);
                    toReturn += "  + " + nameTemp
                            .substring(0, nameWidth)
                            + " ".repeat(recWidth
                                    - 12
                                    - nameWidth)
                            + " " + rs.getString(2)
                            + "\n";
                }

                // Find and print all of its RemoveIngredients
                rs = Execute.runQuery("EXECUTE getRemoves(\'"
                        + tp.txProdID + "\');");
                while (rs.next()) {
                    nameTemp = rs.getString(1);
                    nameWidth = Math.min(nameTemp.length(),
                            recWidth - 4);
                    toReturn += "  - " + nameTemp
                            .substring(0, nameWidth)
                            + "\n";
                }
            }

            toReturn += "\n" + "-".repeat(recWidth) + "\n\n";

            rs = Execute.runQuery("EXECUTE getTxTotal(\'"
                    + txNumber + "\');");
            rs.next();
            toReturn += "Subtotal:" + " ".repeat(recWidth - 17)
                    + rs.getString(1) + "\nTax:"
                    + " ".repeat(recWidth - 12)
                    + rs.getString(2) + "\n\nTotal: "
                    + " ".repeat(recWidth - 15)
                    + rs.getString(3) + "\n";

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

        return toReturn;
    }
}

class txProd {
    public int txProdID;
    public String name;
    public String price;

    public txProd(int item, String name, String price) {
        this.txProdID = item;
        this.name = name;
        this.price = price;
    }
}