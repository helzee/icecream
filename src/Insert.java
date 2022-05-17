import java.sql.*;

public class Insert {

    public static int insertEmployee(String badgeNumber, String firstName,
            String lastName, String phoneNumber, String email) {

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

}
