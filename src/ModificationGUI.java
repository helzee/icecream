import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ModificationGUI {
   private static String[] cols = { "categoryID", "currentPrice", "name",
         "description", "isoffered" };
   private static String[] types = { "integer", "numeric", "string", "string",
         "boolean" };

   private static String entity = "menuproduct";
   private static JFrame frame;

   public static JPanel createAddModificationCard() {

      JPanel addProduct = new JPanel();
      JTextField[] info = new JTextField[cols.length];

      for (int i = 0; i < cols.length - 1; i++) {
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

   public static JPanel createEditModificationCard() {
      return new JPanel();
   }
}
