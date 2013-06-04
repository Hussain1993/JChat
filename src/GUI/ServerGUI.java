package GUI;

import Hussain.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Hussain
 * Date: 03/06/2013
 * Time: 20:04
 * Project Name: JChat
 */
public class ServerGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JButton startStopButton;
    private JTextArea chatArea;
    private JTextArea eventArea;
    private JTextField portNumberField;

    private Server server;

    public ServerGUI(int portNumber){
        super("Chat Server");
        server = null;
        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Port Number: "));
        portNumberField = new JTextField(" "+portNumber);
        northPanel.add(portNumberField);
        startStopButton = new JButton("Start");
        startStopButton.addActionListener(this);
        northPanel.add(startStopButton);
        add(northPanel, BorderLayout.NORTH);

        JPanel centrePanel = new JPanel(new GridLayout(2,1));
        chatArea = new JTextArea(80,80);
        chatArea.setEditable(false);
        appendRoom("Chat Room. \n");
        centrePanel.add(new JScrollPane(chatArea));
        eventArea = new JTextArea(80,80);
        eventArea.setEditable(false);
        appendEvent("Events Logs. \n");
        centrePanel.add(new JScrollPane(eventArea));
        add(centrePanel,BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(server !=  null)
                {
                    try{
                        server.stop();
                    }
                    catch(Exception ignore){}
                    server = null;
                }
                ServerGUI.this.dispose();
                System.exit(0);
            }
        });
        setSize(new Dimension(400,600));
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(server != null)
        {
            server.stop();
            server = null;
            portNumberField.setEditable(true);
            startStopButton.setText("Start");
            appendEvent("The server has been stopped.\n");
            return;
        }
        int portNumber;
        try{
            portNumber = Integer.parseInt(portNumberField.getText().trim());
        }
        catch(Exception exception){
            appendEvent("Invalid port number.\n");
            return;
        }
        server = new Server(portNumber,this);
        new ServerRunning().start();
        startStopButton.setText("Stop");
        portNumberField.setEditable(false);
        appendEvent("The server has been started.\n");
    }

    public void appendRoom(String string){
        chatArea.append(string);
        chatArea.setCaretPosition(chatArea.getText().length() - 1);
    }

    public void appendEvent(String string){
        eventArea.append(string);
        eventArea.setCaretPosition(chatArea.getText().length() - 1);
    }

    public static void main(String [] args){
        new ServerGUI(1500);

    }


    class ServerRunning extends Thread{
        @Override
        public void run(){
            server.start();
            startStopButton.setText("Start");
            portNumberField.setEditable(true);
            appendEvent("Server Failed. \n");
            server = null;
        }
    }
}