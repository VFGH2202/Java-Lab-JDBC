package Client;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;

public class Client_GUI extends JFrame {
    Socket client;
    Client_Socket cs;
    private JPanel root;
    private JButton connect_bt;
    private JFormattedTextField addr_field;
    private JTextField fullnamTxfield;
    private JButton sendBt;
    private JTextField locationTxfield;
    private JComboBox prodBox;
    private JTextField quatityTxfield;

    class Client_Socket implements Runnable {
        DataInputStream in;
        ObjectOutputStream outob;
        ObjectInputStream inob;
        boolean worked;
        ArrayList<String> prlist = new ArrayList<>();

        Client_Socket(InputStream inStr, OutputStream outStr) {
            in = new DataInputStream(inStr);
            try {
                outob = new ObjectOutputStream(outStr);
                inob = new ObjectInputStream(inStr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            worked = true;
            new Thread(this).start();
        }
        public void send_to_server(Map dt) {
            try {
                outob.writeObject(dt);
                outob.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        private void create_map(String name, String locat, String pr, Integer col) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("location", locat);
            data.put("prod", pr);
            data.put("col", col);
            send_to_server(data);
        }

        public void run() {
            try {
                prlist = (ArrayList<String>) inob.readObject();
                for (int i = 0; i<prlist.size();i++) {
                    prodBox.addItem(prlist.get(i));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                String l = null;
                while (worked) {
                    l = in.readUTF();
                    System.out.println(l);
                    if (l.equals("lock")) sendBt.setEnabled(false);
                    if (l.equals("unlock")) sendBt.setEnabled(true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Client_GUI(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(root);
        try {
            MaskFormatter mask = new MaskFormatter("###.###.###.###:####");
            mask.install(addr_field);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        connect_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] out = addr_field.getText().split(":");
                    System.out.println(out[0]);
                    System.out.println(out[1]);
                    InetAddress ia = InetAddress.getByName(out[0]);
                    int port = Integer.parseInt(out[1]);
                    client = new Socket(ia, port);

                    connect_bt.setText("Подключено");
                    InputStream inst = client.getInputStream();
                    OutputStream outst = client.getOutputStream();
                    cs = new Client_Socket(inst, outst);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        sendBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = "";
                name = fullnamTxfield.getText().trim();
                String locat = "";
                locat = locationTxfield.getText().trim();
                String pr = "";
                pr = prodBox.getSelectedItem().toString();
                Integer col = 0;
                col = Integer.parseInt(quatityTxfield.getText());

                cs.create_map(name, locat, pr, col);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (cs != null){
                    String name = "";
                    name = fullnamTxfield.getText().trim();
                    String locat = "";
                    locat = locationTxfield.getText().trim();
                    String pr = "";
                    pr = prodBox.getSelectedItem().toString();
                    Integer col = 0;
                    quatityTxfield.setText("0");
                    col = Integer.valueOf(quatityTxfield.getText());
                    name = "stop";
                    cs.create_map(name, locat, pr, col);
                }
                dispose();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        Client_GUI add = new Client_GUI("Client");
        add.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        add.pack();
        add.setResizable(true);
        add.setVisible(true);
    }

}
