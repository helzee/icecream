import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ManagerGUI implements ItemListener {
   JPanel cards; // a panel that uses CardLayout

   final static String ADD_EMPLOYEE_PANEL = "Add Employee";
   final static String ADD_ITEM_PANEL = "Add Item";

   public void addComponentToPane(Container pane) {
      // Put the JComboBox in a JPanel to get a nicer look.
      JPanel comboBoxPane = new JPanel(); // use FlowLayout
      String comboBoxItems[] = { ADD_EMPLOYEE_PANEL, ADD_ITEM_PANEL };
      JComboBox cb = new JComboBox(comboBoxItems);
      cb.setEditable(false);
      cb.addItemListener(this);
      comboBoxPane.add(cb);

      JPanel addEmployee = createAddEmployeeCard();

      JPanel addItem = createAddItemCard();
      // Create the panel that contains the "cards".
      cards = new JPanel(new CardLayout());

      cards.add(addEmployee, ADD_EMPLOYEE_PANEL);
      cards.add(addItem, ADD_ITEM_PANEL);

      pane.add(comboBoxPane, BorderLayout.PAGE_START);
      pane.add(cards, BorderLayout.CENTER);
   }

   public void itemStateChanged(ItemEvent evt) {
      CardLayout cl = (CardLayout) (cards.getLayout());
      cl.show(cards, (String) evt.getItem());
   }

   private JPanel createAddEmployeeCard() {
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
         }
      });
      addItem.add(enterButton);
      return addItem;
   }

   /**
    * Create the GUI and show it. For thread safety, this method should be invoked
    * from the event dispatch thread.
    */
   private static void createAndShowGUI() {
      // Create and set up the window.
      JFrame frame = new JFrame("Frozen Rock Ice Cream Shop - Manager");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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