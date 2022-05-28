import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Choice {
   static JFrame frame;
   static JPanel panelMain;
   private static final String WINDOW_NAME = "Frozen Rock Ice Cream Shop";
   private static final int WIDTH = 200;
   private static final int HEIGHT = 100;

   public static void build() {
      frame = new JFrame(WINDOW_NAME);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(WIDTH, HEIGHT);

      panelMain = new JPanel(new GridLayout(1, 2));
      frame.add(panelMain);

      JButton managerButton = new JButton("Analytics");
      managerButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AnalyticsGUI.build();

         }
      });
      JButton button1 = new JButton("Employees");
      button1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Gui.build();
         }
      });

      panelMain.add(button1);
      panelMain.add(managerButton);

      frame.setVisible(true);
   }

}
