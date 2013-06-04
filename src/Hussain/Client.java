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

    public boolean start(){
        //try to connect to the server
        try{
           socket = new Socket(server,port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        String message = "Connection Accepted: "+ socket.getInetAddress() + ": "+socket.getPort();
        display(message);
        //try to create both data streams
        try{
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch(IOException exception){
            display("Exception creating new Input/output Streams: " + exception);
            return false;
        }
        new ListenFromServer().start();
        try{
            outputStream.writeObject(username);
        }
        catch(IOException exception){
            display("Exception doing login : " + exception);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String message){
        if(clientGUI == null)
        {
            System.out.println(message);
        }
        else
        {
            //TODO Come back to this
        }
    }

    private void disconnect(){

    }













    class ListenFromServer extends Thread{
        @Override
        public void run(){
           while(true)
           {
               try{
                   String message = (String)inputStream.readObject();
                   if(clientGUI == null)
                   {
                       System.out.println(message);
                       System.out.println("> ");
                   }
                   else
                   {
                       //TODO come back to this
                   }
               }
               catch(ClassNotFoundException ignore){}
               catch(IOException exception){
                   display("Server has close the connection: " + exception);
                   if(clientGUI != null)
                   {
                       //TODO come back to this
                   }
                   break;
               }
           }
        }
    }

}
