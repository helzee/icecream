import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class Transaction {

    private static final char[] txChars = new char[] { '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z' };
    public static final int txNumberSize = 10;
    private String txNumber;
    private int txID;

    public Transaction(String badgeNumber) throws SQLException {
        ResultSet rs = Execute
                .runQuery("EXECUTE getEmployeeID(\'" + badgeNumber + "\');");
        rs.next();
        int employeeID = rs.getInt(1);
        rs.close();

        initializeTransaction(employeeID);
    }

    public Transaction(int employeeID) throws SQLException {
        initializeTransaction(employeeID);
    }

    private void initializeTransaction(int employeeID) throws SQLException {
        txNumber = getTxNumber();

        PreparedStatement newTx = App.conn.prepareStatement(
                "INSERT INTO Transaction (employeeWorking, transactionNumber)"
                        + " VALUES (?,?) RETURNING ID;");
        newTx.setInt(1, employeeID);
        newTx.setString(2, txNumber);
        if (!newTx.execute())
            throw new SQLException();

        ResultSet rs = newTx.getResultSet();
        rs.next();
        txID = rs.getInt(1);
        rs.close();
        newTx.close();
    }

    public int addProduct(int productID) {
        try {
            PreparedStatement findPrice = App.conn.prepareStatement(
                    "SELECT currentPrice FROM MenuProduct WHERE ID = ?;");
            findPrice.setInt(1, productID);
            ResultSet rs1 = findPrice.executeQuery();
            rs1.next();
            BigDecimal currentPrice = rs1.getBigDecimal(1);
            rs1.close();
            findPrice.close();

            PreparedStatement newTxProd = App.conn.prepareStatement(
                    "INSERT INTO TransactionProduct (productID, transactionID, salesPrice, isRefunded)"
                            + "VALUES (?,?,?,true) RETURNING ID;");
            newTxProd.setInt(1, productID);
            newTxProd.setInt(2, txID);
            newTxProd.setBigDecimal(3, currentPrice);

            return Execute.execAndFetchID(newTxProd);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert TransactionProduct: "
                    + productID + " " + txNumber);
            return -1;
        }
    }

    public boolean addProductModification(int txProdID, int menuModID) {

        try {
            PreparedStatement findPrice = App.conn.prepareStatement(
                    "SELECT currentPrice FROM MenuModification WHERE ID = ?;");
            findPrice.setInt(1, menuModID);
            ResultSet rs = findPrice.executeQuery();
            rs.next();
            BigDecimal currentPrice = rs.getBigDecimal(1);
            rs.close();
            findPrice.close();

            PreparedStatement newProdMod = App.conn.prepareStatement(
                    "INSERT INTO Modification (transactionProductID, menuModificationID, salesPrice)"
                            + "VALUES (?,?,?);");
            newProdMod.setInt(1, txProdID);
            newProdMod.setInt(2, menuModID);
            newProdMod.setBigDecimal(3, currentPrice);

            if (newProdMod.executeUpdate() != 1)
                throw new SQLException();

            // App.conn.commit();
            return true;

        } catch (SQLException e) {
            // App.conn.rollback();
            e.printStackTrace();
            System.out.println("Could not insert Product Modification: "
                    + txProdID + " " + menuModID);
            return false;
        }
    }

    public boolean removeProductIngredient(int txProdID, int itemID) {

        try {
            ResultSet rs = App.st.executeQuery("EXECUTE itemPartOfTxProd(\'"
                    + txProdID + " \',\'" + itemID + "\');");
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            if (count != 1)
                throw new SQLException();

            PreparedStatement newProdMod = App.conn.prepareStatement(
                    "INSERT INTO RemoveIngredient (transactionProductID, itemID)"
                            + "VALUES (?,?);");
            newProdMod.setInt(1, txProdID);
            newProdMod.setInt(2, itemID);

            if (newProdMod.executeUpdate() != 1)
                throw new SQLException();

            // App.conn.commit();
            return true;

        } catch (SQLException e) {
            // App.conn.rollback();
            e.printStackTrace();
            System.out.println("Could not insert Remove Ingredient: "
                    + txProdID + " " + itemID);
            return false;
        }
    }

    public boolean finishTransaction() {
        try {
            int count = App.st.executeUpdate(
                    "UPDATE Transaction SET timeCompleted = \'now\'::timestamp WHERE ID = "
                            + txID);
            if (count != 1)
                throw new SQLException();

            // App.conn.commit();
            return true;

        } catch (SQLException e) {
            // App.conn.rollback();
            e.printStackTrace();
            System.out.println("Could not finish transaction: " + txNumber);
            return false;
        }
    }

    public String getReceipt() {
        return Receipt.getReceipt(txNumber);
    }

    private static String getTxNumber() {
        String newTxNum = "";
        ResultSet rs;
        Random gen = new Random();

        try {
            while (true) {
                for (int i = 0; i < txNumberSize; i++)
                    newTxNum += Character
                            .toString(txChars[gen.nextInt(46) % 36]);

                rs = Execute
                        .runQuery("EXECUTE txAvail(\'" + newTxNum + "\');");
                rs.next();
                if (rs.getInt(1) == 0) {
                    rs.close();
                    return newTxNum;
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not get a new transaction number");
            System.exit(1);
            return null;
        }
    }
}