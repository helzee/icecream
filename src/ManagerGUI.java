import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ManagerGUI implements ItemListener {
   JPanel cards; // a panel that uses CardLayout
   public static JFrame frame;

   final static String ADD_EMPLOYEE_PANEL = "Add Employee";
   final static String ADD_ITEM_PANEL = "Add Item";
   final static String EDIT_EMPLOYEE_PANEL = "Edit/delete Employees";
   final static String EDIT_ITEM_PANEL = "Edit/delete Items";
   final static String EDIT_PRODUCT_PANEL = "Edit/delete Product";
   final static String ADD_PRODUCT_PANEL = "Add Product";

   final static String[] ITEM_COLUMNS = { "name", "description",
         "unitisounces", "avgcostperunit" };
   final static String[] ITEM_COL_TYPES = { "string", "string", "boolean",
         "numeric" };

   public void addComponentToPane(Container pane) {
      // Put the JComboBox in a JPanel to get a nicer look.
      JPanel comboBoxPane = new JPanel(); // use FlowLayout
      String comboBoxItems[] = { ADD_EMPLOYEE_PANEL, ADD_ITEM_PANEL,
            EDIT_EMPLOYEE_PANEL, EDIT_ITEM_PANEL, EDIT_PRODUCT_PANEL,
            ADD_PRODUCT_PANEL };
      JComboBox cb = new JComboBox(comboBoxItems);
      cb.setEditable(false);
      cb.addItemListener(this);
      comboBoxPane.add(cb);

      JPanel addEmployee = createAddEmployeeCard();
      JPanel editEmployee = null;
      JPanel editItem = null;
      JPanel editProduct = null;
      JPanel addProduct = null;
      try {
         editEmployee = createEditEmployeeCard();
         editItem = createEditItemCard(ITEM_COLUMNS, ITEM_COL_TYPES, "Item");
         editProduct = ProductGUI.createEditProductCard();
         addProduct = ProductGUI.createAddProductCard();
      } catch (SQLException e) {
         e.printStackTrace();
      }

      JPanel addItem = createAddItemCard();

      // Create the panel that contains the "cards".
      cards = new JPanel(new CardLayout());

      cards.add(addEmployee, ADD_EMPLOYEE_PANEL);
      cards.add(addItem, ADD_ITEM_PANEL);
      cards.add(editEmployee, EDIT_EMPLOYEE_PANEL);
      cards.add(editItem, EDIT_ITEM_PANEL);
      cards.add(editProduct, EDIT_PRODUCT_PANEL);
      cards.add(addProduct, ADD_PRODUCT_PANEL);

      pane.add(comboBoxPane, BorderLayout.PAGE_START);
      pane.add(cards, BorderLayout.CENTER);

   }

   // private static void addNavBar(JPanel panelMain, JFrame frame) {
   // JPanel panelNavToolBar = new JPanel(new FlowLayout());
   // panelMain.add(panelNavToolBar, BorderLayout.NORTH);
   // JButton buttonNavToManager = new JButton("Change To Employee GUI");
   // buttonNavToManager.addActionListener(new ActionListener() {
   // public void actionPerformed(ActionEvent e) {
   // frame.dispose();
   // Gui.build();
   // }
   // });

   // panelNavToolBar.add(buttonNavToManager);
   // }

   public void itemStateChanged(ItemEvent evt) {
      CardLayout cl = (CardLayout) (cards.getLayout());
      cl.show(cards, (String) evt.getItem());
   }

   private static JPanel createEditEmployeeCard() throws SQLException {
      JPanel editEmployee = new JPanel();

      ResultSet employees = Execute
            .runQuery("SELECT id, firstName, LastName FROM Employee;");

      Vector<JButton> empButtons = new Vector<JButton>();
      Vector<Integer> empIDs = new Vector<Integer>();
      JButton button = null;
      while (employees.next()) {
         empIDs.add(employees.getInt(1));
         empButtons.add(button = new JButton(
               employees.getString(2) + " " + employees.getString(3)));
         button.putClientProperty("id", employees.getInt(1));
      }

      for (JButton b : empButtons) {
         editEmployee.add(b);
      }

      for (int i = 0; i < empButtons.size(); i++) {
         empButtons.get(i).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               try {
                  generateEditEmployeeWindow((int) ((JButton) e.getSource())
                        .getClientProperty("id"));
                  frame.dispose();
               } catch (SQLException et) {
                  et.printStackTrace();
               }

            }
         });
      }

      return editEmployee;

   }

   private static void generateEditEmployeeWindow(int empID)
         throws SQLException {
      ResultSet employee = App.conn.createStatement()
            .executeQuery("SELECT * FROM Employee WHERE id =" + empID + ";");
      employee.next();

      JFrame frame = new JFrame("Edit Employee");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel editEmployee = new JPanel();
      JTextField[] info = new JTextField[5];
      editEmployee.add(new JLabel("badgenumber"));
      editEmployee.add(info[0] = new JTextField(employee.getString(2), 6));
      editEmployee.add(new JLabel("firstName"));
      editEmployee.add(info[1] = new JTextField(employee.getString(3), 32));
      editEmployee.add(new JLabel("lastname"));
      editEmployee.add(info[2] = new JTextField(employee.getString(4), 32));
      editEmployee.add(new JLabel("phonenumber"));
      editEmployee.add(info[3] = new JTextField(employee.getString(5), 16));
      editEmployee.add(new JLabel("email"));
      editEmployee.add(info[4] = new JTextField(employee.getString(6), 64));

      JButton enterButton = new JButton("Enter");
      enterButton.putClientProperty("id", empID);

      JButton deleteButton = new JButton("Delete");
      deleteButton.putClientProperty("id", empID);

      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               int empID = (int) ((JButton) e.getSource())
                     .getClientProperty("id");
               String[] colNames = { "badgeNumber", "firstName", "lastName",
                     "phoneNumber", "email" };

               for (int i = 0; i < colNames.length; i++) {
                  App.conn.createStatement()
                        .executeUpdate("UPDATE Employee SET " + colNames[i]
                              + " = \'" + info[i].getText() + "\' "
                              + "WHERE id = " + empID + ";");
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
                     "DELETE FROM Employee WHERE id = " + id + ";");
               App.conn.commit();
            } catch (SQLException et) {
               et.printStackTrace();
            }
            frame.dispose();
            ManagerGUI.createAndShowGUI();

         }
      });

      editEmployee.add(enterButton);
      editEmployee.add(deleteButton);

      frame.add(editEmployee);
      frame.pack();
      frame.setVisible(true);
   }

   private static JPanel createAddEmployeeCard() {
      JPanel addEmployee = new JPanel();
      JTextField[] info = new JTextField[5];
      addEmployee.add(info[4] = new JTextField("BadgeNumber", 6));
      addEmployee.add(info[0] = new JTextField("FirstName", 32));
      addEmployee.add(info[1] = new JTextField("LastName", 32));
      addEmployee.add(info[2] = new JTextField("PhoneNumber", 16));
      addEmployee.add(info[3] = new JTextField("email", 64));
      JButton enterButton = new JButton("Enter");
      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Insert.insertEmployee(info[4].getText(), info[0].getText(),
                  info[1].getText(), info[2].getText(), info[3].getText());

            try {
               App.conn.commit();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            frame.dispose();
            createAndShowGUI();
         }
      });
      addEmployee.add(enterButton);
      return addEmployee;
   }

   private JPanel createAddItemCard() {
      JPanel addItem = new JPanel();
      JTextField[] info = new JTextField[5];
      addItem.add(info[0] = new JTextField("name", 32));
      addItem.add(info[1] = new JTextField("description", 32));
      addItem.add(info[2] = new JTextField("costPerUnit", 12));
      JButton enterButton = new JButton("Enter");
      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Insert.insertItem(info[0].getText(), info[1].getText(), true,
                  Double.parseDouble(info[2].getText()));

            try {
               App.conn.commit();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            frame.dispose();
            createAndShowGUI();
         }
      });
      addItem.add(enterButton);
      return addItem;
   }

   private static JPanel createEditItemCard(String[] cols, String[] types,
         String entity) throws SQLException {
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
         itemButtons.add(button = new JButton(items.getString(2)));
         button.putClientProperty("id", items.getInt(1));

      }

      for (JButton b : itemButtons) {
         editItem.add(b);
      }

      for (int i = 0; i < itemButtons.size(); i++) {
         itemButtons.get(i).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               try {

                  generateEditItemWindow((int) ((JButton) e.getSource())
                        .getClientProperty("id"), entity, cols, types);
                  frame.dispose();
               } catch (SQLException et) {
                  et.printStackTrace();
               }

            }
         });
      }

      return editItem;

   }

   private static void generateEditItemWindow(int itemID, String entity,
         String[] colNames, String[] types) throws SQLException {

      ResultSet item = App.conn.createStatement().executeQuery(
            "SELECT * FROM " + entity + " WHERE id =" + itemID + ";");
      item.next();

      JFrame frame = new JFrame("Edit Item");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel editItem = new JPanel();
      JTextField[] info = new JTextField[colNames.length];

      for (int i = 0; i < colNames.length; i++) {
         int size = 32;
         if (types[i].toLowerCase().equals("string")) {
            size = 40;
         } else if (types[i].toLowerCase().equals("boolean")) {
            size = 5;
         } else if (types[i].toLowerCase().equals("numeric")) {
            size = 12;
         }
         editItem.add(new JLabel(colNames[i]));
         editItem.add(info[i] = new JTextField(item.getString(i + 2), size));
      }

      JButton enterButton = new JButton("Enter");
      enterButton.putClientProperty("id", itemID);

      JButton deleteButton = new JButton("Delete");
      deleteButton.putClientProperty("id", itemID);
      enterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               int id = (int) ((JButton) e.getSource())
                     .getClientProperty("id");

               for (int i = 0; i < colNames.length; i++) {
                  if (types[i].toLowerCase().equals("string")) {
                     Update.updateString(entity, colNames[i],
                           info[i].getText(), id);
                  } else if (types[i].toLowerCase().equals("boolean")) {
                     Update.updateBoolean(entity, colNames[i],
                           info[i].getText(), id);
                  } else if (types[i].toLowerCase().equals("numeric")) {
                     BigDecimal num = null;
                     try {
                        num = new BigDecimal(info[i].getText());
                     } catch (Exception et) {
                        num = new BigDecimal(0);
                     }
                     Update.updateNumeric(entity, colNames[i], num, id);
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

   /**
    * Create the GUI and show it. For thread safety, this method should be invoked
    * from the event dispatch thread.
    */
   public static void createAndShowGUI() {
      // Create and set up the window.
      frame = new JFrame("Frozen Rock Ice Cream Shop - Manager");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // JPanel panelMain = new JPanel();
      // frame.add(panelMain);
      // addNavBar(panelMain, frame);

      // Create and set up the content pane.
      ManagerGUI demo = new ManagerGUI();
      demo.addComponentToPane(frame.getContentPane());

      // Display the window.
      frame.pack();
      frame.setVisible(true);
   }

   public static void build() {
      /* Use an appropriate Look and Feel */
      try {
         // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      } catch (UnsupportedLookAndFeelException ex) {
         ex.printStackTrace();
      } catch (IllegalAccessException ex) {
         ex.printStackTrace();
      } catch (InstantiationException ex) {
         ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      /* Turn off metal's use of bold fonts */
      UIManager.put("swing.boldMetal", Boolean.FALSE);

      // Schedule a job for the event dispatch thread:
      // creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}