package Server.Controller;

import Server.DbHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Server_GUI extends JFrame {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    final int port = 8888;
    int clients_socket_col = 0;
    Client_Socket cw[] = new Client_Socket[50];
    InetAddress addr;

    class Connection_Waiter implements Runnable {
        Connection_Waiter() {
            new Thread(this).start();
        }
        public void run(){
            while (true) {
                try {
                        clientSocket = serverSocket.accept();
                        InputStream in = clientSocket.getInputStream();
                        OutputStream out = clientSocket.getOutputStream();
                        cw[clients_socket_col] = new Client_Socket(in, out);
                        clients_socket_col++;
                        server_area.append("Клиент подключился\n");
                    } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class Client_Socket implements Runnable {
        DataOutputStream out;
        ObjectInputStream inob;
        ObjectOutputStream outob;
        boolean worked;


        Client_Socket(InputStream inStr, OutputStream outStr) {
            try {
                inob = new ObjectInputStream(inStr);
                outob = new ObjectOutputStream(outStr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            out = new DataOutputStream(outStr);
            worked = true;
            new Thread(this).start();
        }

        private synchronized void addd_order(Map save) {
            server_area.append(save.toString());
            server_area.append("\n");
            String name = (String) save.get("prod");
            String full = (String) save.get("name");
            String loc = (String) save.get("location");
            Integer quan = (Integer) save.get("col");
            DbHandler dbHandler = new DbHandler();
            dbHandler.add_order(name, loc, full, quan);
            dbHandler.UpdateCol(name, quan);
        }

        public void stop() {
            server_area.append("Клиент отключен\n");
            worked = false;
        }

        private void send_Button_status(String s) {
            try {
                out.writeUTF(s);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void run() {
            // ArrayList with list of products
            try {
                DbHandler dbHandler = new DbHandler();
                ResultSet rs = dbHandler.getAll();
                ArrayList<String> prosuctsList = new ArrayList<>();
                while (rs.next()){
                    prosuctsList.add(rs.getString(2));
                }
                outob.writeObject(prosuctsList);    // Sending list
                outob.flush();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

            try {
                while (worked) {
                    Map dat = (Map) inob.readObject();  // Receiving Map with order data
                    if (dat.containsValue("stop")){ // Disconnect
                        stop();
                        clients_socket_col--;
                        server_area.append("Активных одключений: " + clients_socket_col + "\n");
                    }
                    else {
                        send_Button_status("lock");
                        addd_order(dat);
                        send_Button_status("unlock");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("br");
        }
    }

    private JButton print_IP;
    private JButton list_bt;
    private JPanel root;
    private JButton sales_bt;
    private JTextArea server_area;

    public Server_GUI(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(root);

        print_IP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server_area.append("Address:    " + addr.getHostAddress() + "\n");
                server_area.append("Port:     " + port + "\n");
            }
        });

        list_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                View_Prod_Table view = new View_Prod_Table("Products list");
            }
        });
        sales_bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Order_Table ordTab = new Order_Table("Orders");
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (clients_socket_col == 0){
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        Server_GUI add = new Server_GUI("Server Window");
        add.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        add.pack();
        add.setResizable(false);
        add.setVisible(true);
        add.start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            addr = InetAddress.getLoopbackAddress();
            server_area.append("Address:    " + addr.getHostAddress() + "\n");
            server_area.append("Port:     " + port + "\n");
            new Connection_Waiter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
