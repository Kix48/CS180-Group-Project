import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * RunLocalTest.java Class
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
public class RunLocalTest {
    private DatabaseHelper databaseHelper;
    private User user;
    private Message message;
    private MessageHistory messageHistory;

    @Before
    public void setUp() {
        // Initialize your classes here
        databaseHelper = new DatabaseHelper();
        user = new User("testUser", "password123", 25, "testUser - PFP.png");
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

        messageHistory.removeMessage("testUser", 0);
        assertEquals("Message was not removed", 0, messageHistory.getMessages().length);
    }

    @Test
    public void testBufferedImageReadWrite() {
        BufferedImage image = databaseHelper.readImage(user.getUserPFPFile());

        assertNotEquals("Failed to read image file", null, image);

        assertEquals("Failed to write image file",
                true, databaseHelper.writeImage(image, user.getUserPFPFile()));
    }

    @Test
    public void testUserFriendsManagement() {
        // Test adding and removing friends
        user.addFriends("friendUser");
        assertTrue("Friend was not added", user.getFriends().contains("friendUser"));
        user.removeFriends("friendUser");
        assertFalse("Friend was not removed", user.getFriends().contains("friendUser"));
    }

    @Test
    public void testClient() {
        Client client = new Client();

        assertTrue(client.initialize());

/*        assertTrue("Failed to register a user!",
                client.register("ChaseGamble48", "Password123!", 19,
                new File("images/testUser - PFP.png")));*/

        assertFalse("We should not be able to login with these credentials!",
                client.login("ChaseGamble48", "WrongPassword"));

        assertFalse("We should not be able to login with these credentials!",
                client.login("Unknown_99", "Password123!"));

        assertTrue("We should be able to login with these credentials!",
                client.login("ChaseGamble48", "Password123!"));

        assertNotEquals("Failed to find user!", client.findUser("ChaseGamble48"), null);

/*        MessageHistory messageHistory = client.getMessageHistory("testUser");
        for (String message : messageHistory.getMessages()) {
            System.out.println(message);
        }*/

        //client.sendMessage("testUser", "Hello");

        client.removeMessage("testUser", 0);

        client.shutdown();
    }
    @Test
    public void testRegisterInvalidUsername() {
        Client client = new Client();
        client.initialize();
        assertFalse("Username validation failed", client.register("", "ValidPass123!", 21, new File("validPFP.png")));
        assertFalse("Username validation failed", client.register("TooLongUsernameIsInvalidBecauseItIsTooLong", "ValidPass123!", 21, new File("validPFP.png")));
        assertFalse("Username validation failed", client.register("Invalid\nUsername", "ValidPass123!", 21, new File("validPFP.png")));
    }

    @Test
    public void testRegisterInvalidAge() {
        Client client = new Client();
        client.initialize();
        assertFalse("Age validation failed", client.register("ValidUser", "ValidPass123!", -1, new File("validPFP.png")));
        assertFalse("Age validation failed", client.register("ValidUser", "ValidPass123!", 0, new File("validPFP.png")));
    }

    @Test
    public void testRegisterInvalidProfilePicture() {
        Client client = new Client();
        client.initialize();
        assertFalse("Profile picture validation failed", client.register("ValidUser", "ValidPass123!", 21, new File("nonexistent.png")));
    }

    @Test
    public void testFindUserNotFound() {
        Client client = new Client();
        client.initialize();
        assertNull("Find user should return null for non-existent user", client.findUser("NonExistentUser"));
    }

    @Test
    public void testLoginInvalidCredentials() {
        Client client = new Client();
        client.initialize();
        assertFalse("Login should fail for wrong credentials", client.login("ValidUser", "WrongPassword"));
    }
}