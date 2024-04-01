import java.text.SimpleDateFormat;
import java.util.Date;

public class Message extends MessageInfo implements MessageInterface {
    String message;
    public Message(String sender, String receiver, String message) { //utilizes super class and creates a new message with the current time
        super(sender, receiver);
        this.message = message;
    }

    public Message(String sender, String receiver, String message, Date date) {  //utilizes super class and creates a new message with a custom time
        super(sender, receiver, date);
        this.message = message;
    }

    public String getMessage() { //retrieve message text
        return this.message;
    }

    public void setMessage(String message) { //edit and change the message text
        this.message = message;
    }

    public String toString(){ //print the message in a chat legible format
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
        return "[" + dateFormatter.format(super.getDate()) + "] " +
                super.getSender() + "-" + this.getReceiver() + ": " + message;
    }
}
