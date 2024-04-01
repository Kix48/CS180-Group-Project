Instructions on how to compile and run your project. 
A list of who submitted which parts of the assignment on Brightspace and Vocareum. 
For example: Student 1 - Submitted Report on Brightspace. Student 2 - Submitted Vocareum workspace.
A detailed description of each class. This should include the functionality included in the class, the testing done to verify it works properly, and its relationship to other classes in the project. 
You can format the README however you like, provided it is written as markdown file (.md). 

DELETE ABOVE BEFORE FINALIZATION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  
  
How to run & compile project:
  
  
  
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
<hr>
Message History Class - <br> <br>
This class creates and manages the complete message history between two users. It contains information including... <br>
  -all messages (as a list of messages(class)) <br>
  -both users involved in the chat <br><br>
The Message History class contains methods to... <br>
  -Create a new message history (constructor) <br>
  -Add new messages to the history <br>
  -remove a specific message from the history (given the sender and date) <br>
  -Get all information covered abover (Method: Getter) <br>
  -Get a formatted string representation of the history <br> <br>
This message history class with serve a main role in messaging system for the project. It will contain all of the text data between two users (existing to represent a single chat for 2 users) and will provide a strong basis for the chat GUI and functionality.
  -This class is most closely related to both the message and message info classes. This history class contains will be utilized to access the specific messages sent between users.
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
<hr>
  
   

 




