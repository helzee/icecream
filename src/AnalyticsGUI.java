import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class AnalyticsGUI {
   static JFrame frame;
   static JPanel panelMain;
   private static final String WINDOW_NAME = "Frozen Rock Ice Cream Shop - Analytics";
   private static final int WIDTH = 1000;
   private static final int HEIGHT = 1000;

   public static void build() {
      frame = new JFrame(WINDOW_NAME);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(WIDTH, HEIGHT);

      panelMain = new JPanel(new GridLayout(1, 2));
      frame.add(panelMain);

      JButton lastMonthSales = new JButton("Analyze last month's sales");
      lastMonthSales.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            goToManagerPage();

         }
      });

      panelMain.add(lastMonthSales);

      frame.setVisible(true);
   }

   private static void goToManagerPage() {

      panelMain = new JPanel();
      frame.add(panelMain);

      frame.setVisible(true);

   }

}
