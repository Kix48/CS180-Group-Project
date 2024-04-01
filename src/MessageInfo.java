import java.text.SimpleDateFormat;
import java.util.Date;
public class MessageInfo implements MessageInfoInterface {
    private String sender;
    private String receiver;
    private Date date;
    
    //general constructor to create a new message (text or picture)
    public MessageInfo(String sender, String receiver){ 
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date();
    }
    
    //general constructor to create a new message with a known date(text or picture)
    public MessageInfo(String sender, String receiver, Date date){ 
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }
    //returns the sender of the message
    public String getSender() { 
        return sender;
    }

    //returns the receiver of the message
    public String getReceiver() { 
        return receiver;
    }

    //returns the date of the message
    public Date getDate() { 
        return date;
    }
}

