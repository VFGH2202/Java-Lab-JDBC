package Server.Controller;

import Server.DbHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class View_Prod_Table extends JFrame {
    private JButton add_new_prod;
    private JTable viewtab;
    private JPanel root;
    private JButton redraw_table;
    private JLabel selected_label;
    private JButton del_bt;
    Integer id_selected = null;

    public View_Prod_Table(String title){
        super(title);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
        Update_list();



        add_new_prod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Add_prod create_pr = new Add_prod("Add Product");
            }
        });

        redraw_table.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Update_list();
            }
        });

        viewtab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                StringBuilder s = new StringBuilder(" id: ");
                id_selected = Integer.valueOf(String.valueOf(source.getModel().getValueAt(row, 0)));
                System.out.println(id_selected);
                for (int i = 0;i<2;i++)
                    s.append(source.getModel().getValueAt(row, i)).append(" ");
                selected_label.setText("");
                selected_label.setText(s.toString());
            }
        });

        del_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DbHandler dbHandler = new DbHandler();
                dbHandler.delete_item(id_selected);
            }
        });

    }

    private void Update_list(){
        try {
            DbHandler dbHandler = new DbHandler();
            ResultSet rs = dbHandler.getAll();
            ResultSetMetaData rsMd = rs.getMetaData();
            int numColumns = rsMd.getColumnCount();
            DefaultTableModel mod = new DefaultTableModel();
            viewtab.setModel(mod);
            for (int i=1; i<=numColumns;i++){
                mod.addColumn(rsMd.getColumnLabel(i));
            }
            while (rs.next()){  // View table
                Object [] file = new Object[numColumns];
                for (int i=0; i<numColumns;i++){
                    file[i] = rs.getObject(i+1).toString(); //Полиморфизм
                }
                mod.addRow(file);
            }
            for (int i=0; i<viewtab.getRowCount();i++) {
                Integer min = Integer.parseInt((String) viewtab.getValueAt(i, 2));
                Integer now = Integer.parseInt((String) viewtab.getValueAt(i, 3));
                String name = (String) viewtab.getValueAt(i, 1);
                Integer col_buy = 0;
                if (now < min) {
                    if (now < 0) col_buy = (now * (-1) + min);
                    else col_buy = min - now;
                    String over = JOptionPane.showInputDialog("Будет заказано " + col_buy.toString() +
                            " единиц, введите избыток");
                    col_buy = min + Integer.valueOf(over);
                    System.out.println(col_buy);
                    dbHandler.Purchase(name, col_buy);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
