import java.util.Date;
public class MessageInfo {
private String sender;
private String receiver;
private Date date;

public MessageInfo(String sender,String receiver){
    this.sender = sender;
    this.receiver = receiver;
    this.date = new Date();
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

