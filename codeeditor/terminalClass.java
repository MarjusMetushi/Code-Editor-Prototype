import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import javax.swing.*;
import java.awt.event.*;

public class terminalClass {
    // Variable declarations and setting up the terminalClass
    public static JTextField textField = new JTextField();
    public static JTextArea textArea;
    public ArrayList<String> commandHistory = new ArrayList<>();
    public int historyIndex = -1;
    static File currentDirectory = new File(System.getProperty("user.home"));
    public String line;
    String currentDir = "";
    JPanel bottomPanel;
    JScrollPane scroll;

    // TerminalClass constructor to set up the terminal GUI
    public terminalClass() {
        // Creating a bottom panel to place the scroll pane, textarea and text field
        bottomPanel = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 12));
        textField.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 12));
        scroll = new JScrollPane(textArea);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Adding the scroll pane, textarea and text field to the bottom panel
        bottomPanel.add(scroll, BorderLayout.CENTER);
        bottomPanel.add(textField, BorderLayout.SOUTH);
        // Adding an action listener to the text field to run the command when the enter
        // key is pressed
        textField.addActionListener(e -> {
            // Capturing the command from the textarea and running it
            String command = textField.getText().trim();
            // Updating the history index to store previous commands
            if (!command.isEmpty()) {
                textArea.append("> " + command + "\n");
                historyIndex = commandHistory.size();
                textField.setText("");
                try {
                    // Running the command
                    runCommand(command);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        // Adding a key listener to the textarea to navigate through the command history
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = textArea.getText().trim();
                    if (!command.isEmpty()) {
                        textArea.append("> " + command + "\n");
                        commandHistory.add(command);
                        historyIndex = commandHistory.size();
                        textArea.setText("");

                    }
                    e.consume();
                }
            }
        });
        // Add a key listener to the text field to navigate through the command history
        // using the keyboard arrows
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    // Passing the direction to the navigateCommandHistory method
                    navigateCommandHistory(-1);
                } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    // Passing the direction to the navigateCommandHistory method
                    navigateCommandHistory(1);
                }
            }
        });
    }

    // Method to navigate through the command history
    public void navigateCommandHistory(int direction) {
        if (commandHistory.isEmpty())
            return;
        // Update the history index based on the direction
        historyIndex += direction;
        if (historyIndex < 0)
            historyIndex = 0;
        if (historyIndex >= commandHistory.size())
            historyIndex = commandHistory.size() - 1;
        // Set the text field to the current command in the history
        textField.setText(commandHistory.get(historyIndex));
    }

    // Method to run a command
    public void runCommand(String command) throws IOException {
        // Adding command to the command history and updating the historyindex
        commandHistory.add(command);
        historyIndex = commandHistory.size();
        // Check if the command starts with cd and change the current directory
        // accordingly allowing for running the files in the current open file's
        // directory for windows and other OS
        if (command.trim().startsWith("cd") && System.getProperty("os.name").toLowerCase().contains("win")) {
            changeDirectory(command);
            return;
        } else if (command.trim().startsWith("cd") && System.getProperty("os.name").toLowerCase().contains("linux")) {
            changeDirectory(command);
            return;
        }
        // Thread to run the command
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ProcessBuilder processBuilder;
                // Calling the command line to execute the command
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                } else {
                    processBuilder = new ProcessBuilder("bash", "-c", command);
                }
                // Setting the current directory to the current directory
                processBuilder.directory(currentDirectory);
                processBuilder.redirectErrorStream(true);
                // Starting the process
                Process process = processBuilder.start();
                // Clear command to clear the text area from the textfield
                if (command.trim().startsWith("cls") && System.getProperty("os.name").toLowerCase().contains("win")) {
                    textArea.setText("");
                } else if (command.trim().startsWith("clear")
                        && System.getProperty("os.name").toLowerCase().contains("linux")) {
                    textArea.setText("");
                }
                // Buffered reader to read the output of the process
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    while ((line = bufferedReader.readLine()) != null) {
                        final String outputLine = line;
                        // Output to the text area
                        SwingUtilities.invokeLater(() -> {
                            textArea.append(outputLine + "\n");
                            textArea.setCaretPosition(textArea.getDocument().getLength());
                        });
                    }
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> textArea.append("Error: " + e.getMessage() + "\n"));
            }
        });
    }

    // Method to execute a python command
    public void executePythonCommand(String command) {
        // Get the script path
        String scriptPath = command.replace("python ", "").trim();
        // Thread to run the command
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Process builder to run the command and set the current directory
                ProcessBuilder processBuilder = new ProcessBuilder("python",
                        scriptPath + " " + FileClass.CurrentUniversalPath);
                processBuilder.directory(currentDirectory);
                processBuilder.redirectErrorStream(true);
                // Starting the process
                Process process = processBuilder.start();
                // Buffered reader to read the output of the process
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    while ((line = bufferedReader.readLine()) != null) {
                        final String outputLine = line;
                        // Output to the text area
                        SwingUtilities.invokeLater(() -> {
                            textArea.append(outputLine + "\n");
                            textArea.setCaretPosition(textArea.getDocument().getLength());
                        });
                    }
                }
            } catch (Exception e) {
                SwingUtilities
                        .invokeLater(() -> textArea.append("Error executing Python command: " + e.getMessage() + "\n"));
            }
        });
    }

    public static void changeDirectory(String command) throws IOException {
        String path = FileClass.CurrentUniversalPath;
        String[] commandParts = command.trim().split("\s+");
        if (commandParts.length < 2) {
            SwingUtilities.invokeLater(() -> textArea.append("cd: Missing argument\n"));
            return;
        }
        // Create a string builder to store the path
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < commandParts.length; i++) {
            pathBuilder.append(commandParts[i]);
            if (i < commandParts.length - 1) {
                pathBuilder.append(" ");
            }
        }
        // Get the path as file
        File newDir = new File(path);
        // Handle the path
        if (path.equals("~")) {
            currentDirectory = new File(System.getProperty("user.home"));
        } else if (newDir.isAbsolute()) {
            // Change the current directory if it is absolute
            if (newDir.isDirectory()) {
                currentDirectory = newDir;
            } else {
                SwingUtilities.invokeLater(() -> textArea.append("cd: No such directory: " + path + "\n"));
                return;
            }
        } else {
            // Change the current directory if it is relative
            File relativeDir = new File(currentDirectory, path);
            if (relativeDir.isDirectory()) {
                currentDirectory = relativeDir;
            } else {
                SwingUtilities.invokeLater(() -> textArea.append("cd: No such directory: " + path + "\n"));
                return;
            }
        }
        SwingUtilities.invokeLater(
                () -> textArea.append("Directory changed to: " + currentDirectory.getAbsolutePath() + "\n"));
    }

    // Getter method for the text area
    public JTextArea getTextArea() {
        return textArea;
    }

    // Method to append text to the text area
    public void appendText(String text) {
        textArea.append("\n" + text);
    }

    // Method to change the current directory path
    public static void changePath(String path) {
        currentDirectory = new File(path);
    }
}
