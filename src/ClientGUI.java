import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ClientGUI extends JComponent implements Runnable {
    private final String APP_NAME = "Chirp";
    private final int INITIAL_WIDTH = 600;
    private final int INITIAL_HEIGHT = 400;
    private Client client;
    private boolean isConnected;
    private User clientUser;
    private JFrame frame;
    private Font largeFont;
    private Font mediumFont;
    private Font smallFont;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField ageField;
    private JButton loginButton;
    private JButton registerPageButton;
    private JButton registerButton;
    private JButton friendsButton;
    private JButton returnButton;
    private JButton removeButton;
    private JButton blockButton;
    private JButton fileSelectButton;
    private File selectedFile;
    private JButton mainMenuButton;
    private JButton friendsOnlyButton;
    private JButton searchGo;
    private JButton seeConvoButton;
    private JButton logoutButton;
    private JButton sendButton;
    boolean switcher = true; //for friendsOnly button


    ActionListener buttonActionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) {
                if (login()) {
                    frame.setContentPane(mainPage());
                }
            } else if (e.getSource() == registerPageButton) {
                frame.setContentPane(registrationPage());

            } else if (e.getSource() == registerButton) {
                if (register()) {
                    frame.setContentPane(loginPage());
                }
            } else if (e.getSource() == fileSelectButton) {
                selectedFile = null;
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");

                fileChooser.setFileFilter(filter);

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();

                    if (selectedFile == null) {
                        showPopup("Invalid file selected", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (e.getSource() == friendsButton) {
                frame.setContentPane(friendsPage());
            } else if (e.getSource() == mainMenuButton) {
                //temp user for getting name
                clientUser = new User("Test User", "Password123!", 21, null);
                frame.setContentPane(mainPage());
            } else if (e.getSource() == returnButton) {
                frame.setContentPane(mainPage());
            }

            if (e.getSource() == friendsOnlyButton) {
                if (switcher) {
                    friendsOnlyButton.setText("Messaging Mode: Friends");
                    switcher = false;
                    //CHANGE BOOLEAN CONDITION HERE!!! (SET TO TRUE i think)


                } else {
                    switcher = true;
                    friendsOnlyButton.setText("Messaging Mode: All        ");
                    //CHANGE BOOLEAN CONDITION HERE!!! (set to false i think)


                }
            }

            // Needs to be called to change container content at runtime
            frame.getContentPane().revalidate();
        }
    };

    public ClientGUI() {
        client = new Client();

        if (client.initialize()) {
            isConnected = true;
            this.smallFont = new Font("TimesRoman", Font.PLAIN, 16);
            this.mediumFont = new Font("TimesRoman", Font.PLAIN, 28);
            this.largeFont = new Font("TimesRoman", Font.PLAIN, 40);
        } else {
            isConnected = false;
            showPopup("Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ClientGUI());
    }

    private void showPopup(String message, int type) {
        String title = "";
        switch (type) {
            case JOptionPane.ERROR_MESSAGE:
                title = "Error";
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                title = "Info";
                break;
            case JOptionPane.WARNING_MESSAGE:
                title = "Warning";
                break;
            case JOptionPane.QUESTION_MESSAGE:
                title = "Question";
                break;
            default:
                title = APP_NAME;
                type = JOptionPane.PLAIN_MESSAGE;
        }
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    private boolean login() {
        try {
            if (client.login(usernameField.getText(), passwordField.getText())) {
                clientUser = client.findUser(usernameField.getText());
                if (clientUser != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            showPopup(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        showPopup("Failed to login.", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean register() {
        int age = 0;
        try {
            age = Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            showPopup("Age field must have a number.", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (!client.register(usernameField.getText(), passwordField.getText(), age, selectedFile)) {
                showPopup("Failed to register user.", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            showPopup(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        showPopup("User successfully registered.", JOptionPane.INFORMATION_MESSAGE);

        return true;
    }

    Container loginPage() {
        Container content = new Container();
        content.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(largeFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 0;
        mainPanel.add(usernameLabel, constraint);

        usernameField = new JTextField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 0;
        mainPanel.add(usernameField, constraint);

        JLabel passwordLabel = new JLabel("Password:");
        constraint.anchor = GridBagConstraints.EAST;
        passwordLabel.setFont(mediumFont);
        constraint.gridx = 0;
        constraint.gridy = 1;
        mainPanel.add(passwordLabel, constraint);

        passwordField = new JTextField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 1;
        mainPanel.add(passwordField, constraint);

        loginButton = new JButton("Login");
        loginButton.setFont(mediumFont);
        loginButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(loginButton, constraint);

        registerPageButton = new JButton("Create Account");
        registerPageButton.setFont(mediumFont);
        registerPageButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(registerPageButton, constraint);


        //TEMP CODE TO ADD MAIN MENU FUNCTIONALITY (from login page)
        mainMenuButton = new JButton("Main menu");
        mainMenuButton.setFont(mediumFont);
        mainMenuButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(mainMenuButton, constraint);


        //END TEMP CODE


        content.add(mainPanel, BorderLayout.CENTER);

        return content;
    }

    Container friendsPage() {
        //temp test friends list
        User[] friends = new User[5];
        friends[0] = new User("Bobby", "123", 5, "otheruser.jpg");
        friends[1] = new User("Robby", "123", 5, "otheruser.jpg");
        friends[2] = new User("Billy", "123", 5, "otheruser.jpg");
        friends[3] = new User("Billy", "123", 5, "otheruser.jpg");
        friends[4] = new User("Biasdlly", "123", 5, "otheruser.jpg");
        Container content = new Container();
        content.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("FRIENDS LIST");
        titleLabel.setFont(largeFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        returnButton = new JButton("Back");
        returnButton.setFont(smallFont);
        returnButton.addActionListener(buttonActionListener);
        titlePanel.add(returnButton, BorderLayout.EAST);

        content.add(titlePanel, BorderLayout.NORTH);

        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));

        for (int x = 0; x < friends.length; x++) {
            User friend = friends[x];
            JPanel friendPanel = new JPanel(new BorderLayout());

            // User profile name
            JLabel friendName = new JLabel(friends[x].getUsername());
            friendName.setFont(mediumFont);
            friendPanel.add(friendName, BorderLayout.WEST);

            //add pfp
            if (friends[x].getUserPFPImage() != null) {
                JLabel profilePicture = new JLabel(new ImageIcon(
                        friends[x].getUserPFPImage().getScaledInstance(100, 100, Image.SCALE_FAST)));
                profilePicture.setHorizontalAlignment(JLabel.CENTER);
                profilePicture.setSize(20, 20);

                friendPanel.add(profilePicture, BorderLayout.WEST);
            }

            // Create a panel for user functionality with GridLayout
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

            //send message functionality

            JLabel sendMessageLabel = new JLabel("Send message:");
            sendMessageLabel.setFont(smallFont);
            JTextField searchTextField = new JTextField(25);
            sendButton = new JButton("Send");
            sendButton.setFont(smallFont);
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //ADDS MESSAGE TO MESSAGE HISTORY
                }
            });
            sendButton.setPreferredSize(new Dimension(80, 15));

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            panel.add(searchTextField, BorderLayout.WEST);
            panel.add(sendMessageLabel, BorderLayout.NORTH);
            panel.add(sendButton, BorderLayout.EAST);
            buttonPanel.add(panel);

            //block
            JPanel panel1 = new JPanel();
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
            blockButton = new JButton("Block");
            blockButton.setFont(smallFont);
            int finalX = x;
            blockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //blocks user friend.getUsername() from friends list
                    System.out.println("block " + friends[finalX].getUsername()); //test can remove later
                }
            });
            blockButton.setPreferredSize(new Dimension(125, 25));
            panel1.add(Box.createVerticalStrut(20));
            panel1.add(blockButton, BorderLayout.NORTH);
            panel1.add(Box.createVerticalStrut(10));

            //remove
            removeButton = new JButton("Remove");
            removeButton.setFont(smallFont);
            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //remove USER friend.getUsername() from friends list
                    System.out.println("remove " + friends[finalX].getUsername()); //test. can remove later
                }
            });
            removeButton.setPreferredSize(new Dimension(100, 25));
            panel1.add(removeButton, BorderLayout.NORTH);
            buttonPanel.add(panel1);

            friendPanel.add(buttonPanel, BorderLayout.EAST);

            friendListPanel.add(friendPanel);
        }
        content.add(new JScrollPane(friendListPanel), BorderLayout.CENTER);
        return content;
    }

    Container registrationPage() {
        Container content = new Container();
        content.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("REGISTRATION");
        titleLabel.setFont(largeFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 0;
        mainPanel.add(usernameLabel, constraint);

        usernameField = new JTextField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 0;
        mainPanel.add(usernameField, constraint);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 1;
        mainPanel.add(passwordLabel, constraint);

        passwordField = new JTextField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 1;
        mainPanel.add(passwordField, constraint);

        JLabel fileLabel = new JLabel("Profile Picture:");
        fileLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 2;
        mainPanel.add(fileLabel, constraint);

        fileSelectButton = new JButton("Select file");
        fileSelectButton.setFont(smallFont);
        fileSelectButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 2;
        mainPanel.add(fileSelectButton, constraint);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 3;
        mainPanel.add(ageLabel, constraint);

        ageField = new JTextField(4);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 3;
        mainPanel.add(ageField, constraint);

        registerButton = new JButton("Register");
        registerButton.setFont(mediumFont);
        registerButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(registerButton, constraint);

        content.add(mainPanel, BorderLayout.CENTER);

        return content;
    }

    Container mainPage() {
        Container content = new Container();
        content.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();


        //top parts (PFP, username, button for condition)
        JPanel topPanel = new JPanel(new BorderLayout());

        //adding PPF
        if (clientUser.getUserPFPImage() != null) {
            JLabel profilePicture = new JLabel(new ImageIcon(
                    clientUser.getUserPFPImage().getScaledInstance(100, 100, Image.SCALE_FAST)));
            profilePicture.setHorizontalAlignment(JLabel.CENTER);
            profilePicture.setSize(20, 20);

            topPanel.add(profilePicture, BorderLayout.WEST);
        }

        //adding username
        JLabel usernameTop = new JLabel(clientUser.getUsername());
        usernameTop.setFont(mediumFont);
        usernameTop.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(usernameTop, BorderLayout.CENTER);

        //adding friendsButton
        friendsOnlyButton = new JButton("Messaging Mode: All        ");
        friendsOnlyButton.setFont(smallFont);
        friendsOnlyButton.addActionListener(buttonActionListener);
        topPanel.add(friendsOnlyButton, BorderLayout.EAST);

        //horizontal line + Adding
        JSeparator sepHorizontal = new JSeparator();
        sepHorizontal.setOrientation(SwingConstants.HORIZONTAL);
        topPanel.add(sepHorizontal, BorderLayout.SOUTH);


        //adding topPanel
        content.add(topPanel, BorderLayout.NORTH);

        //creating searchPanel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());


        //adding searchLabel
        JLabel searchUserLabel = new JLabel("Search User:");
        searchUserLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 0;
        constraint.gridy = 0;
        searchPanel.add(searchUserLabel, constraint);

        //adding search text Field
        JTextField searchTextField = new JTextField(12);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 1;
        constraint.gridy = 0;
        searchPanel.add(searchTextField, constraint);

        //adding search Button
        searchGo = new JButton("GO!");
        searchGo.setFont(smallFont);
        searchGo.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 2;
        constraint.gridy = 0;
        searchPanel.add(searchGo, constraint);
        content.add(searchPanel, BorderLayout.CENTER);


        //Bottom buttons (seeFriends, seeConvo, Logout)

        //adding friendsButton
        friendsButton = new JButton("See Friends");
        friendsButton.setFont(mediumFont);
        friendsButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(friendsButton, constraint);


        //adding convo button
        seeConvoButton = new JButton("See Conversations");
        seeConvoButton.setFont(mediumFont);
        seeConvoButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(seeConvoButton, constraint);

        //adding logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(mediumFont);
        logoutButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(logoutButton, constraint);

        //adding mainPanel to final
        content.add(mainPanel, BorderLayout.SOUTH);
        return content;
    }


    public void run() {
        if (isConnected) {
            frame = new JFrame(APP_NAME);
            frame.setContentPane(loginPage());
            frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }
}