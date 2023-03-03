package Server.Controller;

import Server.DbHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Add_prod extends JFrame {
    String name = "None";
    Integer Col, minCol;
    private JTextField prod_name;
    private JTextField min_col;
    private JPanel add_pr;
    private JButton add_bt;
    private JTextField col;

    private boolean checkStringIsDigit(String text) {
        if (text!=null)
        {
            for(int i=0; i<text.length(); i++)
            {
                if (!Character.isDigit(text.charAt(i)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public Add_prod(String title) {
        super(title);
        setContentPane(add_pr);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        add_bt.requestFocusInWindow();
        setResizable(false);
        setVisible(true);

        DbHandler dbHandler = new DbHandler();

        add_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        prod_name.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(prod_name.getText().equals("Название продукта")) {
                    prod_name.setText("");
                }
            }
            public void focusLost(FocusEvent e) {
                if(prod_name.getText().isEmpty()) {
                    prod_name.setText("Название продукта");
                }
                else {
                    name = prod_name.getText().trim();
                }
            }
        });

        col.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(col.getText().equals("Количество")) {
                    col.setText("");
                }
            }
            public void focusLost(FocusEvent e) {
                if(col.getText().isEmpty()) {
                    col.setText("Количество");
                }
                else {
                    String buf = col.getText().trim();
                    if(checkStringIsDigit(buf)) Col = Integer.valueOf(buf);
                    else col.setText("Количество");
                }
            }
        });

        min_col.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(min_col.getText().equals("Минимальное количество")) {
                    min_col.setText("");
                }
            }
            public void focusLost(FocusEvent e) {
                if(min_col.getText().isEmpty()) {
                    min_col.setText("Минимальное количество");
                }
                else {
                    String buf = min_col.getText().trim();
                    if(checkStringIsDigit(buf)) minCol = Integer.valueOf(buf);
                    else min_col.setText("Минимальное количество");
                }
            }
        });

        add_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(name);
                System.out.println(Col);
                System.out.println(minCol);

                prod_name.setText("Название продукта");
                col.setText("Количество");
                min_col.setText("Минимальное количество");

                dbHandler.create_product(name, Col, minCol);
            }
        });
    }

}
