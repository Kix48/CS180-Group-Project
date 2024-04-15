import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Message.java Class
 *
 * Purdue University CS18000 Spring 2024
 *
 * @author Giancarlo Guccione
 * @author Steven Krauter
 * @author Justin Lin
 * @author Wael Harith
 * @author Chase Gamble
 * @version 1.0 April 2024
 */
public class Message extends MessageInfo implements MessageInterface {
    String message;

    //utilizes super class and creates a new message with the current time
    public Message(String sender, String receiver, String message) { 
        super(sender, receiver);
        this.message = message;
    }

    //utilizes super class and creates a new message with a custom time
    public Message(String sender, String receiver, String message, Date date) {  
        super(sender, receiver, date);
        this.message = message;
    }

    //retrieve message text
    public String getMessage() { 
        return this.message;
    }

    //edit and change the message text
    public void setMessage(String message) { 
        this.message = message;
    }

    //print the message in a chat legible format
    public String toString() { 
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
        return "[" + dateFormatter.format(super.getDate()) + "] " +
                super.getSender() + "-" + this.getReceiver() + ": " + message;
    }
}
