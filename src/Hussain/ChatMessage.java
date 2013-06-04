package Hussain;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Hussain
 * Date: 03/06/2013
 * Time: 19:55
 * Project Name: JChat
 */
public class ChatMessage implements Serializable {
    protected static final long serialVersionUID = 1112122200L;

    protected static final int WHOISIN = 0;
    protected static final int MESSAGE = 1;
    protected static final int LOGOUT = 2;

    private int type;
    private String message;

    public ChatMessage(int type, String message){
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
