import java.util.Date;
public class MessageInfo implements MessageInfoInterface {
    private String sender;
    private String receiver;
    private Date date;

    public MessageInfo(String sender, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date();
    }

    public MessageInfo(String sender, String receiver, Date date){
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public Date getDate() {
        return date;
    }
}

