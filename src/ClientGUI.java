import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

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
    private JTextField searchTextField;
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
    private JButton blockListButton;
    private JButton logoutButton;
    private JButton sendButton;
    private JButton sendMessageButton;

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
                ArrayList<User> friendsList = new ArrayList<User>();
                try {
                    for (String friendUsername : clientUser.getFriends()) {
                        User foundFriend = client.findUser(friendUsername);
                        if (foundFriend != null) {
                            friendsList.add(foundFriend);
                        }
                    }
                } catch (Exception v) {
                    showPopup("Failed to retrieve all friends", JOptionPane.ERROR_MESSAGE);
                }

                frame.setContentPane(listPage("FRIENDS LIST", friendsList, null));
            } else if (e.getSource() == mainMenuButton) {
                //temp user for getting name
                clientUser = new User("Test User", "Password123!", 21, null);
                frame.setContentPane(mainPage());
            } else if (e.getSource() == returnButton) {
                frame.setContentPane(mainPage());
            } else if (e.getSource() == logoutButton) {

                if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {  // Confirms logout choice
                    clientUser = null; // Logs user out
                    frame.setContentPane(loginPage());
                    frame.getContentPane().revalidate();
                    JOptionPane.showMessageDialog(frame, "Logout successful", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (e.getSource() == friendsOnlyButton) {
                if (setFriendsOnly(!clientUser.isFriendsOnly())) {
                    clientUser.setFriendsOnly(!clientUser.isFriendsOnly());
                }
            } else if (e.getSource() == seeConvoButton) {
                searchMessageHistories();
            } else if (e.getSource() == searchGo) {
                searchUser();
            } else if (e.getSource() == blockListButton) {
                ArrayList<User> blockList = new ArrayList<User>();
                try {
                    for (String blockedUsername : clientUser.getBlockedUsers()) {
                        User foundBlock = client.findUser(blockedUsername);
                        if (foundBlock != null) {
                            blockList.add(foundBlock);
                        }
                    }
                } catch (Exception v) {
                    v.printStackTrace();
                    showPopup("Failed to retrieve all blocks", JOptionPane.ERROR_MESSAGE);
                }

                frame.setContentPane(listPage("BLOCK LIST", blockList, null));
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

    private boolean setFriendsOnly(boolean condition) {
        try {
            if (client.setFriendsOnly(condition)) {
                friendsOnlyButton.setText(condition ? "Messaging Mode: Friends" : "Messaging Mode: All       ");
                return true;
            }
        } catch (Exception e) {
            showPopup(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        showPopup("Failed to change message settings.", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean searchUser() {
        try {
            ArrayList<User> foundUsers = new ArrayList<User>();
            ArrayList<String> foundUsernames = client.searchUser(searchTextField.getText());
            if (foundUsernames.size() > 0) {
                for (String username : foundUsernames) {
                    User foundUser = client.findUser(username);
                    if (foundUser != null) {
                        foundUsers.add(foundUser);
                    }
                }
                frame.setContentPane(listPage("SEARCH RESULTS", foundUsers, null));
                return true;
            }
        } catch (Exception e) {
            showPopup(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        showPopup("No users found with search.", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean searchMessageHistories() {
        try {
            ArrayList<MessageHistory> foundMessageHistories = client.searchMessageHistories(clientUser.getUsername());
            if (foundMessageHistories.size() > 0) {
                frame.setContentPane(listPage("CONVERSATIONS", null, foundMessageHistories));
                return true;
            }
        } catch (Exception e) {
            showPopup(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        showPopup("No conversations exist.", JOptionPane.ERROR_MESSAGE);
        return false;
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

    Container listPage(String title, ArrayList<User> users, ArrayList<MessageHistory> messageHistories) {
        if (messageHistories == null) {
            boolean isFriendsList = title.contains("FRIEND");
            boolean isBlockList = title.contains("BLOCK");

            if (!isBlockList) {
                for (int i = 0; i < users.size(); i++) {
                    for (int j = 0; j < clientUser.getBlockedUsers().size(); j++) {
                        if (users.get(i).getUsername().equals(clientUser.getBlockedUsers().get(j))) {
                            users.remove(i);
                            break;
                        }
                    }
                }
            }

            Container content = new Container();
            content.setLayout(new BorderLayout());

            // Title Panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(largeFont);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            titlePanel.add(titleLabel, BorderLayout.CENTER);

            // Back Button
            returnButton = new JButton("Back");
            returnButton.setFont(mediumFont);
            returnButton.addActionListener(buttonActionListener);
            titlePanel.add(returnButton, BorderLayout.EAST);

            // Horizontal Separator
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            titlePanel.add(separator, BorderLayout.SOUTH);

            content.add(titlePanel, BorderLayout.NORTH);

            // Friends List Panel
            JPanel userListPanel = new JPanel(new BorderLayout());
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (User user : users) {
                if (user.getAge() == 1) {
                    listModel.addElement(String.format("%s (%d year old)", user.getUsername(), user.getAge()));
                } else {
                    listModel.addElement(String.format("%s (%d years old)", user.getUsername(), user.getAge()));
                }
            }
            JList<String> usersList = new JList<>(listModel);
            JScrollPane scroll = new JScrollPane(usersList);

            scroll.setPreferredSize(new Dimension(300, scroll.getPreferredSize().height));
            userListPanel.add(scroll, BorderLayout.CENTER);
            content.add(userListPanel, BorderLayout.WEST);

            // Actions Panel
            JPanel actionsPanel = new JPanel();
            actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

            if (isBlockList) {
                JButton blockButton = new JButton("Unblock");
                blockButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selectedUser = usersList.getSelectedValue();
                        if (selectedUser != null) {
                            String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));

                            //Unblock
                            if (client.unblockUser(selectedUsername)) {
                                clientUser.removeBlockedUsers(selectedUsername);
                            }

                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(selectedUsername)) {
                                    users.remove(i);
                                    break;
                                }
                            }

                            frame.setContentPane(listPage(title, users, null));
                            frame.getContentPane().revalidate();
                        }
                    }
                });
                actionsPanel.add(blockButton);
            } else {
                JButton openConvoButton = new JButton("Open Conversation");
                openConvoButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selectedUser = usersList.getSelectedValue();
                        if (selectedUser != null) {
                            String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                            // TODO
                        }
                    }
                });
                actionsPanel.add(openConvoButton);

                if (!isFriendsList) {
                    JButton friendButton = new JButton("Friend");
                    friendButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String selectedUser = usersList.getSelectedValue();
                            if (selectedUser != null) {
                                String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                                if (client.addFriend(selectedUsername)) {
                                    clientUser.addFriends(selectedUsername);

                                    showPopup(selectedUsername + " is now your friend.",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    });
                    actionsPanel.add(friendButton);
                }

                JButton unfriendButton = new JButton("Unfriend");
                unfriendButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selectedUser = usersList.getSelectedValue();
                        if (selectedUser != null) {
                            String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                            if (client.removeFriend(selectedUsername)) {
                                clientUser.removeFriends(selectedUsername);

                                if (isFriendsList) {
                                    for (int i = 0; i < users.size(); i++) {
                                        if (users.get(i).getUsername().equals(selectedUsername)) {
                                            users.remove(i);
                                            break;
                                        }
                                    }

                                    frame.setContentPane(listPage(title, users, null));
                                    frame.getContentPane().revalidate();
                                }

                                showPopup(selectedUsername + " has been unfriended.",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });
                actionsPanel.add(unfriendButton);

                JButton blockButton = new JButton("Block");
                blockButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selectedUser = usersList.getSelectedValue();
                        if (selectedUser != null) {
                            String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                            if (client.blockUser(selectedUsername)) {
                                clientUser.addBlockedUsers(selectedUsername);
                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).getUsername().equals(selectedUsername)) {
                                        users.remove(i);
                                        break;
                                    }
                                }

                                frame.setContentPane(listPage(title, users, null));
                                frame.getContentPane().revalidate();

                                showPopup(selectedUsername + " has been blocked.",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });
                actionsPanel.add(blockButton);
            }

            content.add(actionsPanel, BorderLayout.CENTER);
            return content;
        } else {
            Container content = new Container();
            content.setLayout(new BorderLayout());

            // Title Panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(largeFont);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            titlePanel.add(titleLabel, BorderLayout.CENTER);

            // Back Button
            returnButton = new JButton("Back");
            returnButton.setFont(mediumFont);
            returnButton.addActionListener(buttonActionListener);
            titlePanel.add(returnButton, BorderLayout.EAST);

            // Horizontal Separator
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            titlePanel.add(separator, BorderLayout.SOUTH);

            content.add(titlePanel, BorderLayout.NORTH);

            // Message History List Panel
            JPanel messageHistoryListPanel = new JPanel(new BorderLayout());
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (MessageHistory history : messageHistories) {
                String otherUsername = history.getUser1();
                if (history.getUser1().equals(clientUser.getUsername())) {
                    otherUsername = history.getUser2();
                }

                String lastMessage = history.getMessages()[history.getMessages().length - 1];
                String timestamp = lastMessage.substring(lastMessage.indexOf('[') + 1, lastMessage.indexOf(']'));
                listModel.addElement(String.format("%s (Last %s)", otherUsername, timestamp));
            }
            JList<String> messageHistoryList = new JList<>(listModel);
            JScrollPane scroll = new JScrollPane(messageHistoryList);

            scroll.setPreferredSize(new Dimension(300, scroll.getPreferredSize().height));
            messageHistoryListPanel.add(scroll, BorderLayout.CENTER);
            content.add(messageHistoryListPanel, BorderLayout.WEST);

            // Actions Panel
            JPanel actionsPanel = new JPanel();
            actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

            JButton openConvoButton = new JButton("Open Conversation");
            openConvoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = messageHistoryList.getSelectedValue();
                    if (selectedUser != null) {
                        String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                        // TODO
                    }
                }
            });
            actionsPanel.add(openConvoButton);

            JButton friendButton = new JButton("Friend");
            friendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = messageHistoryList.getSelectedValue();
                    if (selectedUser != null) {
                        String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                        if (client.addFriend(selectedUsername)) {
                            clientUser.addFriends(selectedUsername);

                            showPopup(selectedUsername + " is now your friend!",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });
            actionsPanel.add(friendButton);

            JButton unfriendButton = new JButton("Unfriend");
            unfriendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = messageHistoryList.getSelectedValue();
                    if (selectedUser != null) {
                        String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                        if (client.removeFriend(selectedUsername)) {
                            clientUser.removeFriends(selectedUsername);

                            showPopup(selectedUsername + " has been unfriended.",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });
            actionsPanel.add(unfriendButton);

            JButton blockButton = new JButton("Block");
            blockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = messageHistoryList.getSelectedValue();
                    if (selectedUser != null) {
                        String selectedUsername = selectedUser.substring(0, selectedUser.indexOf(" ("));
                        if (client.blockUser(selectedUsername)) {
                            clientUser.addBlockedUsers(selectedUsername);
                            for (int i = 0; i < messageHistories.size(); i++) {
                                MessageHistory currentHistory = messageHistories.get(i);
                                if (currentHistory.getUser1().equals(selectedUsername)
                                        || currentHistory.getUser2().equals(selectedUsername)) {
                                    messageHistories.remove(i);
                                    break;
                                }
                            }

                            frame.setContentPane(listPage(title, null, messageHistories));
                            frame.getContentPane().revalidate();

                            showPopup(selectedUsername + " has been blocked!",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });
            actionsPanel.add(blockButton);

            content.add(actionsPanel, BorderLayout.CENTER);
            return content;
        }
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
            JLabel profilePicture = new JLabel(new ImageIcon(clientUser.getUserPFPImage().getScaledInstance(100, 100, Image.SCALE_FAST)));
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
        friendsOnlyButton = new JButton(clientUser.isFriendsOnly() ? "Messaging Mode: Friends" : "Messaging Mode: All       ");
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
        searchTextField = new JTextField(12);
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

        //adding convo button
        seeConvoButton = new JButton("See Conversations");
        seeConvoButton.setFont(mediumFont);
        seeConvoButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(seeConvoButton, constraint);

        //adding friendsButton
        friendsButton = new JButton("See Friends");
        friendsButton.setFont(mediumFont);
        friendsButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(friendsButton, constraint);

        //adding block list button
        blockListButton = new JButton("See Blocks");
        blockListButton.setFont(mediumFont);
        blockListButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(blockListButton, constraint);

        //adding logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(mediumFont);
        logoutButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(logoutButton, constraint);

        //adding mainPanel to final
        content.add(mainPanel, BorderLayout.SOUTH);
        return content;
    }

    Container MessagingPage() {

        Container content = new Container();
        content.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        //title panel top
        JLabel titleLabel = new JLabel("Messaging");
        titleLabel.setFont(largeFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        //titlePanel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        //back button
        returnButton = new JButton("Back");
        returnButton.setFont(smallFont);
        returnButton.addActionListener(buttonActionListener);
        titlePanel.add(returnButton, BorderLayout.EAST);

        //horizontal line + Adding
        JSeparator sepHorizontal = new JSeparator();
        sepHorizontal.setOrientation(SwingConstants.HORIZONTAL);
        titlePanel.add(sepHorizontal, BorderLayout.SOUTH);

        //adding topPanel
        content.add(titlePanel, BorderLayout.NORTH);




        //BLOCK OF TESTS ------------ MESSAGING  START


        JPanel scrollPanelTest = new JPanel();
        scrollPanelTest.setLayout(new BoxLayout(scrollPanelTest, BoxLayout.Y_AXIS));


        //individual jpanel test
        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.X_AXIS));

        //name test
        JLabel nameTest = new JLabel("Name Here!");
        nameTest.setFont(mediumFont);
        layoutPanel.add(nameTest, BorderLayout.WEST);


        //message test
        JTextArea messageTEST = new JTextArea("This is where the message would go, im making this as long as possible hoping that the message would do something that dosent make the entire thing cut off");
        messageTEST.setFont(mediumFont);
        messageTEST.setLineWrap(true);
        messageTEST.setWrapStyleWord(true);
        layoutPanel.add(messageTEST, BorderLayout.EAST);

        scrollPanelTest.add(layoutPanel);

        //////////////


        JSeparator lineTest = new JSeparator();
        lineTest.setOrientation(SwingConstants.HORIZONTAL);
        scrollPanelTest.add(lineTest, BorderLayout.SOUTH);

        ///////////////


        ////////////////////////////

        //individual jpanel test
        JPanel layoutPanel2 = new JPanel();
        layoutPanel2.setLayout(new BoxLayout(layoutPanel2, BoxLayout.X_AXIS));

        //name test
        JLabel nameTest2 = new JLabel("Gavin");
        nameTest2.setFont(mediumFont);
        layoutPanel2.add(nameTest2, BorderLayout.WEST);


        //message test
        JTextArea messageTEST2 = new JTextArea("GAVIN TEST that dosent make the entire thing cut off");
        messageTEST2.setFont(mediumFont);
        messageTEST2.setLineWrap(true);
        messageTEST2.setWrapStyleWord(true);
        layoutPanel2.add(messageTEST2, BorderLayout.EAST);

        scrollPanelTest.add(layoutPanel2);


        //////////////////////


        JSeparator lineTest2 = new JSeparator();
        lineTest2.setOrientation(SwingConstants.HORIZONTAL);
        scrollPanelTest.add(lineTest2, BorderLayout.SOUTH);

        //////////////////////


        //individual jpanel test
        JPanel layoutPanel3 = new JPanel();
        layoutPanel3.setLayout(new BoxLayout(layoutPanel3, BoxLayout.X_AXIS));

        //name test
        JLabel nameTest3 = new JLabel("Colten");
        nameTest3.setFont(mediumFont);
        layoutPanel3.add(nameTest3, BorderLayout.WEST);


        //message test
        JTextArea messageTEST3 = new JTextArea("I hate CS!");
        messageTEST3.setFont(mediumFont);
        messageTEST3.setLineWrap(true);
        messageTEST3.setWrapStyleWord(true);
        layoutPanel3.add(messageTEST3, BorderLayout.EAST);

        scrollPanelTest.add(layoutPanel3);


        content.add(new JScrollPane(scrollPanelTest), BorderLayout.CENTER);


        //BLOCK OF TESTS ------------ END



        //sendMessagePanel
        JPanel sendMessagePanel = new JPanel();
        sendMessagePanel.setLayout(new GridBagLayout());


        //adding sendMessageLabel
        JLabel sendMessageLabel = new JLabel("Send Message:");
        sendMessageLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 0;
        constraint.gridy = 0;
        sendMessagePanel.add(sendMessageLabel, constraint);

        //adding sendMessage text Field
        JTextField sendMessageTextField = new JTextField(12);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 1;
        constraint.gridy = 0;
        sendMessagePanel.add(sendMessageTextField, constraint);

        //adding send Message Button
        sendMessageButton = new JButton("Send");
        sendMessageButton.setFont(smallFont);
        sendMessageButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 2;
        constraint.gridy = 0;
        sendMessagePanel.add(sendMessageButton, constraint);

        content.add(sendMessagePanel, BorderLayout.SOUTH);


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