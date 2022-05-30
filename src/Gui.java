import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import javax.sound.midi.SysexMessage;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

public class Gui {
    static JFrame frame;
    static JPanel panelWorkspace;
    static JPanel panelMain;
    static JPanel panelNavToolBar;
    static JScrollPane scrollPanelLeft;
    static JPanel panelRight;
    static JPanel ilp;
    static JPanel itemPlaceHolder;
    static JPanel selectedItem;
    static Color normal = new Color(238,238,238);

    private static final String WINDOW_NAME = "Frozen Rock Ice Cream Shop";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;


    public static void build() {
        frame = new JFrame(WINDOW_NAME);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        panelWorkspace = new JPanel(new GridLayout(1, 2));
        panelMain = new JPanel(new BorderLayout());
        frame.add(panelMain);
        panelMain.add(panelWorkspace, BorderLayout.CENTER);
        panelNavToolBar = new JPanel(new FlowLayout());
        panelMain.add(panelNavToolBar, BorderLayout.NORTH);

        ilp = new JPanel();
        scrollPanelLeft = new JScrollPane(ilp);
        ilp.setLayout(new BoxLayout(ilp, BoxLayout.Y_AXIS));

        panelWorkspace.add(scrollPanelLeft);
        panelRight = new JPanel(new FlowLayout());
        panelWorkspace.add(panelRight);
        
        /* used for manually making buttons
        for (int i = 0; i < 0; i++) {
            JTextArea item = new JTextArea("vanilla icecream");
            item.setMaximumSize(item.getPreferredSize());
            item.setBackground(Color.CYAN);
            ilp.add(item);
        }

        JButton button1 = new JButton("vanilla");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem("vanilla");
            }
        });
        JButton button2 = new JButton("chocolate");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem("chocolate");
            }
        });
        JButton button3 = new JButton("strawberry");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem("strawberry");
            }
        });
        */

        JButton buttonNavToManager = new JButton("Change To Manager GUI");
        buttonNavToManager.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ManagerGUI.build();
            }
        });
        JButton finishOrder = new JButton("Complete Order");
        finishOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    finishOrder();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        
        getItemButtons();

        panelNavToolBar.add(buttonNavToManager);
        panelNavToolBar.add(finishOrder);

        frame.setVisible(true);
    }

    private static void addItem(String id, String name, String price) {
        // itembox panel to hold All item information

        JPanel itemBox = new JPanel(new BorderLayout(10,5));
        itemBox.setMaximumSize(new Dimension(10000,25));
        itemBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                selectItem(itemBox);
            }
        });

        JPanel itemBoxLeft = new JPanel(new BorderLayout(2,0));
        itemBoxLeft.setMaximumSize(new Dimension(80,25));
        itemBoxLeft.setOpaque(false);
        itemBox.add(itemBoxLeft,BorderLayout.WEST);
        

        // Delete Button to delete the item from the order
        JButton DeleteButton = new JButton("X");
        DeleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedItem == itemBox) selectItem(null);
                ilp.remove(itemBox); // remove the item from the Item Left Panel (ilp)
                ilp.revalidate(); // refresh
                ilp.repaint();
            }
        });
        DeleteButton.setMaximumSize(new Dimension(20,20));
        itemBoxLeft.add(DeleteButton,BorderLayout.WEST);
        
        // text displaying id
        JLabel idText = new JLabel(id);
        itemBoxLeft.add(idText, BorderLayout.EAST);

        // text displaying name
        JLabel nameText = new JLabel(name);
        nameText.setMaximumSize(nameText.getPreferredSize());
        nameText.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        itemBox.add(nameText,BorderLayout.CENTER);

        // text displaying price
        JLabel priceText = new JLabel(price);
        priceText.setMaximumSize(priceText.getPreferredSize());
        priceText.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        itemBox.add(priceText,BorderLayout.EAST);

        //ilp.remove(itemPlaceHolder);
        ilp.add(itemBox);
        selectItem(itemBox);

        frame.setVisible(true);

        System.out.print(itemBox.getBackground());
    }
    private static void selectItem(JPanel p){
        if (selectedItem != null)
            selectedItem.setBackground(normal);
        if (p != null){
            selectedItem = p;
            selectedItem.setBackground(Color.LIGHT_GRAY);
        }
    }

    private static void addItemMod(String id, String name, String price) {
        // itembox panel to hold All item information

        JPanel itemBox = new JPanel(new BorderLayout(10,5));
        itemBox.setMaximumSize(new Dimension(10000,25));
        itemBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                selectItem(itemBox);
            }
        });

        JPanel itemBoxLeft = new JPanel(new BorderLayout(2,0));
        itemBoxLeft.setMaximumSize(new Dimension(80,25));
        itemBoxLeft.setOpaque(false);
        itemBox.add(itemBoxLeft,BorderLayout.WEST);
        

        // Delete Button to delete the item from the order
        JButton DeleteButton = new JButton("X");
        DeleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedItem == itemBox) selectItem(null);
                ilp.remove(itemBox); // remove the item from the Item Left Panel (ilp)
                ilp.revalidate(); // refresh
                ilp.repaint();
            }
        });
        DeleteButton.setMaximumSize(new Dimension(20,20));
        itemBoxLeft.add(DeleteButton,BorderLayout.WEST);
        
        // text displaying id
        JLabel idText = new JLabel(id);
        itemBoxLeft.add(idText, BorderLayout.EAST);

        // text displaying name
        JLabel nameText = new JLabel("(" + name + ")");
        nameText.setMaximumSize(nameText.getPreferredSize());
        nameText.setBorder(BorderFactory.createEmptyBorder(0,40,0,0));
        itemBox.add(nameText,BorderLayout.CENTER);

        // text displaying price
        JLabel priceText = new JLabel(price);
        priceText.setMaximumSize(priceText.getPreferredSize());
        priceText.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        itemBox.add(priceText,BorderLayout.EAST);

        //ilp.remove(itemPlaceHolder);
        ilp.add(itemBox,getIndex(ilp, selectedItem));

        frame.setVisible(true);

        System.out.print(itemBox.getBackground());
    }
    
  
    private static void getItemButtons() { // change so that when clicked it adds the id as well as the name and price to the building reciept
        JPanel itemBox = new JPanel(new BorderLayout(10,5));
        itemBox.setMaximumSize(new Dimension(10000,25));

        JPanel itemBoxLeft = new JPanel(new BorderLayout(2,0));
        itemBoxLeft.setMaximumSize(new Dimension(80,25));
        itemBox.add(itemBoxLeft,BorderLayout.WEST);

        JLabel idText = new JLabel("ID", SwingConstants.CENTER);
        idText.setMaximumSize(idText.getPreferredSize());
        idText.setBorder(BorderFactory.createEmptyBorder(0,50,0,0));
        itemBoxLeft.add(idText, BorderLayout.EAST);

        // name label
        JLabel nameText = new JLabel("NAME", SwingConstants.CENTER);
        nameText.setMaximumSize(nameText.getPreferredSize());
        itemBox.add(nameText,BorderLayout.CENTER);

        // price label
        JLabel priceText = new JLabel("PRICE", SwingConstants.CENTER);
        priceText.setMaximumSize(priceText.getPreferredSize());
        priceText.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        itemBox.add(priceText,BorderLayout.EAST);

        ilp.add(itemBox);

        String[] idsitem = Format.rsToArray(Execute.runQuery("SELECT id FROM MenuProduct WHERE isOffered is TRUE;"));

        for (String id : idsitem) {

            String name = Format.rsToString(Execute.runQuery("SELECT name FROM MenuProduct WHERE id = " + id +";"));
            String price = Format.rsToString(Execute.runQuery("SELECT currentPrice FROM MenuProduct WHERE id = " + id +";"));
            
            // button to add item to order
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addItem(id, name, price);
                }
            });

            panelRight.add(button);
        }

        String[] idsmod = Format.rsToArray(Execute.runQuery("SELECT id FROM MenuModification WHERE isOffered is TRUE;"));

        for (String id : idsmod) {

            String name = Format.rsToString(Execute.runQuery("SELECT name FROM MenuModification WHERE id = " + id +";"));
            String price = Format.rsToString(Execute.runQuery("SELECT currentPrice FROM MenuModification WHERE id = " + id +";"));
            
            // button to add item to order
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addItemMod(id, name, price);
                }
            });
            button.setBackground(Color.LIGHT_GRAY);
            panelRight.add(button);
        }
    }

    public static void finishOrder() throws SQLException {
        String jimmyID = Format.rsToString(Execute.runQuery("SELECT id, firstname, lastname FROM Employee"));
        System.out.println(jimmyID);
        //String jimmyID = Format.rsToString(Execute.runQuery("SELECT id FROM Employee WHERE firstname = 'jimmy' AND lastname = 'bob' LIMIT 1;"));
        System.out.println(jimmyID);
        Transaction newTx = new Transaction(Integer.parseInt(jimmyID)); // CHANGE **
        newTx.finishTransaction();
        Component[] oi = ilp.getComponents(); // order items
        
        int lastItemTXID = -1;

        // start at ilp 1 because 0th index is the title bar (id, name, price) not including the placeholder at the end
        for (int i = 1; i < oi.length; i++){ // go through each item in order and make its respective insert 
            Component[] ic = ((JPanel)oi[i]).getComponents(); // item components
            String id = ((JLabel)((JPanel)ic[0]).getComponent(1)).getText(); // should be itemboxleft
            String name = (((JLabel)ic[1])).getText(); // should be itemboxleft

            //System.out.println(name);

            if (name.charAt(0) == '(' && name.charAt(name.length() - 1) == ')' ) { // is a mod
                if (lastItemTXID != -1)
                    newTx.addProductModification(lastItemTXID, Integer.parseInt(id)); // add a mod to the last product
                    System.out.println("added mod " + name + " to last item created");
            }
            else{
                lastItemTXID = newTx.addProduct(Integer.parseInt(id)); // add a new product
                System.out.println("added item " + name);
            }
        }
        newTx.finishTransaction();
        showReciept(newTx.getReceipt());
    }


    private static void showReciept(String s) {
        System.out.println(s);
        JFrame receiptframe = new JFrame();
        JPanel panel = new JPanel(); 
        JScrollPane scrollpanel = new JScrollPane(panel);

        JLabel reciept = new JLabel();
        reciept.setText("<html><pre>" + s.replaceAll("<","&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</pre></html>");
        //Font  f1  = new Font(Font.MONOSPACED, Font.PLAIN,  10);
        //reciept.setFont(f1);
        receiptframe.add(scrollpanel);
        panel.add(reciept);
        receiptframe.setSize(400,500);
        receiptframe.setVisible(true);
    }

    private static int getIndex(JPanel p, Component c) {
        Component[] cl = p.getComponents();
        if (c == null) return cl.length;
        for (int i = 0; i < cl.length;i++){
            if (cl[i] == c) return i + 1;
        }
        System.out.println("component not in list");
        return cl.length;
    }
}
