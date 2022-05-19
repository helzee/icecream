import java.math.BigDecimal;
import java.sql.*;
import java.util.Random;

public class Insert {

    private static char[] badgeChars = new char[] { 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static char[] badgeNums = new char[] { '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0' };
    public static final int badgeNumberSize = 6;

    public static int insertMenuProduct(int categoryID, double currentPrice,
            String name, String desc) {

        try {
            PreparedStatement newMenuProd = App.conn.prepareStatement(
                    "INSERT INTO MenuProduct (categoryID, currentPrice, name, description, isOffered)"
                            + "VALUES (?,?,?,?,true)");
            newMenuProd.setInt(1, categoryID);
            newMenuProd.setBigDecimal(2, new BigDecimal(currentPrice));
            newMenuProd.setString(3, name);
            newMenuProd.setString(4, desc);

            int toReturn = newMenuProd.executeUpdate();
            newMenuProd.close();

            if (toReturn != 1) {
                // App.conn.rollback();
                return -1;
            }

            PreparedStatement menuProdID = App.conn.prepareStatement(
                    "SELECT ID FROM MenuProduct WHERE name = ? AND categoryID = ? AND isOffered = true;");
            menuProdID.setString(1, name);
            menuProdID.setInt(2, categoryID);

            ResultSet rs = menuProdID.executeQuery();
            rs.next();
            int newID = rs.getInt(1);
            rs.close();
            menuProdID.close();

            // App.conn.commit();
            return newID;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert MenuProduct: " + categoryID
                    + " " + name);
            return -1;
        }
    }

    public static int insertMenuCategory(String name) {

        try {
            PreparedStatement newCategory = App.conn.prepareStatement(
                    "INSERT INTO MenuCategory (name) VALUES (?)");
            newCategory.setString(1, name);

            int toReturn = newCategory.executeUpdate();
            newCategory.close();

            if (toReturn != 1) {
                // App.conn.rollback();
                return -1;
            }

            PreparedStatement categoryID = App.conn.prepareStatement(
                    "SELECT ID FROM MenuCategory WHERE name = ?;");
            categoryID.setString(1, name);

            ResultSet rs = categoryID.executeQuery();
            rs.next();
            int newID = rs.getInt(1);
            rs.close();
            categoryID.close();

            // App.conn.commit();
            return newID;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert menu category: " + name);
            return -1;
        }
    }

    public static int insertMenuMod(int itemID, double unitsNeeded,
            double currentPrice, String name, String desc) {

        try {
            PreparedStatement newMenuMod = App.conn.prepareStatement(
                    "INSERT INTO MenuModification (itemID, unitsNeeded, currentPrice, name, description, isOffered)"
                            + "VALUES (?,?,?,?,?,true)");
            newMenuMod.setInt(1, itemID);
            newMenuMod.setBigDecimal(2, new BigDecimal(unitsNeeded));
            newMenuMod.setBigDecimal(3, new BigDecimal(currentPrice));
            newMenuMod.setString(4, name);
            newMenuMod.setString(5, desc);

            int toReturn = newMenuMod.executeUpdate();
            newMenuMod.close();

            if (toReturn != 1) {
                // App.conn.rollback();
                return -1;
            }

            PreparedStatement menuModID = App.conn.prepareStatement(
                    "SELECT ID FROM MenuModification WHERE name = ? AND isOffered = true;");
            menuModID.setString(1, name);

            ResultSet rs = menuModID.executeQuery();
            rs.next();
            int newID = rs.getInt(1);
            rs.close();
            menuModID.close();

            // App.conn.commit();
            return newID;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert MenuModification: " + itemID
                    + " " + name);
            return -1;
        }
    }

    public static int insertItem(String name, String desc, boolean unitIsOz,
            double avgCostPerUnit) {

        try {
            PreparedStatement newItem = App.conn.prepareStatement(
                    "INSERT INTO Item (name, description, unitIsOunces, avgCostPerUnit)"
                            + "VALUES (?,?,?,?)");
            newItem.setString(1, name);
            newItem.setString(2, desc);
            newItem.setBoolean(3, unitIsOz);
            newItem.setBigDecimal(4, new BigDecimal(avgCostPerUnit));

            int toReturn = newItem.executeUpdate();
            newItem.close();

            if (toReturn != 1) {
                // App.conn.rollback();
                return -1;
            }

            PreparedStatement itemID = App.conn
                    .prepareStatement("SELECT ID FROM Item WHERE name = ?;");
            itemID.setString(1, name);

            ResultSet rs = itemID.executeQuery();
            rs.next();
            int newID = rs.getInt(1);
            rs.close();
            itemID.close();

            // App.conn.commit();
            return newID;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert item: " + name + " " + desc
                    + " " + unitIsOz + " " + avgCostPerUnit);
            return -1;
        }
    }

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

            int toReturn = insertEmployee.executeUpdate();
            insertEmployee.close();
            return toReturn;

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

                rs = Execute.runQuery(st,
                        "EXECUTE badgeAvail(\'" + newBadge + "\');");
                rs.next();
                if (rs.getInt(1) == 0) {
                    rs.close();
                    st.close();
                    return newBadge;
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not get a new badge number");
            System.exit(1);
            return null;
        }
    }

}
