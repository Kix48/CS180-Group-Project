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
  -Get all informaiton covered above (Method: Getter)  
  -Set all information to new values (Method: Setter)  
  -A way to display each user as a String (toString)  
  -AddingImages to each user given input of a file (loadImage)  

The User class servers as the basis for everything within the project. It contains each individual user, and all related data.   
This allows other classes, such as MessageHistory, to keep track of each user's messages to one another.     
  -For instance, MessageHistory uses each 'User' to keep track of their messages to one another.  
  -MessageHistory also keeps track of all messages, with it containing each user's messages to one another.    
The User class is also used to read and write each user.  

 The User class is tested for functionality through RunLocalTest, within setUp() and testUserReadWrite(). Any failures in User are presented through RunLocalTest  
 




