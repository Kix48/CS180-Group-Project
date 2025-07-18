
import java.util.ArrayList;
import java.util.Date;
/**
 * MessageHistory.java Class
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
public class MessageHistory implements MessageHistoryInterface {
    private ArrayList<Message> allMessages;
    private String user1;
    private String user2;

    //creates new message history class between two users 
    public MessageHistory(String user1, String user2) { 
        this.user1 = user1;
        this.user2 = user2;
        this.allMessages = new ArrayList<Message>();
    }
    
    //creates message history class between two users (imports messages)
    public MessageHistory(String user1, String user2, ArrayList<Message> messages) { 
        this.user1 = user1;
        this.user2 = user2;
        this.allMessages = messages;
    }

    //returns user1
    public String getUser1() { 
        return this.user1;
    }

    //returns user2
    public String getUser2() {
        return this.user2;
    }

    //adds a message to the message history while verifying the sender/receiver
    public boolean addMessage(String sender, String message) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //checks message history and removes the first message (whose sender and date match the values inputted)
    public boolean removeMessage(String sender, int index) {
        try {
            if (index >= allMessages.size()) {
                throw new IndexOutOfBoundsException("Message index out of bounds");
            }

            Message message = allMessages.get(index);
            if (!message.getSender().equals(sender)) {
                throw new Exception("Cannot remove message that was not sent by the client");
            }

            allMessages.remove(index);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //returns an array representing the complete message history (in text legible format)
    public String[] getMessages() { 
        String[] messages = new String[allMessages.size()];
        for (int x = 0; x < allMessages.size(); x++) {
            messages[x] = allMessages.get(x).toString();
        }
        return messages;
    }

    //returns a formatted string representing the message history
    public String toString() { 
        String output = String.format("User1: %s\nUser2: %s\nCount: %d\n",
                this.user1, this.user2, this.allMessages.size());

        for (Message message : this.allMessages) {
            output += message.toString() + "\n";
        }

        return output;
    }
}
