import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.synth.SynthPasswordFieldUI;

public class Gui {
    static JFrame frame;
    static JPanel panelWorkspace;
    static JPanel panelMain;
    static JPanel panelNavToolBar;
    static JScrollPane scrollPanelLeft;
    static JPanel panelRight;
    static JPanel ilp;

    private static final String WINDOW_NAME = "Frozen Rock Ice Cream Shop";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    public static void build() {
        frame = new JFrame(WINDOW_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        panelWorkspace = new JPanel(new GridLayout(1, 2));
        panelMain = new JPanel(new BorderLayout());
        frame.add(panelMain);
        panelMain.add(panelWorkspace, BorderLayout.CENTER);
        panelNavToolBar = new JPanel(new FlowLayout());
        panelMain.add(panelNavToolBar, BorderLayout.NORTH);

        // JPanel panelLeft = new JPanel();
        // JScrollPane scrollPanelLeft = new JScrollPane(ilp);
        ilp = new JPanel();
        scrollPanelLeft = new JScrollPane(ilp);
        ilp.setLayout(new BoxLayout(ilp, BoxLayout.Y_AXIS));
        // ilp.add(Box.createVerticalGlue());

        panelWorkspace.add(scrollPanelLeft);
        panelRight = new JPanel(new FlowLayout());
        panelWorkspace.add(panelRight);

        /*
         * used for manually making buttons for (int i = 0; i < 0; i++) { JTextArea item
         * = new JTextArea("vanilla icecream");
         * item.setMaximumSize(item.getPreferredSize()); item.setBackground(Color.CYAN);
         * ilp.add(item); }
         * 
         * JButton button1 = new JButton("vanilla"); button1.addActionListener(new
         * ActionListener() { public void actionPerformed(ActionEvent e) {
         * addItem("vanilla"); } }); JButton button2 = new JButton("chocolate");
         * button2.addActionListener(new ActionListener() { public void
         * actionPerformed(ActionEvent e) { addItem("chocolate"); } }); JButton button3
         * = new JButton("strawberry"); button3.addActionListener(new ActionListener() {
         * public void actionPerformed(ActionEvent e) { addItem("strawberry"); } });
         */

        JButton buttonNavToManager = new JButton("Change To Manager GUI");
        buttonNavToManager.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ManagerGUI.build();
            }
        });

        getItemButtons();

        panelNavToolBar.add(buttonNavToManager);

        frame.setVisible(true);
    }

    private static void addItem(String id, String name, String price) {
        // itembox panel to hold All item information
        JPanel itemBox = new JPanel(new BorderLayout(10, 5));
        itemBox.setMaximumSize(new Dimension(10000, 25));

        JPanel itemBoxLeft = new JPanel(new BorderLayout(2, 0));
        itemBoxLeft.setMaximumSize(new Dimension(80, 25));
        itemBox.add(itemBoxLeft, BorderLayout.WEST);

        // button to delete the item
        JButton DeleteButton = new JButton("X");
        DeleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ilp.remove(itemBox); // remove the item from the Item Left Panel (ilp)
                ilp.revalidate();
                ilp.repaint();
            }
        });
        // DeleteButton.setMaximumSize(new Dimension(25,25));
        DeleteButton.setSize(25, 25);
        itemBoxLeft.add(DeleteButton, BorderLayout.WEST);

        // text displaying id
        JLabel idText = new JLabel("ID: " + id);
        itemBoxLeft.add(idText, BorderLayout.EAST);

        // text displaying name
        JLabel nameText = new JLabel(name);
        nameText.setMaximumSize(nameText.getPreferredSize());
        nameText.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        itemBox.add(nameText, BorderLayout.CENTER);

        // text displaying price
        JLabel priceText = new JLabel(price);
        priceText.setMaximumSize(priceText.getPreferredSize());
        priceText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        itemBox.add(priceText, BorderLayout.EAST);

        // item.setMaximumSize(item.getPreferredSize());
        // item.setBackground(Color.CYAN);
        ilp.add(itemBox);
        frame.setVisible(true);
    }

    private static void getItemButtons() { // change so that when clicked it adds the id as well as the name and price to
                                           // the building reciept
        JPanel itemBox = new JPanel(new BorderLayout(10, 5));
        itemBox.setMaximumSize(new Dimension(10000, 25));

        JPanel itemBoxLeft = new JPanel(new BorderLayout(2, 0));
        itemBoxLeft.setMaximumSize(new Dimension(80, 25));
        itemBox.add(itemBoxLeft, BorderLayout.WEST);

        JLabel idText = new JLabel("ID", SwingConstants.CENTER);
        idText.setMaximumSize(idText.getPreferredSize());
        idText.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        itemBoxLeft.add(idText, BorderLayout.EAST);

        // text displaying name
        JLabel nameText = new JLabel("NAME", SwingConstants.CENTER);
        nameText.setMaximumSize(nameText.getPreferredSize());
        itemBox.add(nameText, BorderLayout.CENTER);

        // text displaying price
        JLabel priceText = new JLabel("PRICE", SwingConstants.CENTER);
        priceText.setMaximumSize(priceText.getPreferredSize());
        priceText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        itemBox.add(priceText, BorderLayout.EAST);

        ilp.add(itemBox);

        String[] ids = Format.rsToArray(Execute.runQuery(
                "SELECT id FROM MenuProduct WHERE isOffered is TRUE;"));

        for (String id : ids) {

            String name = Format.rsToString(Execute.runQuery(
                    "SELECT name FROM MenuProduct WHERE id = " + id + ";"));
            String price = Format.rsToString(Execute.runQuery(
                    "SELECT currentPrice FROM MenuProduct WHERE id = " + id
                            + ";"));

            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addItem(id, name, price);
                }
            });
            panelRight.add(button);
        }

    }

}
