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
   private static JFrame frame;

   public static void defineProductItems(int id) {
      try {
         ResultSet nameGetter = App.conn.createStatement().executeQuery(
               "SELECT name FROM menuProduct WHERE id = " + id + ";");
         nameGetter.next();

         frame = new JFrame("Define product: " + nameGetter.getString(1));
      } catch (SQLException e) {
         e.printStackTrace();
         frame = new JFrame("Define product");
      }

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel workspace = new JPanel();
      JPanel main = new JPanel(new GridLayout(1, 2));
      frame.add(main);
      main.add(workspace, BorderLayout.CENTER);
      JPanel toolBar = new JPanel(new FlowLayout());
      main.add(toolBar, BorderLayout.NORTH);

      JPanel leftPanel = new JPanel();
      leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
      workspace.add(leftPanel);
      JPanel rightPanel = new JPanel(new FlowLayout());
      rightPanel.add(new JLabel("Choose items to add to this product"));
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      workspace.add(rightPanel);

      leftPanel.add(new JLabel("Items in product. Click to remove 1."));
      populateItems(id, leftPanel);
      seeAllItems(rightPanel, id);

      JButton updateProduct = new JButton("Update Product");
      updateProduct.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               App.conn.commit();
            } catch (SQLException et) {
               et.printStackTrace();
            }

            frame.dispose();
            ManagerGUI.createAndShowGUI();
         }
      });
      toolBar.add(updateProduct);

      frame.pack();
      frame.setVisible(true);
   }

   private static void seeAllItems(JPanel panel, int id) {
      String query = "SELECT * FROM Item;";
      ResultSet items = Execute.runQuery(query);

      Vector<JButton> itemButtons = new Vector<JButton>();
      Vector<Integer> itemIDs = new Vector<Integer>();
      JButton button = null;
      try {
         while (items.next()) {
            itemIDs.add(items.getInt(1));
            itemButtons.add(button = new JButton(items.getString(2)));
            button.putClientProperty("id", items.getInt(1));

         }
      } catch (SQLException et) {
         et.printStackTrace();
      }

      for (JButton b : itemButtons) {
         panel.add(b);
      }

      for (int i = 0; i < itemButtons.size(); i++) {
         itemButtons.get(i).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               int itemID = (int) ((JButton) e.getSource())
                     .getClientProperty("id");
               try {
                  ResultSet i = App.conn.createStatement().executeQuery(
                        "SELECT * FROM ProductIngredient WHERE productID = "
                              + id + " AND itemID = " + itemID + ";");
                  if (i.next()) {
                     int unitsNeeded = i.getInt(3);
                     App.conn.createStatement().executeUpdate(
                           "UPDATE ProductIngredient SET unitsNeeded = "
                                 + (unitsNeeded + 1) + " WHERE productID = "
                                 + id + " AND itemID = " + itemID + ";");
                  } else {
                     Insert.insertProductIngredient(id, itemID, 1);
                  }

               } catch (SQLException et) {
                  et.printStackTrace();
               }
               frame.dispose();

               defineProductItems(id);

            }
         });
      }
   }

   private static void populateItems(int id, JPanel pane) {
      ResultSet items = null;

      try {
         items = App.conn.createStatement().executeQuery(
               "SELECT itemID, Item.name, unitsNeeded FROM ProductIngredient"
                     + " JOIN Item ON (Item.id = ItemID) WHERE productID = "
                     + id + ";");

         while (items.next()) {
            JButton button = new JButton(
                  items.getString(2) + ": " + items.getInt(3));
            button.putClientProperty("id", items.getInt(1));
            button.putClientProperty("quantity", items.getString(3));

            button.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  int itemID = (int) ((JButton) e.getSource())
                        .getClientProperty("id");
                  try {

                     ResultSet i = App.conn.createStatement().executeQuery(
                           "SELECT itemID, unitsNeeded, productID FROM ProductIngredient"
                                 + " WHERE productID = " + id + "AND itemID = "
                                 + itemID + ";");
                     i.next();

                     int unitsNeeded = i.getInt(2);
                     if (unitsNeeded <= 1) {
                        App.conn.createStatement()
                              .execute("DELETE FROM ProductIngredient"
                                    + "WHERE productID = " + i.getInt(3)
                                    + " AND itemID = " + i.getInt(1));
                     } else {
                        App.conn.createStatement()
                              .execute("UPDATE ProductIngredient SET "
                                    + "unitsNeeded = " + (unitsNeeded - 1)
                                    + " WHERE productID = " + id + " AND "
                                    + " itemID = " + itemID);
                     }
                  } catch (SQLException et) {
                     et.printStackTrace();
                  }
                  frame.dispose();
                  defineProductItems(id);

               }
            });

            pane.add(button);
         }
      } catch (Exception et) {
         et.printStackTrace();
      }

   }

   public static JPanel createAddProductCard() {

      JPanel addProduct = new JPanel();
      JTextField[] info = new JTextField[cols.length];

      for (int i = 1; i < cols.length - 1; i++) {
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
         info[i] = new JTextField(size);
         addProduct.add(new JLabel(cols[i]));
         addProduct.add(info[i]);
      }

      JButton enterButton = new JButton("Enter");

      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Insert.insertMenuProduct(256,
                  Double.parseDouble(info[1].getText()), info[2].getText(),
                  info[3].getText());

            try {
               App.conn.commit();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            ManagerGUI.frame.dispose();
            ManagerGUI.createAndShowGUI();
         }
      });
      addProduct.add(enterButton);
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

      JButton enterButton = new JButton("Update");
      enterButton.putClientProperty("id", id);

      JButton defineItems = new JButton("Define Items");
      defineItems.putClientProperty("id", id);

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

      defineItems.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int id = (int) ((JButton) e.getSource()).getClientProperty("id");
            frame.dispose();
            defineProductItems(id);
         }
      });

      deleteButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int id = (int) ((JButton) e.getSource()).getClientProperty("id");

            try {
               App.conn.createStatement().executeUpdate(
                     "DELETE FROM productIngredient WHERE productid = " + id
                           + ";");
               App.conn.createStatement().executeUpdate(
                     "DELETE FROM " + entity + " WHERE id = " + id + ";");
               App.conn.commit();
            } catch (SQLException et) {
               System.err.println(
                     "Can't delete product. It is probably used in a transaction");
            }
            frame.dispose();
            ManagerGUI.createAndShowGUI();

         }
      });

      editItem.add(enterButton);
      editItem.add(deleteButton);
      editItem.add(defineItems);

      frame.add(editItem);
      frame.pack();
      frame.setVisible(true);

   }

}
