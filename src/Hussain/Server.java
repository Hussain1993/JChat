package Hussain;

import GUI.ServerGUI;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Hussain
 * Date: 03/06/2013
 * Time: 19:57
 * Project Name: JChat
 */
public class Server {
    private static int uniqueId;
    private ArrayList<ClientThread> clientThreads;
    private ServerGUI serverGUI;
    private SimpleDateFormat simpleDateFormat;
    private int port;//This is the port number to listen for connections
    private boolean keepGoing;

    public Server(int port, ServerGUI serverGUI){
        this.serverGUI = serverGUI;
        this.port = port;
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        clientThreads = new ArrayList<ClientThread>();
    }

    public Server(int port){
        this(port,null);
    }

    public void start(){
        keepGoing = true;
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            //infinite loop for connections
            while(keepGoing)
            {
               display("Server waiting for Clients on port "+port+".");
               Socket socket = serverSocket.accept(); //Accept connections
               if(!keepGoing)
               {
                   break;
               }
               ClientThread thread = new ClientThread(socket); //Make a thread of it
               clientThreads.add(thread);
               thread.start();
            }
            try{
                serverSocket.close();
                for(int i = 0; i < clientThreads.size(); ++i)
                {
                    ClientThread ct = clientThreads.get(i);
                    try{
                        ct.outputStream.close();
                        ct.inputStream.close();
                        ct.socket.close();
                    }
                    catch(IOException ignore){}
                }
            }
            catch(Exception e){
                display("Exception closing the server and clients: " + e);
            }
        }
        catch(IOException exception){
            String msg = simpleDateFormat.format(new Date()) + " Exception on new ServerSocket: " + exception + "\n";
            display(msg);
        }
    }

    public void stop(){
        keepGoing = false;
        try{
            new Socket("localhost",port);
        }
        catch(Exception ignore){}
    }


    private void display(String message){
        String time = simpleDateFormat.format(new Date()) + " " + message;
        if(serverGUI == null)
        {
            System.out.println(time);
        }
        else
        {
            serverGUI.appendEvent(time+"\n");
        }
    }

    private synchronized void broadcast(String message){
        String time = simpleDateFormat.format(new Date());
        String messageLongForm = time + " " + message + "\n";
        if(serverGUI == null)
        {
            System.out.print(messageLongForm);
        }
        else
        {
            serverGUI.appendRoom(messageLongForm);
        }
        for(int i = clientThreads.size(); --i >= 0;)
        {
            ClientThread ct = clientThreads.get(i);
            if(!ct.writeMessage(messageLongForm))
            {
                clientThreads.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    protected synchronized void remove(int id){
        for(int i = 0; i < clientThreads.size(); i++)
        {
            ClientThread ct = clientThreads.get(i);
            if(ct.id == id)
            {
                clientThreads.remove(i);
                return;
            }
        }

    }

    public static void main(String [] args){
        int portNumber = 1500;
        switch(args.length)
        {
            case 1: portNumber = Integer.parseInt(args[0]);break;
            case 0: break;
            default: break;
        }
        Server server = new Server(portNumber);
        server.start();
    }


    class ClientThread extends Thread{
        Socket socket;
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;
        int id;
        String username;
        ChatMessage chatMessage;
        String date;

        public ClientThread(Socket socket){
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");
            try{
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                username = (String)inputStream.readObject();
                display(username +" just connected");
            }
            catch(IOException e){
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            catch(ClassNotFoundException ignore){}
            date = new Date().toString()+"\n";
        }

        @Override
        public void run(){
            boolean keepGoing = true;
            while(keepGoing)
            {
                try{
                    chatMessage = (ChatMessage)inputStream.readObject();
                }
                catch(IOException exception){
                    display(username + " Exception reading Streams: " + exception);
                    break;
                }
                catch(ClassNotFoundException exception){
                    break;
                }
                String message = chatMessage.getMessage();
                switch(chatMessage.getType())
                {
                    case ChatMessage.MESSAGE: broadcast(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT: broadcast(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN: writeMessage("List of users connected at "+simpleDateFormat.format(new Date()) + "\n");
                        for(int i = 0; i < clientThreads.size(); ++i)
                        {
                           ClientThread ct = clientThreads.get(i);
                           writeMessage((i+1) + ") " + ct.username + " since " + ct.date);
                        }
                        break;
                }
            }
            remove(id);
            close();
        }

        private boolean writeMessage(String message){
           if(!socket.isConnected())
           {
               close();
               return false;
           }
           try{
               outputStream.writeObject(message);
           }
           catch(IOException exception){
               display("Error sending message to " + username);
               display(exception.toString());
           }
           return true;
        }

        private void close() {
           try{
               if(outputStream != null)
               {
                   outputStream.close();
               }
           }
           catch(IOException ignore){}
           try{
               if(inputStream != null)
               {
                   inputStream.close();
               }
           }
           catch(IOException ignore){}
           try{
               if(socket != null)
               {
                   socket.close();
               }
           }
           catch(IOException ignore){}
        }
    }
}
