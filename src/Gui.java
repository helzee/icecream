import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;

public class Gui {
    static JFrame frame;
    static JPanel panelWorkspace;
    static JPanel panelMain;
    static JPanel panelNavToolBar;
    static JScrollPane scrollPanelLeft;
    static JPanel panelRight;
    static JPanel panelProducts;
    static JPanel panelModAdds;
    static JPanel panelModRemoves;

    static JPanel ilp;
    static JPanel itemPlaceHolder;
    static JPanel selectedItem;
    static Color normal = new Color(238,238,238);
    static JLabel WorkingEmployee;

    private static final String WINDOW_NAME = "Frozen Rock Ice Cream Shop";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;


    public static void build() throws SQLException {
        if (WorkingEmployee == null){
            showEmployeeSelect();
            return;
        }

        frame = new JFrame(WINDOW_NAME);
        frame.setSize(WIDTH, HEIGHT);

        panelWorkspace = new JPanel(new GridLayout(1, 2));
        panelMain = new JPanel(new BorderLayout());
        frame.add(panelMain);
        panelMain.add(panelWorkspace, BorderLayout.CENTER);
        panelNavToolBar = new JPanel(new FlowLayout());
        panelMain.add(panelNavToolBar, BorderLayout.NORTH);

        ilp = new JPanel();
        scrollPanelLeft = new JScrollPane(ilp);
        scrollPanelLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanelLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ilp.setLayout(new BoxLayout(ilp, BoxLayout.Y_AXIS));

        panelWorkspace.add(scrollPanelLeft);
        panelRight = new JPanel( new GridLayout(3,1));
        panelWorkspace.add(panelRight);


        JPanel panelMainProducts = new JPanel(new BorderLayout());
        panelProducts = new JPanel(new WrapLayout());
        JScrollPane scrollProducts = new JScrollPane(panelProducts);
        scrollProducts.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollProducts.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JLabel l0 = new JLabel("PRODUCTS", SwingConstants.CENTER);
        l0.setFont(new Font("Dialog", Font.PLAIN, 20));
        l0.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        panelMainProducts.add(l0, BorderLayout.NORTH);
        panelMainProducts.add(scrollProducts, BorderLayout.CENTER);
        panelRight.add(panelMainProducts);

        JPanel panelMainModAdds = new JPanel(new BorderLayout());
        panelModAdds = new JPanel(new WrapLayout());
        JScrollPane scrollModAdds = new JScrollPane(panelModAdds);
        scrollModAdds.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollModAdds.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JLabel l1 = new JLabel("MODIFICATIONS", SwingConstants.CENTER);
        l1.setFont(new Font("Dialog", Font.PLAIN, 20));
        l1.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        panelMainModAdds.add(l1, BorderLayout.NORTH);
        panelMainModAdds.add(scrollModAdds, BorderLayout.CENTER);

        panelRight.add(panelMainModAdds);


        JPanel panelMainModRemoves = new JPanel(new BorderLayout());
        panelModRemoves = new JPanel(new WrapLayout());
        JScrollPane scrollModRemoves = new JScrollPane(panelModRemoves);
        scrollModRemoves.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollModRemoves.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JLabel l2 = new JLabel("REMOVALS", SwingConstants.CENTER);
        l2.setFont(new Font("Dialog", Font.PLAIN, 20));
        l2.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        panelMainModRemoves.add(l2, BorderLayout.NORTH);
        panelMainModRemoves.add(scrollModRemoves, BorderLayout.CENTER);
        
        panelRight.add(panelMainModRemoves);



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

        JButton employeeSelect = new JButton("Select Worker");
        employeeSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    showEmployeeSelect();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        getItemButtons();

        panelNavToolBar.add(buttonNavToManager);
        panelNavToolBar.add(finishOrder);
        panelNavToolBar.add(employeeSelect);
        panelNavToolBar.add(WorkingEmployee);

        frame.setVisible(true);
    }

    private static void addItem(String id, String name, String price) throws SQLException {
        JPanel itemBox = new JPanel(new BorderLayout(10,5));
        itemBox.setMaximumSize(new Dimension(10000,25));
        itemBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    selectItem(itemBox);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                if (selectedItem == itemBox)
                    try {
                        selectItem(null);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    deleteItemAndMod(itemBox);
                //ilp.remove(itemBox); // remove the item from the Item Left Panel (ilp)
                //ilp.revalidate(); // refresh
                //ilp.repaint();
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
    }

    private static void selectItem(JPanel p) throws SQLException{
        if (selectedItem != null)
            selectedItem.setBackground(normal);
        if (p != null){
            selectedItem = p;
            selectedItem.setBackground(Color.LIGHT_GRAY);
        }

        updateRemoveButtons();
    }

    private static void addItemMod(String id, String name, String price, Boolean add) {
        // itembox panel to hold All item information

        JPanel itemBox = new JPanel(new BorderLayout(10,5));
        itemBox.setMaximumSize(new Dimension(10000,25));
        itemBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    selectItem(itemBox);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                if (selectedItem == itemBox)
                    try {
                        selectItem(null);
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

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
        JLabel nameText;
        if (add)
            nameText = new JLabel("+   " + name);
        else
            nameText = new JLabel("-    " + name);
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
    }
    
    private static void deleteItemAndMod(Component c){
        int i = getIndex(ilp, c) - 1;
        ilp.remove(i); // remove the item from the Item Left Panel (ilp)

        if (ilp.getComponentCount() < i) {
            ilp.revalidate(); // refresh
            ilp.repaint();
            return;
        }

        Component next = ilp.getComponent(i);
        Component[] ic = ((JPanel)next).getComponents(); // item components
        String name = (((JLabel)ic[1])).getText(); // should be itemboxleft

        while (name.charAt(0) == '+' || name.charAt(0) == '-'){
            ilp.remove(i); // remove the item from the Item Left Panel (ilp)
            if (ilp.getComponentCount() <= i) break;
            next = ilp.getComponent(i);
            ic = ((JPanel)next).getComponents(); // item components
            name = (((JLabel)ic[1])).getText(); // should be itemboxleft
        }

        ilp.revalidate(); // refresh
        ilp.repaint();
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

        String[] idsproj = Format.rsToArray(Execute.runQuery("SELECT id FROM MenuProduct WHERE isOffered is TRUE;"));

        for (String id : idsproj) {

            String name = Format.rsToString(Execute.runQuery("SELECT name FROM MenuProduct WHERE id = " + id +";"));
            String price = Format.rsToString(Execute.runQuery("SELECT currentPrice FROM MenuProduct WHERE id = " + id +";"));
            
            // button to add item to order
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        addItem(id, name, price);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            button.setBackground(new Color(204, 223, 255));
            panelProducts.add(button);
        }

        String[] idsmod = Format.rsToArray(Execute.runQuery("SELECT id FROM MenuModification WHERE isOffered is TRUE;"));

        for (String id : idsmod) {

            String name = Format.rsToString(Execute.runQuery("SELECT name FROM MenuModification WHERE id = " + id +";"));
            String price = Format.rsToString(Execute.runQuery("SELECT currentPrice FROM MenuModification WHERE id = " + id +";"));
            
            // button to add item to order
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addItemMod(id, name, price, true);
                }
            });
            button.setBackground(new Color(206, 240, 209));
            panelModAdds.add(button);
        }
    }

    public static void finishOrder() throws SQLException {
        Transaction newTx = new Transaction((int)(WorkingEmployee.getClientProperty("id"))); // CHANGE **
        newTx.finishTransaction();
        Component[] oi = ilp.getComponents(); // order items
        int lastItemTXID = -1;

        Vector<String> removesFromItem = new Vector<String>();
        // start at ilp 1 because 0th index is the title bar (id, name, price) not including the placeholder at the end
        for (int i = 1; i < oi.length; i++){ // go through each item in order and make its respective insert 
            Component[] ic = ((JPanel)oi[i]).getComponents(); // item components
            String id = ((JLabel)((JPanel)ic[0]).getComponent(1)).getText(); // should be itemboxleft
            String name = (((JLabel)ic[1])).getText(); // should be itemboxleft
            System.out.println(name);
            System.out.println(id);

            if (name.charAt(0) == '+') { // is a mod
                if (lastItemTXID != -1)
                    newTx.addProductModification(lastItemTXID, Integer.parseInt(id)); // add a mod to the last product
                    System.out.println("added mod " + name + " to last item created");
            }
            else if (name.charAt(0) == '-') { // is a mod removal
                if (lastItemTXID != -1 && !removesFromItem.contains(id))
                    newTx.removeProductIngredient(lastItemTXID, Integer.parseInt(id)); // add a mod to the last product
                    System.out.println("Removed ingredient " + name + " to last item created");
                    removesFromItem.add(id);
            }
            else{
                lastItemTXID = newTx.addProduct(Integer.parseInt(id)); // add a new product
                removesFromItem.clear();
                System.out.println("added item " + name);
            }
        }
        newTx.finishTransaction();
        showReciept(newTx.getReceipt());
        clearILP();
    }

    private static void showReciept(String s) {
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

    private static void showEmployeeSelect() throws SQLException {
        JFrame newframe = new JFrame("Select Working Employee");

        JPanel editEmployee = new JPanel();
        JScrollPane scrollpanel = new JScrollPane(editEmployee);
        editEmployee.setLayout(new BoxLayout(editEmployee, BoxLayout.Y_AXIS));
        ResultSet employees = Execute.runQuery("SELECT id, firstName, LastName FROM Employee;");
  
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
            JPanel p = new JPanel( new BorderLayout());
            p.add(b);
           editEmployee.add(p);
        }
  
        for (int i = 0; i < empButtons.size(); i++) {
            empButtons.get(i).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (WorkingEmployee == null){
                        WorkingEmployee = new JLabel();
                    }
                    WorkingEmployee.setText("Current Worker: " + ((JButton) e.getSource()).getText());
                    WorkingEmployee.putClientProperty("id", ((int) ((JButton) e.getSource()).getClientProperty("id")));
                    ((JFrame)(scrollpanel.getParent().getParent().getParent().getParent())).dispose();
                    try {
                        updateWorkerText();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        
        newframe.add(scrollpanel);
        newframe.setSize(400,200);
        newframe.setVisible(true);
     }

    private static void updateWorkerText() throws SQLException {
        if (frame == null){
            build();
        }
    }

    private static void updateRemoveButtons() throws SQLException {
        panelModRemoves.removeAll();
        if (selectedItem == null) return;

        Component[] ic = (selectedItem).getComponents(); // item components
        String projID = ((JLabel)((JPanel)ic[0]).getComponent(1)).getText(); // should be itemboxleft
        String[] idsitem = Format.rsToArray(Execute.runQuery("SELECT itemId FROM ProductIngredient WHERE productID = " + projID + ";"));

        for (String id : idsitem) {

            String name = Format.rsToString(Execute.runQuery("SELECT name FROM Item WHERE id = " + id +";"));
            
            // button to add item to order
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addItemMod(id, name, "0.00", false);
                }
            });
            button.setBackground(new Color(209, 169, 171));
            panelModRemoves.add(button);
        }
        panelModRemoves.revalidate(); // refresh
        panelModRemoves.repaint();
    }

    private static void clearILP() throws SQLException {
        selectItem(null);
        updateRemoveButtons();
        if (ilp.getComponentCount() <= 1) return;
        while (ilp.getComponent(1) != null){
            ilp.remove(ilp.getComponent(1));
            if (ilp.getComponentCount() <= 1) break;
        }
        ilp.revalidate(); // refresh
        ilp.repaint();
    }
}







































