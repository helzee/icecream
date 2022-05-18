import java.sql.*;
import java.util.*;
import java.util.Random;

public class Transaction {

    private static final char[] txChars = new char[] { 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0' };
    public static final int txNumberSize = 10;

    private LinkedList<PreparedStatement> commands;

    public Transaction(String badgeNumber) {
        commands = new LinkedList<PreparedStatement>();
        String txNumber = getTxNumber();

        try {
            Statement st = App.conn.createStatement();
            ResultSet rs = Exec.runQuery(st,
                    "EXECUTE getEmployeeID(\'" + badgeNumber + "\');");
            rs.next();
            int employeeID = rs.getInt(1);
            rs.close();
            st.close();

            PreparedStatement newTx = App.conn.prepareStatement(
                    "INSERT INTO Transaction (employeeWorking, transactionNumber, timeCompleted)"
                            + " VALUES (?,?,\'now\'::timestamp)");
            newTx.setInt(1, employeeID);
            newTx.setString(2, txNumber);
            commands.add(newTx);

        } catch (SQLException e) {
            System.out.println("Could not create transaction: " + badgeNumber);
        }
    }

    public boolean finishTransaction() {

        try {
            int toReturn;
            for (PreparedStatement ps : commands) {
                toReturn = ps.executeUpdate();
                ps.close();

                if (toReturn != 1)
                    return false;
            }
            return true;

        } catch (SQLException e) {
            System.out.println("Could not finalize transaction");
            return false;
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
                    newTxNum += Character.toString(txChars[gen.nextInt(36)]);

                rs = Exec.runQuery(st,
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
