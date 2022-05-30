import java.awt.*;
import java.awt.event.*;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ModificationGUI {
   private static String[] cols = { "ItemID", "unitsNeeded", "currentPrice",
         "name", "isoffered" };
   private static String[] types = { "integer", "numeric", "numeric", "string",
         "boolean" };

   private static int itemID;

   public static JPanel createAddModificationCard() throws SQLException {
      itemID = -1;

      JPanel addProduct = new JPanel();
      JTextField[] info = new JTextField[cols.length];

      JPanel itemButtons = getItems();
      addProduct.add(itemButtons);

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
            if (itemID > -1) {
               Insert.insertMenuMod(itemID,
                     Double.parseDouble(info[1].getText()),
                     Double.parseDouble(info[2].getText()), info[3].getText());
            } else {
               System.err.println(
                     "Error: Must select an item for the modification");
            }

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

   private static JPanel getItems() throws SQLException {
      JPanel editItem = new JPanel();

      String query = "SELECT * FROM Item;";
      ResultSet items = Execute.runQuery(query);

      Vector<JRadioButton> itemButtons = new Vector<JRadioButton>();
      Vector<Integer> itemIDs = new Vector<Integer>();
      JRadioButton button = null;
      while (items.next()) {
         itemIDs.add(items.getInt(1));
         itemButtons.add(button = new JRadioButton(items.getString(2)));
         button.putClientProperty("id", items.getInt(1));

      }

      ButtonGroup buttGroup = new ButtonGroup();
      for (JRadioButton b : itemButtons) {
         buttGroup.add(b);
         editItem.add(b);
      }

      for (int i = 0; i < itemButtons.size(); i++) {
         itemButtons.get(i).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

               itemID = ((int) ((JRadioButton) e.getSource())
                     .getClientProperty("id"));

            }
         });
      }

      return editItem;

   }

   private static void addItem(int id, int modID) {

   }

   public static JPanel createEditModificationCard() {
      return new JPanel();
   }
}
