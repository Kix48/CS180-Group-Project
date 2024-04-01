import java.text.SimpleDateFormat;
import java.util.Date;
public class MessageInfo implements MessageInfoInterface {
    private String sender;
    private String receiver;
    private Date date;

    public MessageInfo(String sender, String receiver){ //general constructor to create a new message (text or picture)
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date();
    }

    public MessageInfo(String sender, String receiver, Date date){ //general constructor to create a new message with a known date(text or picture)
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public String getSender() { //returns the sender of the message
        return sender;
    }

    public String getReceiver() { //returns the receiver of the message
        return receiver;
    }

    public Date getDate() { //returns the date of the mmessage
        return date;
    }
}

