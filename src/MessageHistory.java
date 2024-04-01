
import java.util.ArrayList;
import java.util.Date;

public class MessageHistory implements MessageHistoryInterface {
    private ArrayList<Message> allMessages;
    private String user1;
    private String user2;

    public MessageHistory(String user1, String user2) {
        this.allMessages = new ArrayList<Message>();
        this.user1 = user1;
        this.user2 = user2;
    }

    public void addMessage(String sender, String receiver, String message) {
        try {
            if ((sender.equals(user1) && receiver.equals(user2)) || (sender.equals(user2) && receiver.equals(user1))) {
                allMessages.add(new Message(sender, receiver, message));
            }
            else{
                throw new Exception("Sender/Receiver name typo");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeMessage(String sender, Date date) {
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
    public String[] getMessages() {
        String[] messages = new String[allMessages.size()];
        for (int x = 0; x < allMessages.size(); x++) {
            messages[x] = allMessages.get(x).toString();
        }
        return messages;
    }
}
