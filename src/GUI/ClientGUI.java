package GUI;

import Hussain.ChatMessage;
import Hussain.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Hussain
 * Date: 04/06/2013
 * Time: 14:32
 * Project Name: JChat
 */
public class ClientGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JLabel label;
    private JTextField textField;

    private JTextField serverField;
    private JTextField portNumberField;

    private JButton loginInButton;
    private JButton logOutButton;
    private JButton whoIsInButton;

    private JTextArea chatArea;

    private JPanel northPanel;
    private JPanel serverAndPort;
    private JPanel centerPanel;
    private JPanel southPanel;

    private boolean connected;

    private Client client;

    private int defaultPortNumber;
    private String defaultHost;

    public ClientGUI(String host, int portNumber){
        super("Chat Client");
        defaultPortNumber = portNumber;
        defaultHost = host;
        initWidgets();
        addWidgets();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(600,600));
        setVisible(true);
        textField.requestFocus();
    }

    private void initWidgets(){
        northPanel = new JPanel(new GridLayout(3,1));
        serverAndPort =  new JPanel(new GridLayout(1,5,1,3));
        serverField = new JTextField(defaultHost);
        portNumberField = new JTextField(""+defaultPortNumber);
        portNumberField.setHorizontalAlignment(SwingConstants.RIGHT);
        label = new JLabel("Enter your username below,",SwingConstants.CENTER);

        textField = new JTextField("Anonymous");
        textField.setBackground(Color.WHITE);

        chatArea = new JTextArea("Welcome to the Chat Room\n",80,80);
        chatArea.setEditable(false);

        centerPanel = new JPanel(new GridLayout(1,1));

        loginInButton = new JButton("Login");
        loginInButton.addActionListener(this);
        logOutButton = new JButton("Logout");
        logOutButton.addActionListener(this);
        logOutButton.setEnabled(false);
        whoIsInButton = new JButton("Who is in");
        whoIsInButton.addActionListener(this);
        whoIsInButton.setEnabled(false);

        southPanel = new JPanel();
    }

    private void addWidgets(){
        serverAndPort.add(new JLabel("Server Address: "));
        serverAndPort.add(serverField);
        serverAndPort.add(new JLabel("Port Number: "));
        serverAndPort.add(portNumberField);
        serverAndPort.add(new JLabel(""));

        northPanel.add(serverAndPort);
        northPanel.add(label);

        northPanel.add(textField);
        add(northPanel,BorderLayout.NORTH);

        centerPanel.add(new JScrollPane(chatArea));

        add(centerPanel,BorderLayout.CENTER);

        southPanel.add(loginInButton);
        southPanel.add(logOutButton);
        southPanel.add(whoIsInButton);
        add(southPanel,BorderLayout.SOUTH);
    }

    public void append(String string){
        chatArea.append(string);
        chatArea.setCaretPosition(chatArea.getText().length() - 1);

    }

    public void connectionFailed(){
        loginInButton.setEnabled(true);
        logOutButton.setEnabled(false);
        whoIsInButton.setEnabled(false);
        label.setText("Enter your username below");
        textField.setText("Anonymous");
        portNumberField.setText(""+defaultPortNumber);
        serverField.setText(defaultHost);
        serverField.setEditable(false);
        portNumberField.setEditable(false);
        textField.removeActionListener(this);
        connected = false;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object o = actionEvent.getSource();
        if(o == logOutButton)
        {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT,""));
            return;
        }
        else if(o == whoIsInButton)
        {
            client.sendMessage(new ChatMessage(ChatMessage.WHOISIN,""));
            return;
        }
        if(connected)
        {
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE,textField.getText()));
            textField.setText("");
            return;
        }
        if(o == loginInButton)
        {
            String username = textField.getText().trim();
            if(username.isEmpty())
            {
                return;
            }
            String serverName = serverField.getText().trim();
            if(serverName.isEmpty())
            {
                return;
            }
            String portNumber = portNumberField.getText().trim();
            if(portNumber.isEmpty())
            {
                return;
            }
            int port;
            try{
               port = Integer.parseInt(portNumber);
            }
            catch(Exception exception){
               return;
            }
            client = new Client(serverName,username,port,this);
            if(!client.start())
            {
                return;
            }
            textField.setText("");
            label.setText("Enter your message below");
            connected = true;
            loginInButton.setEnabled(false);
            logOutButton.setEnabled(true);
            whoIsInButton.setEnabled(true);
            serverField.setEditable(false);
            portNumberField.setEditable(false);
            textField.addActionListener(this);
        }
    }

    public static void main(String [] args){
        new ClientGUI("localhost",1500);
    }
}
