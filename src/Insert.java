import java.sql.*;
import java.util.Random;

public class Insert {

    private static char[] badgeChars = new char[] { 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static char[] badgeNums = new char[] { '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0' };
    public static final int badgeNumberSize = 6;

    public static int insertEmployee(String firstName, String lastName,
            String phoneNumber, String email) {

        String badgeNumber = getBadgeNumber();

        try {
            PreparedStatement insertEmployee = App.conn.prepareStatement(
                    "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                            + "VALUES (?,?,?,?,?)");
            insertEmployee.setString(1, badgeNumber);
            insertEmployee.setString(2, firstName);
            insertEmployee.setString(3, lastName);
            insertEmployee.setString(4, phoneNumber);
            insertEmployee.setString(5, email);
            return insertEmployee.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Could not insert employee: " + badgeNumber
                    + " " + firstName + " " + lastName + " " + phoneNumber
                    + " " + email);
            return 0;
        }
    }

    private static String getBadgeNumber() {
        String newBadge;
        Statement st;
        ResultSet rs;
        Random gen = new Random();

        try {
            st = App.conn.createStatement();

            while (true) {
                newBadge = Character.toString(badgeChars[gen.nextInt(26)]);
                for (int i = 1; i < badgeNumberSize; i++)
                    newBadge += Character.toString(badgeNums[gen.nextInt(10)]);

                rs = Exec.runQuery(st,
                        "EXECUTE badgeAvail(\'" + newBadge + "\');");
                System.out.println("Proposed Badge = " + newBadge);
                rs.next();
                if (rs.getInt(1) == 0)
                    return newBadge;
            }

        } catch (SQLException e) {
            System.out.println("Could not get a new badge number");
            System.exit(1);
            return null;
        }
    }

}
