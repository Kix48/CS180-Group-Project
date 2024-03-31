public class Message extends MessageInfo{
    String message;
    public Message(String sender, String receiver, String message) {
        super(sender, receiver);
        this.message = message;

    }
    public String toString(){
        return "[" + super.getDate().toString() + "] " + super.getSender() + ": " + message;
    }
}
