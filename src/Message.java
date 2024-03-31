public class Message extends MessageInfo implements MessageInterface {
    String message;
    public Message(String sender, String receiver, String message) {
        super(sender, receiver);
        this.message = message;

    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){
        return "[" + super.getDate().toString() + "] " + super.getSender() + ": " + message;
    }
}
