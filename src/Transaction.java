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
        Statement st = App.conn.createStatement();
        ResultSet rs = Execute.runQuery(st,
                "EXECUTE getEmployeeID(\'" + badgeNumber + "\');");
        rs.next();
        int employeeID = rs.getInt(1);
        rs.close();
        st.close();

        initializeTransaction(employeeID);
    }

    public Transaction(int employeeID) throws SQLException {
        initializeTransaction(employeeID);
    }

    private void initializeTransaction(int employeeID) throws SQLException {
        txNumber = getTxNumber();

        PreparedStatement newTx = App.conn.prepareStatement(
                "INSERT INTO Transaction (employeeWorking, transactionNumber, timeCompleted)"
                        + " VALUES (?,?,\'now\'::timestamp) RETURNING ID;");
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

            if (!newTxProd.execute()) {
                newTxProd.close();
                // App.conn.rollback();
                return -1;
            }

            ResultSet rs = newTxProd.getResultSet();
            rs.next();
            int newID = rs.getInt(1);
            rs.close();
            newTxProd.close();

            return newID;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert TransactionProduct: "
                    + productID + " " + txNumber);
            return -1;
        }
    }

    private static String getTxNumber() {
        String newTxNum = "";
        Statement st;
        ResultSet rs;
        Random gen = new Random();

        try {
            st = App.conn.createStatement();

            while (true) {
                for (int i = 0; i < txNumberSize; i++)
                    newTxNum += Character
                            .toString(txChars[gen.nextInt(46) % 36]);

                rs = Execute.runQuery(st,
                        "EXECUTE txAvail(\'" + newTxNum + "\');");
                rs.next();
                if (rs.getInt(1) == 0) {
                    rs.close();
                    st.close();
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