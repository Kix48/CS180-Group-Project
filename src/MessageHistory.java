
import java.util.ArrayList;
import java.util.Date;

public class MessageHistory {
    private ArrayList<Message> allMessages;
    private String user1;
    private String user2;

    public MessageHistory(String user1,String user2) {
        this.allMessages = new ArrayList<Message>();
        this.user1 = user1;
        this.user2 = user2;
    }

    public void addMessage(String sender, String receiver, String message) throws Exception {
        try {
            if((sender.equals(user1)&&receiver.equals(user2))||(sender.equals(user2)&&receiver.equals(user1))){
                allMessages.add(new Message(sender, receiver, message));
            }
            else{
                System.out.println("Sender/Reciever name typo");
                throw new Exception();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeMessage(String sender, Date date) {
        boolean removed = false;
        for (int x = 0; x < allMessages.size(); x++) {
            Message message = allMessages.get(x);
            if (message.getSender().equals(sender) && message.getDate().equals(date)) {
                allMessages.remove(x);
                removed = true;
                break;
            }
        }
        if (!removed){
            System.out.println("Failed to remove message! Check sender/date for typos");
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
