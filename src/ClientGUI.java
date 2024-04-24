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
    private JFrame frame;
    private Font largeFont;
    private Font mediumFont;
    private Font smallFont;
    private JButton loginButton;
    private JButton registerPageButton;
    private JButton registerButton;
    private JButton friendsButton;
    private JButton returnButton;
    private JButton removeButton;
    private JButton blockButton;
    private JButton fileSelectButton;
    private File selectedFile;
    ActionListener buttonActionListener = new ActionListener() {
        @Override

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) {
                if (login()) {
                    // Main page
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
            } else if (e.getSource() == returnButton) {
                //set pane to main menu
            } else if (e.getSource() == blockButton) {

            } else if (e.getSource() == removeButton) {

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
        System.out.println("Logging in!");
        return true;
    }

    private boolean register() {
        System.out.println("Registering!");
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

        JTextField usernameField = new JTextField(12);
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

        JTextField passwordField = new JTextField(12);
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

        /*TEMP CODE TO ADD FRIENDS LIST FUNCTIONALITY (FROM LOGIN PAGE)
        friendsButton = new JButton("Friends List");
        friendsButton.setFont(mediumFont);
        friendsButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(friendsButton, constraint);
        */

        content.add(mainPanel, BorderLayout.CENTER);

        return content;
    }

    Container friendsPage() {
        //temp test friends list
        User[] friends = new User[4];
        friends[0] = new User("Bobby", "123", 5, "yo.jpg");
        friends[1] = new User("Robby", "123", 5, "yo.jpg");
        friends[2] = new User("Billy", "123", 5, "yo.jpg");
        friends[3] = new User("Billy", "123", 5, "yo.jpg");

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

            // Create a panel for buttons with GridLayout
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
            blockButton = new JButton("Block");
            blockButton.setFont(smallFont);
            blockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //remove blocks user friend.getUsername() from friends list
                }
            });
            blockButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(blockButton);

            removeButton = new JButton("Remove");
            removeButton.setFont(smallFont);
            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //remove USER friend.getUsername() from friends list
                }
            });
            removeButton.setPreferredSize(new Dimension(100, 25));
            buttonPanel.add(removeButton);

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

        JTextField usernameField = new JTextField(12);
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

        JTextField passwordField = new JTextField(12);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 1;
        mainPanel.add(passwordField, constraint);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 2;
        mainPanel.add(ageLabel, constraint);

        JTextField ageField = new JTextField(4);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 2;
        mainPanel.add(ageField, constraint);

        JLabel fileLabel = new JLabel("Profile Picture:");
        fileLabel.setFont(mediumFont);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.gridx = 0;
        constraint.gridy = 3;
        mainPanel.add(fileLabel, constraint);

        fileSelectButton = new JButton("Select file");
        fileSelectButton.setFont(smallFont);
        fileSelectButton.addActionListener(buttonActionListener);
        constraint.anchor = GridBagConstraints.WEST;
        constraint.gridx = 1;
        constraint.gridy = 3;
        mainPanel.add(fileSelectButton, constraint);

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