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

    public static boolean insertProductIngredient(int productID, int itemID,
            double unitsNeeded) {

        try {
            PreparedStatement newMenuProd = App.conn.prepareStatement(
                    "INSERT INTO ProductIngredient (productID, itemID, unitsNeeded)"
                            + "VALUES (?,?,?)");
            newMenuProd.setInt(1, productID);
            newMenuProd.setInt(2, itemID);
            newMenuProd.setBigDecimal(3, new BigDecimal(unitsNeeded));

            int toReturn = newMenuProd.executeUpdate();
            newMenuProd.close();

            if (toReturn != 1) {
                // App.conn.rollback();
                return false;
            }
            // App.conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert Product Ingredient: "
                    + productID + " " + itemID);
            return false;
        }
    }

    public static int insertMenuProduct(int categoryID, double currentPrice,
            String name, String desc) {

        try {
            PreparedStatement newMenuProd = App.conn.prepareStatement(
                    "INSERT INTO MenuProduct (categoryID, currentPrice, name, description, isOffered)"
                            + "VALUES (?,?,?,?,true) RETURNING ID");
            newMenuProd.setInt(1, categoryID);
            newMenuProd.setBigDecimal(2, new BigDecimal(currentPrice));
            newMenuProd.setString(3, name);
            newMenuProd.setString(4, desc);

            return Execute.execAndFetchID(newMenuProd);

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
                    "INSERT INTO MenuCategory (name) VALUES (?) RETURNING ID");
            newCategory.setString(1, name);

            return Execute.execAndFetchID(newCategory);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not insert menu category: " + name);
            return -1;
        }
    }

    public static int insertMenuMod(int itemID, double unitsNeeded,
            double currentPrice, String name) {

        try {
            PreparedStatement newMenuMod = App.conn.prepareStatement(
                    "INSERT INTO MenuModification (itemID, unitsNeeded, currentPrice, name, isOffered)"
                            + "VALUES (?,?,?,?,true) RETURNING ID");
            newMenuMod.setInt(1, itemID);
            newMenuMod.setBigDecimal(2, new BigDecimal(unitsNeeded));
            newMenuMod.setBigDecimal(3, new BigDecimal(currentPrice));
            newMenuMod.setString(4, name);

            return Execute.execAndFetchID(newMenuMod);

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
                            + "VALUES (?,?,?,?) RETURNING ID;");
            newItem.setString(1, name);
            newItem.setString(2, desc);
            newItem.setBoolean(3, unitIsOz);
            newItem.setBigDecimal(4, new BigDecimal(avgCostPerUnit));

            return Execute.execAndFetchID(newItem);

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
            PreparedStatement newEmployee = App.conn.prepareStatement(
                    "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                            + "VALUES (?,?,?,?,?) RETURNING ID;");
            newEmployee.setString(1, badgeNumber);
            newEmployee.setString(2, firstName);
            newEmployee.setString(3, lastName);
            newEmployee.setString(4, phoneNumber);
            newEmployee.setString(5, email);

            return Execute.execAndFetchID(newEmployee);

        } catch (SQLException e) {
            System.out.println("Could not insert employee: " + badgeNumber
                    + " " + firstName + " " + lastName + " " + phoneNumber
                    + " " + email);
            return -1;
        }
    }

    public static int insertEmployee(String badgeNumber, String firstName,
            String lastName, String phoneNumber, String email) {

        try {
            PreparedStatement newEmployee = App.conn.prepareStatement(
                    "INSERT INTO Employee (badgeNumber, firstName, lastName, phoneNumber, email)"
                            + "VALUES (?,?,?,?,?) RETURNING ID;");
            newEmployee.setString(1, badgeNumber);
            newEmployee.setString(2, firstName);
            newEmployee.setString(3, lastName);
            newEmployee.setString(4, phoneNumber);
            newEmployee.setString(5, email);

            return Execute.execAndFetchID(newEmployee);

        } catch (SQLException e) {
            System.out.println("Could not insert employee: " + badgeNumber
                    + " " + firstName + " " + lastName + " " + phoneNumber
                    + " " + email);
            return -1;
        }

    }

    private static String getBadgeNumber() {
        String newBadge;
        ResultSet rs;
        Random gen = new Random();

        try {
            while (true) {
                newBadge = Character.toString(badgeChars[gen.nextInt(26)]);
                for (int i = 1; i < badgeNumberSize; i++)
                    newBadge += Character.toString(badgeNums[gen.nextInt(10)]);

                rs = Execute
                        .runQuery("EXECUTE badgeAvail(\'" + newBadge + "\');");
                rs.next();
                if (rs.getInt(1) == 0) {
                    rs.close();
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
