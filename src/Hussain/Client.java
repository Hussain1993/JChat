package Hussain;

import GUI.ClientGUI;

import java.io.*;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hussain
 * Date: 04/06/2013
 * Time: 14:32
 * Project Name: JChat
 */
public class Client {
    //This is for I/O
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;

    private ClientGUI clientGUI; //If we are using the GUI or not

    private String server; //This is the server
    private String username; //This is the username of the person
    private int port; //This is the port number

    public Client(String server, String username, int port, ClientGUI clientGUI){
        this.server = server;
        this.username = username;
        this.port = port;
        this.clientGUI = clientGUI;
    }

    public Client(String server, String username, int port){
        this(server,username,port,null);
    }

}
