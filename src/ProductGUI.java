import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ProductGUI {

   private static String[] cols = { "categoryID", "currentPrice", "name",
         "description", "isoffered" };
   private static String[] types = { "integer", "numeric", "string", "string",
         "boolean" };

   private static String entity = "menuproduct";

   public static JPanel createAddProductCard() {
      JPanel addProduct = new JPanel();

      return addProduct;

   }

   public static JPanel createEditProductCard() throws SQLException {
      JPanel editItem = new JPanel();

      String query = "SELECT id";
      for (String s : cols) {
         query += ", " + s;
      }
      query += " FROM " + entity + ";";
      ResultSet items = Execute.runQuery(query);

      Vector<JButton> itemButtons = new Vector<JButton>();
      Vector<Integer> itemIDs = new Vector<Integer>();
      JButton button = null;
      while (items.next()) {
         itemIDs.add(items.getInt(1));
         itemButtons.add(button = new JButton(items.getString(4)));
         button.putClientProperty("id", items.getInt(1));

      }

      for (JButton b : itemButtons) {
         editItem.add(b);
      }

      for (int i = 0; i < itemButtons.size(); i++) {
         itemButtons.get(i).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               try {

                  generateEditProductWindow((int) ((JButton) e.getSource())
                        .getClientProperty("id"));
                  ManagerGUI.frame.dispose();
               } catch (SQLException et) {
                  et.printStackTrace();
               }

            }
         });
      }

      return editItem;

   }

   private static void generateEditProductWindow(int id) throws SQLException {
      ResultSet item = App.conn.createStatement().executeQuery(
            "SELECT * FROM " + entity + " WHERE id =" + id + ";");
      item.next();

      JFrame frame = new JFrame("Edit Item");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel editItem = new JPanel();
      JTextField[] info = new JTextField[cols.length];

      for (int i = 0; i < cols.length; i++) {
         int size = 32;
         if (types[i].toLowerCase().equals("string")) {
            size = 40;
         } else if (types[i].toLowerCase().equals("boolean")) {
            size = 5;
         } else if (types[i].toLowerCase().equals("numeric")) {
            size = 12;
         } else if (types[i].toLowerCase().equals("integer")) {
            size = 12;
         }
         info[i] = new JTextField(item.getString(i + 2), size);
         editItem.add(new JLabel(cols[i]));
         editItem.add(info[i]);

      }

      JButton enterButton = new JButton("Enter");
      enterButton.putClientProperty("id", id);

      JButton deleteButton = new JButton("Delete");
      deleteButton.putClientProperty("id", id);
      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               int id = (int) ((JButton) e.getSource())
                     .getClientProperty("id");

               for (int i = 0; i < cols.length; i++) {
                  if (types[i].toLowerCase().equals("string")) {
                     Update.updateString(entity, cols[i], info[i].getText(),
                           id);
                  } else if (types[i].toLowerCase().equals("boolean")) {
                     Update.updateBoolean(entity, cols[i], info[i].getText(),
                           id);
                  } else if (types[i].toLowerCase().equals("numeric")) {
                     BigDecimal num = null;
                     try {
                        num = new BigDecimal(info[i].getText());
                     } catch (Exception et) {
                        num = new BigDecimal(0);
                     }
                     Update.updateNumeric(entity, cols[i], num, id);
                  }

               }
               App.conn.commit();

            } catch (SQLException et) {
               et.printStackTrace();
            }

            frame.dispose();
            ManagerGUI.createAndShowGUI();

         }
      });

      deleteButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int id = (int) ((JButton) e.getSource()).getClientProperty("id");

            try {
               App.conn.createStatement().executeUpdate(
                     "DELETE FROM " + entity + " WHERE id = " + id + ";");
               App.conn.commit();
            } catch (SQLException et) {
               et.printStackTrace();
            }
            frame.dispose();
            ManagerGUI.createAndShowGUI();

         }
      });

      editItem.add(enterButton);
      editItem.add(deleteButton);

      frame.add(editItem);
      frame.pack();
      frame.setVisible(true);

   }

}
