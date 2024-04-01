Instructions on how to compile and run your project. 
A list of who submitted which parts of the assignment on Brightspace and Vocareum. 
For example: Student 1 - Submitted Report on Brightspace. Student 2 - Submitted Vocareum workspace.
A detailed description of each class. This should include the functionality included in the class, the testing done to verify it works properly, and its relationship to other classes in the project. 
You can format the README however you like, provided it is written as markdown file (.md). 

DELETE ABOVE BEFORE FINALIZATION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  
  
How to run & compile project:
- Open the file tab
- Click Project Structure
- Scroll down and click Libraries
- Add a project library from Maven
- Search for JUnit 4.10.jar and extended jar
- Install both
- Once Installed, go to RunLocalTest.java
- Run the test

Note: RunLocalTest.java includes 4 tests: 
- public void testUserReadWrite() {
        // Test writing a user to disk and reading it back
- public void testMessageToString() { 
        //Test the string's validity/match
- public void testMessageHistoryAddAndRemove() {
        // Test adding and removing messages from the history
- public void testUserFriendsManagement() {
        // Test adding and removing friends
  
  
Student Submitted Report:  
Student Submitted Vocareum Workspace:  



User Class -

The User Class creates and manages information regarding each User. This includes...
  -Username  
  -Password  
  -Age  
  -Friends (as a list)  
  -Blocked Users (as a list)  
  -friendsOnly (Condition if the user wants to see messages from only friends, or all users)  
  -Profile_Picture_File (File containing the user's profile picture {if added})  

The User Class contains methods to...  
  -Create a new user (Constructor)  
  -Get all information covered above (Method: Getter)  
  -Set all information to new values (Method: Setter)  
  -A way to display each user as a String (toString)  
  -AddingImages to each user given input of a file (loadImage)  

The User class servers as the basis for everything within the project. It contains each individual user, and all related data.   
This allows other classes, such as MessageHistory, to keep track of each user's messages to one another.     
  -For instance, MessageHistory uses each 'User' to keep track of their messages to one another.  
  -MessageHistory also keeps track of all messages, with it containing each user's messages to one another.    
The User class is also used to read and write each user.  

 The User class is tested for functionality through RunLocalTest, within setUp() and testUserReadWrite(). Any failures in User are presented through RunLocalTest  

UserInterface contains the methods used in User, and was how our group communicated which methods we needed to implement within User.  


 
<hr>
Message History Class - <br> <br>
This class creates and manages the complete message history between two users. It contains information including... <br>
  -all messages (as a list of messages(class)) <br>
  -both users involved in the chat <br><br>
The Message History class contains methods to... <br>
  -Create a new message history (constructor) <br>
  -Add new messages to the history <br>
  -remove a specific message from the history (given the sender and date) <br>
  -Get all information covered above (Method: Getter) <br>
  -Get a formatted string representation of the history <br> <br>
This message history class with serve a main role in messaging system for the project. It will contain all of the text data between two users (existing to represent a single chat for 2 users) and will provide a strong basis for the chat GUI and functionality.
  -This class is most closely related to both the message and message info classes. This history class contains will be utilized to access the specific messages sent between users.  
  
MessageHistory was tested through RunLocalTest, in "testMessageHistoryAddAndRemove()". This tests that a message is able to be added to the list of messages, and can be removed. This allows us to test the core functionality of MessageHistory, adding and removing messages, and ensuring we are removing the correct message, checking through the users included, and the date the message was sent. 
  
 
  MessageHistoryInterface allowed our group to communicate which methods we needed to implement within MessageHistory  
  
<hr>
Message Class - <br><br>
This class creates and manages a single text message meant for use in a chat between two users. It contains the following info... <br>
  -message<br>
  -sender(sent to superclass)<br>
  -receiver(sent to superclass)<br>
  -date of message(sent to superclass)<br>
The message class also contains methods to...<br>
  -Get and set message (Getter/Setter Method)<br>
  -Print the message (in a chat legible string format)<br> <br>
This message class is simple yet provides crucial functionality to the entire messaging system. Each class created represents a single message that stores all of the necessary information.
  -The class extends the more generic messageInfo parent class providing text functionality. It also is used extensively in the messageHistoy class which stores messages in an array. 

Message was tested through RunLocalTest, within "testMessageToString()". This allowed us to test that messages being created were properly formatted, containing the correct Sender/Receiver, Message (String), and date of the message.   
  
MessageInterface allowed our group to plan out which methods we wanted to include within the Message Class  

<hr>
  
MessageInfo Class - <br><br>
This class contains the following information for each message  
  -The Sender (a user)
  -Receiver (a user)
  -The Date the message was sent  
The messageInfo class contains:  
  -a constructor for creating a new message, containing the sender & receiver
  -a constructor for creating a new message, containing the sender, receiver, & date  
  -Methods for obtaining sender, receiver, & date (Method: Getter)

MessageInfo was tested through the MessageClass, as Message extends MessageInfo. Thus, Message was tested through RunLocalTest, within "testMessageToString()". This allowed us to test that messages being created were properly formatted, containing the correct Sender/Reciever, Message (String), and date of the message. 

The MessageInfo class implements MessageInfoInterface, which contains methods used in MessageInfo. We used MessageInfoInterface to communicate with our team on what methods and functions were required for MessageInfo.  

MessageInfo is extended in the Message class.

<hr>

DatabaseHelper Class - <br><br>
This class handles the reading and writing of the database users, images, and messages to file. It uses synchronized objects (gatekeeper fields) in order to make all of its methods thread-safe. <br>
  -USERS_DIRECTORY (final String that holds the location where users are saved)<br>
  -IMAGES_DIRECTORY (final String that holds the location where images are saved)<br>
  -MESSAGES_DIRECTORY (final String that holds the location where messages are saved)<br>
  -userGatekeeper (synchronized object to block execution when dealing with reading/writing users at the same time)<br>
  -imageGatekeeper (synchronized object to block execution when dealing with reading/writing images at the same time)<br>
  -messageGatekeeper (synchronized object to block execution when dealing with reading/writing messages at the same time)<br>
The DataBaseHelper class also contains methods that..<br>
  -Read/Write Users classes from/to a file<br>
   -Read/Write BufferedImage classes from/to a file<br>
  -Read/Write MessageHistory classes from/to a file<br> <br>
This class is the backbone of the database because it is what actually retrieves and classes from a file and writes classes to a file. It needs to be thread safe because if there are multiple users, they could potentially be trying to both access a file at the same time and we do not want that because it could cause unexpected behavior.
  -There can be multiple instances of DatabaseHelper used in multiple threads. This allows for greater flexibility and lives up to its name as more of a helper class.

DatabaseHelper was tested through RunLocalTest, within "___". This allowed us to test that User, BufferedImage, and MessageHistory classes could be successfully saved and read from their individual files correctly.
  
DatabaseHelperInterface allowed our group to plan out which methods we wanted to include within the DatabaseHelper class.
