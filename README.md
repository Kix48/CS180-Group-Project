Phase 2 ReadMe
<hr>
Student Submitted Report: NEED TO PUT WHO HERE

Student Submitted Vocareum Workspace: NEED TO PUT WHO HERE

IMPORTANT FOR RUNNING RUNLOCALTEST
-For testBufferedImageReadWrite, need to add image before testing to filepath "images/testUser - PFP.png"

How to run & compile project:
Open the file tab
Click Project Structure
Scroll down and click Libraries
Add a project library from Maven
Search for JUnit 4.10.jar and extended jar
Install both
Once Installed, go to RunLocalTest.java
Run the test
Note: RunLocalTest.java includes PUT NEW AMOUNT HERE tests:

public void testUserReadWrite() { // Test writing a user to disk and reading it back <br>
public void testMessageToString() { //Test the string's validity/match <br>
public void testMessageHistoryAddAndRemove() { // Test adding and removing messages from the history <br>
public void testUserFriendsManagement() { // Test adding and removing friends <br>
public void testBufferedImageReadWrite() { // Test that database can read/write files <br>
public void testClient() { // Test for initializing a client <br>
public void testRegisterInvalidUsername() { //Test for registering users (username) <br>
public void testRegisterInvalidAge() { //Test registering users (age) <br>
public void testRegisterInvalidProfilePicture() { //Test registering users (profile pic) <br>
public void testFindUserNotFound() { //Test for finding users <br>
public void testLoginInvalidCredentials() { //Test for login  <br>

<hr>
Client Class

The client class handles outgoing requests to communicate with the server. Allows the user to authenticate in to an account and request functions such as...

initialize() - initialize will make a new socket, using HOSTNAME: "localhost", and PORT: "4444". It will initialize a reader using the socket's input stream, and a writer from the sockets output stream. It will catch any exceptions that arise, and return any errors. <br>

shutdown() - shutdown will close the socket, reader, and writer. It will return any errors if presented. <br>

register() - register will attempt to add a new user to the database. After making sure the username and password are valid and the age is older than 0, an image is read for the profile picture. ALl of this data is then sent to the server. For the profile picture, it is Base64 encoded and sent in chunks because the data cannot all be sent in a single packet. If the server returns an error for any reason, the error message is displayed,  <br>

login() - login will take in an input of a username and password. After verifying the strings are valid, the strings are trimmed to remove whitespace. The client will then print "LOGIN" to the server, as well as the username and password. The client will then read from the server. This will either be "SUCCESS", where **login** will print a welcome message. Any other output will be an error, with a message describing it. <br>

findUser() - findUser takes in an input of a user. It will ensure the username is valid, then send "FIND_USER" to the Server, along with the username. The client will read the result from the server. If the output is not "SUCCESS", it will print the error message. If it is "SUCCESS", the client will then decode the output, then return it with a verification message. If any errors occur, it ill print the stack trace.  <br>

addFriend() - addFriend takes in an input of a 2nd username of a friend. Once validating that the username is a valid name, it is trimmed. The method then will write "ADD_FRIEND" to the server, along with the clientUsername, and the friend username. The client will then read an output from Server.java. If the output is "SUCCESS", a message will be displayed stating it was a success. If not, an error will be displayed, with details regarding it. <br>

blockUser() - blockUser takes in an input of a 2nd username of a user needing to be blocked. Once validating that the username is a valid name, it is trimmed. The method will then write "BLOCK" to the server, along with the name of the clientUser, and the user to be blocked. The client will read an output from Server.java. If the output is "SUCCESS", a message will be displayed stating it was a success. If not, an error will be displayed, with details regarding it. <br>

sendMessage() - sendMessage will send the server "SEND_MESSAGE", the clientUsername, the receiver of the message, and the message itself. It will then read an output from the server. If "SUCCESS", it will return a confirmation that it has completed. It may also read "ERROR", as well as a message describing the issue. <br>

removeMessage() - removeMessage takes in an input of a reciever, and the message index. After verifying the inputs are valid, the client will write "REMOVE_MESSAGE" to the server, along with the clientUsername, reciever, and message index. The client will then wait for an output from the server. If it is "SUCCESS", the method will display a completion message. If it recieves "ERROR", it will recieve an error message and display it. <br>


getMessageHistory() - getMessageHistory takes in parameters of a username. After checking its validity, the username is trimmed of whitespace, then the client sends "MESSAGE_HISTORY", the client's username, and the second username. The client will then wait for a response from the server. If it is success, it will display a completion message. If "ERROR" it will display information regarding it.  <br>

setFriendsOnly() - setFriendsOnly takes in an input of a boolean, indicating the condition of if a user wants to receive all messages, or messages only from friends. The client will send the server "FRIENDS_ONLY", the username of the client, and the boolean of the condition inputted. It will then receive a String from the server, if it says "SUCCESS", it will say it is completed. If not, the program will output the error presented, with information regarding it.  <br>

Client.java also implements ClientInterface - An interface we used to communicate ideas and changes to the client class. <br>

<hr>

Server Class <br>

The server class manages incoming requests from multiple clients (from Client.java). Contains methods that process authentication, user interaction, and database retrieval. This includes functions such as...

registerUser() - registerUser is called when the server receives a "REGISTER" request from the client. The server first checks if a user with the given username already exists. If not, it continues to read in the password and age. In regard to the profile picture, the Base64 encoded data is read in from chunks and then Base64 decoded into its byte array and turned into a BufferedImage. Finally, a call to DatabaseHelper saves the new user data to a file and success is sent back to the client. <br>

authenticate() - authenticate will be called when the server reads "LOGIN" from the client. authenticate will then check that the username of the user logging in is valid. It will then check that the password inputted matches the password of the client. If the password is correct, the server will send the client "SUCCESS". If not, it will send "ERROR", with a message describing it. <br>

addFriend() - addFriend will be called when the server is reading from the client. Upon seeing "ADD_FRIEND", the addFriend method within Server.java will be called. addFriend Will read the two usernames sent from the client, then verify they are actual users. It will then attempt to add the friend to the user, and upon success, will send "SUCCESS" to the client. If failure or any other error is presented, an error message will be sent back to the client. <br>

blockUser() - blockUser will be called when the server is reading from the client. Upon seeing "BLOCK", the blockUser method within Server.java will be called. blockUser will read the two usernames sent from client, then verify they are actual users. It will attempt to add the 2nd user to the first ones blocked list. If successful, the server will send "SUCCESS" to the client. If any errors or failure appears, it will send "ERROR, along with any error information back to the client.  <br>

getMessageHistory() - getMessageHistory is called when the server reads "MESSAGE_HISTORY" from the client. It will then verify the users involved are valid. The message history will then be read. If there is history, the server will write the history to the client, along with "SUCCESS". If there is no message history, an error message is sent to the client describing the issue. If there is any other errors, the server will send "ERROR", along with the error presented.  <br>

sendMessage() - sendMessage will be called when the server recieves "SEND_MESSAGE" from the client. It will verify the sender and reciever as valid users, and ensure there are no conflicts with friending/blocking. It will attempt to create the message, and add it to the history. If any errors arise, sendMessage will send "ERROR" along with an error message to the client. If none arise, it will send "SUCCESS". <br>

removeMessage() - removeMessage will be called when the server recieves "REMOVE_MESSAGE" from the client. It will verify the sender and reciever of the message are valid, and verify the message. removeMessage will then attempt to remove the message. On success, it will write "SUCCESS" to the client. If any issues arise, it will write "ERROR" along with an error message.<br>

changeVisibility() - changeVisibility will be called when the server reads "FRIENDS_ONLY" from the client. The server will then validate that user is a real one, and then will change the condition of friendsOnly for seeing types of users. If it is a success, the server will send "SUCCESS" to the client, and if an error arises, it will send "ERROR" and extra information to the client.  <br>

findUser() - findUser will be called when the server reads "FIND_USER" from the client. findUser will validate that every part of the user (username, password, age, profile picture), and return an error if any arise. If it succeeds, it will write "SUCCESS", the username, and age of the user. To send the profile picture, it will be sent as a byte[]. If any errors occur, it will return those.<br>

run() - run keeps track of requests sent from the client, to the server. Run initializes a databaseHelper, and creates new writers and readers. Server will then wait for inputs from the client, such as "REGISTER" or "ADD_FRIEND". It will stay within this loop of managing requests. Once the thread has ended, the reader and writer will be closed. <br>

Server.java also implements ServerInterface. This allowed us to create and include changes to the Server class to be communicated to each other. 
Server.java also implements **Runnable**

<hr>

<hr>
Phase 1 ReadMe Below
<hr>

Student Submitted Report:  Steven Krauter  

Student Submitted Vocareum Workspace:  Steven Krauter  

**IMPORTANT FOR RUNNING RUNLOCALTEST** <br>
        -For testBufferedImageReadWrite, need to add image before testing to filepath "images/testUser - PFP.png"

- How to run & compile project:
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
- public void testBufferedImageReadWrite() {
       // Test that database can read/write files
  




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
  -The class extends the more generic messageInfo parent class providing text functionality. It also is used extensively in the messageHistory class which stores messages in an array. 

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

MessageInfo was tested through the MessageClass, as Message extends MessageInfo. Thus, Message was tested through RunLocalTest, within "testMessageToString()". This allowed us to test that messages being created were properly formatted, containing the correct Sender/Receiver, Message (String), and date of the message. 

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
The DataBaseHelper class also contains methods that...<br>
  -Read/Write Users classes from/to a file<br>
   -Read/Write BufferedImage classes from/to a file<br>
  -Read/Write MessageHistory classes from/to a file<br> <br>
This class is the backbone of the database because it is what actually retrieves and classes from a file and writes classes to a file. It needs to be thread safe because if there are multiple users, they could potentially be trying to both access a file at the same time and we do not want that because it could cause unexpected behavior.
  -There can be multiple instances of DatabaseHelper used in multiple threads. This allows for greater flexibility and lives up to its name as more of a helper class.

DatabaseHelper was tested through RunLocalTest.java with the methods testUserReadWrite, testMessageHistory, and testBufferedImageReadWrite. This allowed us to test that User, BufferedImage, and MessageHistory classes could be successfully saved and read from their individual files correctly.
  
DatabaseHelperInterface allowed our group to plan out which methods we wanted to include within the DatabaseHelper class.


<hr>


Interfaces to be expanded upon in Phase 2 - <br><br>

ClientInterface - Serves as an outline for all functions that will be completed by the client, also serves as a baseline for phase 2.<br>
-Registers and logs in users based off the provided username and password<br>
-The ability to add friends and blocks users<br>
-The client can send and remove messages that they have sent previously<br>
-The client can change whether they are open to direct messaging from all users or just friends<br>

ServerInterface - Can be used as the outline for all functions that will be completed by the server. These functions will be called once the client sends the request.  
-Creates, modifies, and deletes users.  
-Handles the login process for clients.  
-Handles basic platform functionality, i.e. sending messages.  
-Interacts with database to retrieve message history and find users.
