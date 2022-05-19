import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Gui {
    static JFrame frame;
    static JPanel panelMain;
    static JScrollPane scrollPanelLeft;
    static JPanel panelRight;
    static JPanel ilp;


    public static void build(){
        frame = new JFrame("My First GUI");        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);

        panelMain = new JPanel(new GridLayout(1,2));
        frame.add(panelMain);
        //JPanel panelLeft = new JPanel();
        //JScrollPane scrollPanelLeft = new JScrollPane(ilp);
        ilp = new JPanel();
        scrollPanelLeft = new JScrollPane(ilp);
        ilp.setLayout(new BoxLayout(ilp, BoxLayout.Y_AXIS));
        //ilp.add(Box.createVerticalGlue());
        
        panelMain.add(scrollPanelLeft);
        panelRight = new JPanel(new FlowLayout());
        panelMain.add(panelRight);


        //JTextArea item1 = new JTextArea("vanilla icecream");
        //JTextArea item2 = new JTextArea("vanilla icecream");
        //JTextArea item3 = new JTextArea("vanilla icecream");
        //JTextArea item4 = new JTextArea("vanilla icecream");

        for (int i = 0; i< 0;i++){
            JTextArea item = new JTextArea("vanilla icecream");
            item.setMaximumSize( item.getPreferredSize());
            item.setBackground(Color.CYAN);
            ilp.add(item);
        }
        

        //ilp.add(item1);
        //ilp.add(item2);
        //ilp.add(item3);
        //ilp.add(item4);

        JButton button1 = new JButton("vanilla");
        button1.addActionListener( new ActionListener() {public void actionPerformed(ActionEvent e){addItem("vanilla");}});
        JButton button2 = new JButton("chocolate");
        button2.addActionListener( new ActionListener() {public void actionPerformed(ActionEvent e){addItem("chocolate");}});
        JButton button3 = new JButton("strawberry");
        button3.addActionListener( new ActionListener() {public void actionPerformed(ActionEvent e){addItem("strawberry");}});
        panelRight.add(button1);
        panelRight.add(button2);
        panelRight.add(button3);

        
        frame.setVisible(true);
     }

     private static void addItem(String s){
         System.out.println("calleed");
        JTextArea item = new JTextArea(s);
        item.setMaximumSize( item.getPreferredSize());
        item.setBackground(Color.CYAN);
        ilp.add(item);
        frame.setVisible(true);
     }
}