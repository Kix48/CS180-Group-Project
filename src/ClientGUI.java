import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
/**
 * ClientGUI.java Class
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
public class ClientGUI extends JComponent implements Runnable {
    private final String APP_NAME = "Chirp";
    private final int INITIAL_WIDTH = 800;
    private final int INITIAL_HEIGHT = 600;
    private final long REFRESH_TIME_MS = 5000;
    private Client client;
    private boolean isConnected;
    private User clientUser;
    private JFrame frame;
    private Font largeFont;
    private Font mediumFont;
    private Font smallFont;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField ageField;
    private JTextField searchTextField;
    private JTextField messageIndexField;
    private JTextArea sendMessageText;
    private JButton loginButton;
    private JButton registerPageButton;
    private JButton registerButton;
    private JButton friendsButton;
    private JButton returnButton;
    private JButton fileSelectButton;
    private File selectedFile;
    private JButton friendsOnlyButton;
    private JButton searchGo;
    private JButton seeConvoButton;
    private JButton blockListButton;
    private JButton logoutButton;
    private JButton sendMessageButton;
    private String currentMessageReceiver;
    private Thread updaterThread;
    private ArrayList<MessageHistory> currentConversations;
    private boolean insideConversation = false;
    private boolean insideConversationList = false;

    ActionListener buttonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) {
                if (login()) {
                    // Create and start thread which keeps our client up-to-date
                    updaterThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            conversationUpdater();
                        }
                    });

                    updaterThread.start();
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
            } else if (e.getSource() == returnButton) {
                frame.setContentPane(mainPage());
            } else if (e.getSource() == logoutButton) {

                if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Confirm",
                                                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {  // Confirms logout choice
                    clientUser = null; // Logs user out
                    frame.setContentPane(loginPage());
                    frame.getContentPane().revalidate();
                    JOptionPane.showMessageDialog(frame, "Logout successful", "Goodbye",
                                                  JOptionPane.INFORMATION_MESSAGE);
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
            } else if (e.getSource() == sendMessageButton) {
                try {
                    if (sendMessageText.getText() != null && !sendMessageText.getText().equals("")) {
                        if (client.sendMessage(currentMessageReceiver, sendMessageText.getText())) {
                            MessageHistory history = client.getMessageHistory(currentMessageReceiver);

                            frame.setContentPane(messagingPage(history, currentMessageReceiver));
                            frame.getContentPane().revalidate();
                        }
                    }
                } catch (Exception v) {
                    showPopup(v.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }

            // Needs to be called to change container content at runtime
            frame.getContentPane().revalidate();
        }
    };

    // Needed for text wrapping of messages
    // https://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height?rq=1/
    public class CustomCellRenderer extends DefaultListCellRenderer {

        private JPanel panel;
        private JTextArea textArea;

        public CustomCellRenderer() {
            panel = new JPanel();
            panel.setLayout(new BorderLayout());

            // text
            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            panel.add(textArea, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList list,
                                                      final Object value, final int index, final boolean isSelected,
                                                      final boolean hasFocus) {
            textArea.setText((String) value);
            int width = list.getWidth();
            if (width > 0)
                textArea.setSize(width, Short.MAX_VALUE);

            return panel;
        }
    }

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

    private synchronized void conversationUpdater() {
        while (true) {
            try {
                // Get updated conversations
                currentConversations = client.searchMessageHistories(clientUser.getUsername());
                if (insideConversation && sendMessageText != null && sendMessageText.getText().equals("")
                && messageIndexField != null && messageIndexField.getText().equals("")) {
                    MessageHistory currentMessageHistory = null;
                    for (MessageHistory history : currentConversations) {
                        if (history.getUser1().equals(currentMessageReceiver)
                                || history.getUser2().equals(currentMessageReceiver)) {
                            currentMessageHistory = history;
                            break;
                        }
                    }

                    frame.setContentPane(messagingPage(currentMessageHistory, currentMessageReceiver));
                    frame.getContentPane().validate();
                } else if (insideConversationList) {
/*                    if (currentConversations.size() > 0) {
                        frame.setContentPane(listPage("CONVERSATIONS", null, currentConversations));
                        frame.getContentPane().validate();
                    }*/
                }

                wait(REFRESH_TIME_MS);
            } catch (Exception e) {
                // Most likely going to be an interrupt exception
                if (!e.getMessage().equals("No message histories found.")) {
                    e.printStackTrace();
                }
            }
        }
    }

    Container loginPage() {
        insideConversation = false;
        insideConversationList = false;

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

        passwordField = new JPasswordField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 1;
        mainPanel.add(passwordField, constraint);

        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                passwordField.setEchoChar(cb.isSelected() ? '\u0000' : '•');
            }
        });
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 2;
        constraint.gridy = 1;
        constraint.gridwidth = 1;
        mainPanel.add(showPasswordCheckbox, constraint);

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

        content.add(mainPanel, BorderLayout.CENTER);

        return content;
    }

    Container listPage(String title, ArrayList<User> users, ArrayList<MessageHistory> messageHistories) {
        insideConversation = false;
        insideConversationList = false;

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
                    listModel.addElement(String.format("%s", user.getUsername()));
                } else {
                    listModel.addElement(String.format("%s", user.getUsername()));
                }
            }
            JList<String> usersList = new JList<>(listModel);
            JScrollPane scroll = new JScrollPane(usersList);

            scroll.setPreferredSize(new Dimension(400, scroll.getPreferredSize().height));
            userListPanel.add(scroll, BorderLayout.CENTER);
            content.add(userListPanel, BorderLayout.WEST);

            // Actions Panel
            JPanel actionsPanel = new JPanel();
            actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

            JButton viewProfileButton = new JButton("View Profile");
            viewProfileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = usersList.getSelectedValue();
                    if (selectedUser != null) {
                        try {
                            User user = client.findUser(selectedUser);
                            if (user != null) {
                                frame.setContentPane(profilePage(user, title, users, null));
                                frame.getContentPane().revalidate();
                            } else {
                                showPopup("Could not retrieve user information!", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception v) {
                            showPopup("Failed to view profile for user!", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            actionsPanel.add(viewProfileButton);

            if (isBlockList) {
                JButton blockButton = new JButton("Unblock");
                blockButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selectedUser = usersList.getSelectedValue();
                        if (selectedUser != null) {
                            //Unblock
                            if (client.unblockUser(selectedUser)) {
                                clientUser.removeBlockedUsers(selectedUser);
                            }

                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(selectedUser)) {
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
                            MessageHistory history = client.getMessageHistory(selectedUser);
                            currentMessageReceiver = selectedUser;

                            frame.setContentPane(messagingPage(history, selectedUser));
                            frame.getContentPane().revalidate();
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
                                if (client.addFriend(selectedUser)) {
                                    clientUser.addFriends(selectedUser);

                                    showPopup(selectedUser + " is now your friend.",
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
                            if (client.removeFriend(selectedUser)) {
                                clientUser.removeFriends(selectedUser);

                                if (isFriendsList) {
                                    for (int i = 0; i < users.size(); i++) {
                                        if (users.get(i).getUsername().equals(selectedUser)) {
                                            users.remove(i);
                                            break;
                                        }
                                    }

                                    frame.setContentPane(listPage(title, users, null));
                                    frame.getContentPane().revalidate();
                                }

                                showPopup(selectedUser + " has been unfriended.",
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
                            if (client.blockUser(selectedUser)) {
                                clientUser.addBlockedUsers(selectedUser);
                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).getUsername().equals(selectedUser)) {
                                        users.remove(i);
                                        break;
                                    }
                                }

                                frame.setContentPane(listPage(title, users, null));
                                frame.getContentPane().revalidate();

                                showPopup(selectedUser + " has been blocked.",
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
            insideConversationList = true;
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

            scroll.setPreferredSize(new Dimension(400, scroll.getPreferredSize().height));
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
                        MessageHistory history = client.getMessageHistory(selectedUser);
                        currentMessageReceiver = selectedUser;

                        frame.setContentPane(messagingPage(history, selectedUser));
                        frame.getContentPane().revalidate();
                    }
                }
            });
            actionsPanel.add(openConvoButton);

            JButton friendButton = new JButton("Friend");
            friendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedUser = messageHistoryList.getSelectedValue();
                    if (selectedUser != null) {
                        if (client.addFriend(selectedUser)) {
                            clientUser.addFriends(selectedUser);

                            showPopup(selectedUser + " is now your friend!",
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
                        if (client.removeFriend(selectedUser)) {
                            clientUser.removeFriends(selectedUser);

                            showPopup(selectedUser + " has been unfriended.",
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
                        if (client.blockUser(selectedUser)) {
                            clientUser.addBlockedUsers(selectedUser);
                            for (int i = 0; i < messageHistories.size(); i++) {
                                MessageHistory currentHistory = messageHistories.get(i);
                                if (currentHistory.getUser1().equals(selectedUser)
                                        || currentHistory.getUser2().equals(selectedUser)) {
                                    messageHistories.remove(i);
                                    break;
                                }
                            }

                            frame.setContentPane(listPage(title, null, messageHistories));
                            frame.getContentPane().revalidate();

                            showPopup(selectedUser + " has been blocked!",
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
        insideConversation = false;
        insideConversationList = false;

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

        passwordField = new JPasswordField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 1;
        mainPanel.add(passwordField, constraint);

        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                passwordField.setEchoChar(cb.isSelected() ? '\u0000' : '•');
            }
        });
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 2;
        constraint.gridy = 1;
        mainPanel.add(showPasswordCheckbox, constraint);

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
        insideConversation = false;
        insideConversationList = false;

        Container content = new Container();
        content.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();


        //top parts (PFP, username, button for condition)
        JPanel topPanel = new JPanel(new BorderLayout());

        //adding PPF
        if (clientUser.getUserPFPImage() != null) {
            JLabel profilePicture = new JLabel(new ImageIcon(clientUser.getUserPFPImage()
                                                             .getScaledInstance(100, 100, Image.SCALE_FAST)));
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
        friendsOnlyButton = new JButton(clientUser.isFriendsOnly() ? "Messaging Mode: Friends" 
                                        : "Messaging Mode: All       ");
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

    Container messagingPage(MessageHistory history, String otherUsername) {
        insideConversation = true;
        insideConversationList = false;

        Container content = new Container();
        content.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("MESSAGING: " + otherUsername);
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

        // Message List Panel
        JPanel messageListPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (history != null) {
            for (int i = 0; i < history.getMessages().length; i++) {
                String message = history.getMessages()[i];
                String[] splitMessage = message.split(": ");
                String actualMessage = splitMessage[1];

                // In-case the message include the character ':'
                if (splitMessage.length > 2) {
                    for (int j = 2; j < splitMessage.length; j++) {
                        actualMessage += (":" + splitMessage[j]);
                    }
                }

                String formattedMessage = String.format("(%d) %s:\n%s\n", i, splitMessage[0], actualMessage);
                listModel.addElement(formattedMessage);
            }
        }

        final JList<String> messagesList = new JList<>(listModel) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        messagesList.setCellRenderer(new CustomCellRenderer());
        ComponentListener messageListListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                messagesList.setFixedCellHeight(10);
                messagesList.setFixedCellHeight(-1);
            }

        };

        messagesList.addComponentListener(messageListListener);
        JScrollPane scroll = new JScrollPane(messagesList);

        scroll.setPreferredSize(new Dimension(400, scroll.getPreferredSize().height));
        messageListPanel.add(scroll, BorderLayout.CENTER);
        content.add(messageListPanel, BorderLayout.WEST);

        // Actions Panel
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        //adding sendMessage text Field
        sendMessageText = new JTextArea(14, 20);
        sendMessageText.setLineWrap(true);
        sendMessageText.setWrapStyleWord(true);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 0;
        actionsPanel.add(sendMessageText, constraint);

        //adding send Message Button
        sendMessageButton = new JButton("Send");
        sendMessageButton.setFont(smallFont);
        sendMessageButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        actionsPanel.add(sendMessageButton, constraint);

        JLabel ageLabel = new JLabel("Message ID:");
        ageLabel.setFont(smallFont);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.NONE;
        actionsPanel.add(ageLabel, constraint);

        messageIndexField = new JTextField(4);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 1;
        actionsPanel.add(messageIndexField, constraint);

        JButton removeMessageButton = new JButton("Remove Message");
        removeMessageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedMessage = Integer.parseInt(messageIndexField.getText());
                    if (selectedMessage >= 0 && selectedMessage < history.getMessages().length) {
                        if (client.removeMessage(otherUsername, selectedMessage)) {
                            history.removeMessage(clientUser.getUsername(), selectedMessage);

                            frame.setContentPane(messagingPage(history, otherUsername));
                            frame.getContentPane().revalidate();

                            showPopup("Message has been removed.",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        showPopup("Not a valid message index!", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException v) {
                    showPopup("Message Index must be a number!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 5;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        actionsPanel.add(removeMessageButton, constraint);

        content.add(actionsPanel, BorderLayout.CENTER);
        return content;
    }

    Container profilePage(User user, String oldTitle, ArrayList<User> oldUsers,
                          ArrayList<MessageHistory> oldMessageHistories) {
        insideConversation = false;
        insideConversationList = false;

        Container content = new Container();
        content.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(user.getUsername() + "'s PROFILE");
        titleLabel.setFont(largeFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        if (user.getUserPFPImage() != null) {
            JLabel profilePicture = new JLabel(new ImageIcon(user.getUserPFPImage()
                    .getScaledInstance(300, 300, Image.SCALE_FAST)));
            profilePicture.setHorizontalAlignment(JLabel.CENTER);
            profilePicture.setSize(300, 300);
            constraint.anchor = GridBagConstraints.CENTER;
            constraint.gridx = 0;
            constraint.gridy = 0;
            mainPanel.add(profilePicture, constraint);
        }

        JLabel ageLabel = null;
        if (user.getAge() == 1) {
            ageLabel = new JLabel("" + user.getAge() + " year old");
        } else {
            ageLabel = new JLabel("" + user.getAge() + " years old");
        }

        ageLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 1;
        mainPanel.add(ageLabel, constraint);

        boolean isFriend = false;
        for (String friend :clientUser.getFriends()) {
            if (friend.equals(user.getUsername())) {
                isFriend = true;
                break;
            }
        }

        JLabel friendLabel = null;
        if (isFriend) {
            friendLabel = new JLabel("Your friend");
        } else {
            friendLabel = new JLabel("Not your friend");
        }

        friendLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 2;
        mainPanel.add(friendLabel, constraint);

        JButton returnButton = new JButton("Return");
        returnButton.setFont(mediumFont);
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(listPage(oldTitle, oldUsers, oldMessageHistories));
                frame.getContentPane().revalidate();
            }
        });
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(returnButton, constraint);

        content.add(mainPanel, BorderLayout.CENTER);

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
