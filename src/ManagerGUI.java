import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ManagerGUI implements ItemListener {
   JPanel cards; // a panel that uses CardLayout
   final static String BUTTONPANEL = "Card with JButtons";
   final static String TEXTPANEL = "Card with JTextField";
   final static String ADD_EMPLOYEE_PANEL = "Add Employee";

   public void addComponentToPane(Container pane) {
      // Put the JComboBox in a JPanel to get a nicer look.
      JPanel comboBoxPane = new JPanel(); // use FlowLayout
      String comboBoxItems[] = { BUTTONPANEL, TEXTPANEL, ADD_EMPLOYEE_PANEL };
      JComboBox cb = new JComboBox(comboBoxItems);
      cb.setEditable(false);
      cb.addItemListener(this);
      comboBoxPane.add(cb);

      // Create the "cards".
      JPanel card1 = new JPanel();
      card1.add(new JButton("Button 1"));
      card1.add(new JButton("Button 2"));
      card1.add(new JButton("Button 3"));

      JPanel card2 = new JPanel();
      card2.add(new JTextField("TextField", 20));

      JPanel addEmployee = createAddEmployeeCard();
      // Create the panel that contains the "cards".
      cards = new JPanel(new CardLayout());
      cards.add(card1, BUTTONPANEL);
      cards.add(card2, TEXTPANEL);
      cards.add(addEmployee, ADD_EMPLOYEE_PANEL);

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