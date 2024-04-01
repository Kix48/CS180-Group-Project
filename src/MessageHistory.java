
import java.util.ArrayList;
import java.util.Date;

public class MessageHistory implements MessageHistoryInterface {
    private ArrayList<Message> allMessages;
    private String user1;
    private String user2;

    public MessageHistory(String user1, String user2) { //creates new message history class between two users
        this.user1 = user1;
        this.user2 = user2;
        this.allMessages = new ArrayList<Message>();
    }

    public MessageHistory(String user1, String user2, ArrayList<Message> messages) { //creates message history class between two users (imports messages)
        this.user1 = user1;
        this.user2 = user2;
        this.allMessages = messages;
    }

    public String getUser1() { //returns user1
        return this.user1;
    }

    public String getUser2() {//returns user2
        return this.user2;
    }

    public void addMessage(String sender, String message) { //adds a message to the message history while verifying the sender/reciever
        try {
            String receiver;
            if (sender.equals(this.user1)) {
                receiver = this.user2;
            } else if (sender.equals(this.user2)) {
                receiver = this.user1;
            } else {
                throw new Exception("Sender/Receiver name typo");
            }

            allMessages.add(new Message(sender, receiver, message));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeMessage(String sender, Date date) { //checks message history and removes the first message (whose sender and date match the values inputted)
        try {
            boolean removed = false;
            for (int x = 0; x < allMessages.size(); x++) {
                Message message = allMessages.get(x);

                String d1 = message.getDate().toString();
                String d2 = date.toString();

                if (message.getSender().equals(sender) && d1.equals(d2)) {
                    allMessages.remove(x);
                    removed = true;
                    break;
                }
            }

            if (!removed){
                throw new Exception("Failed to remove message! Check sender/date for typos");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getMessages() { //returns an array representing the complete message history (in text legible format)
        String[] messages = new String[allMessages.size()];
        for (int x = 0; x < allMessages.size(); x++) {
            messages[x] = allMessages.get(x).toString();
        }
        return messages;
    }

    public String toString() { //returns a formatted string representing the message history
        String output = String.format("User1: %s\nUser2: %s\n", this.user1, this.user2);

        for (Message message : this.allMessages) {
            output += message.toString() + "\n";
        }

        return output;
    }
}
