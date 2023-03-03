package Server.Controller;

import Server.DbHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Order_Table extends JFrame{
    private JPanel root;
    private JTable order_jtabel;
    private JButton del_bt;
    private JLabel selected_line;
    private JButton order_upd;
    Integer id_selected = null;

    public Order_Table(String title) {
        super(title);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
        Update_list();

        order_jtabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                StringBuilder s = new StringBuilder(" id: ");
                id_selected = Integer.valueOf(String.valueOf(source.getModel().getValueAt(row, 0)));
                System.out.println(id_selected);
                for (int i = 0;i<4;i++)
                    s.append(source.getModel().getValueAt(row, i)).append(" ");
                selected_line.setText("");
                selected_line.setText(s.toString());
            }
        });
        del_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DbHandler dbHandler = new DbHandler();
                dbHandler.orderDeleteItem(id_selected);
            }
        });
        order_upd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Update_list();
            }
        });
    }

    private void Update_list(){
        try {
            DbHandler dbHandler = new DbHandler();
            ResultSet rs = dbHandler.orderGetAll();
            ResultSetMetaData rsMd = rs.getMetaData();
            int numColumns = rsMd.getColumnCount();
            DefaultTableModel mod = new DefaultTableModel();
            order_jtabel.setModel(mod);
            System.out.println(rs);
            for (int i=1; i<=numColumns;i++){
                mod.addColumn(rsMd.getColumnLabel(i));
            }
            while (rs.next()){
                Object [] file = new Object[numColumns];
                for (int i=0; i<numColumns;i++){
                    file[i] = rs.getObject(i+1).toString(); //Полиморфизм
                }
                mod.addRow(file);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
