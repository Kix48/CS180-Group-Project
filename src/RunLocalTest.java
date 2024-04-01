import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class RunLocalTest {
    private DatabaseHelper databaseHelper;
    private User user;
    private Message message;
    private MessageHistory messageHistory;

    @Before
    public void setUp() {
        // Initialize your classes here
        databaseHelper = new DatabaseHelper();
        user = new User("testUser", "password123", 25);
        message = new Message("testUser", "receiverUser", "Hello, JUnit!");
        messageHistory = new MessageHistory("testUser", "receiverUser");
    }

    @Test
    public void testUserReadWrite() {
        // Test writing a user to disk and reading it back
        assertTrue("Writing user failed", databaseHelper.writeUser(user));
        User readUser = databaseHelper.readUser("testUser");
        assertNotNull("Reading user failed", readUser);
        assertEquals("Usernames do not match", "testUser", readUser.getUsername());
    }

    @Test
    public void testMessageToString() {
        String messageString = message.toString();
        assertTrue("Message toString() does not contain sender",
                messageString.contains(message.getSender()));
        assertTrue("Message toString() does not contain message text",
                messageString.contains("Hello, JUnit!"));
    }


    @Test
    public void testMessageHistory() {
        // Test adding and removing messages from the history
        try {
            messageHistory.addMessage("testUser", "Test message");
        } catch (Exception e) {
            fail("Adding message failed");
        }
        assertEquals("Message history size incorrect", 1, messageHistory.getMessages().length);

        assertEquals("Failed to write message history",
                true, databaseHelper.writeMessageHistory(messageHistory));

        MessageHistory readHistory = databaseHelper.readMessageHistory("testUser", "receiverUser");
        assertNotEquals("Could not read the message history",
                null, readHistory);

        messageHistory.removeMessage("testUser", new java.util.Date());
        assertEquals("Message was not removed", 0, messageHistory.getMessages().length);
    }

    @Test
    public void testBufferedImage() {
        BufferedImage image = databaseHelper.readImage("testUser - PFP.png");

        assertNotEquals("Failed to read image file", null, image);

        assertEquals("Failed to write image file",
                true, databaseHelper.writeImage(image, "testUser - PFP.png"));
    }

    @Test
    public void testUserFriendsManagement() {
        // Test adding and removing friends
        user.addFriends("friendUser");
        assertTrue("Friend was not added", user.getFriends().contains("friendUser"));
        user.removeFriends("friendUser");
        assertFalse("Friend was not removed", user.getFriends().contains("friendUser"));
    }

}
