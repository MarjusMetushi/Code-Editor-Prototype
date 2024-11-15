import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.Properties;

public class helpClass {
    //Declaring variables
    terminalClass terminalClass;
    Properties settings;
    Color bgcolor;
    Color fgcolor;
    //Command listing method
    public void command() throws IOException {
        //Creating a dialog GUI and setting up the GUI components
        JDialog dialog = new JDialog();
        dialog.setTitle("Command list");
        dialog.setSize(400, 400);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(13, 2));
        //Getting the settings and loading them
        settings = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/settings.properties")) {
            if (input != null) {
                settings.load(input);
            } else {
                terminalClass.appendText("Settings file not found.");
            }
        }
        String backgroundColor = settings.getProperty("backgroundColor", "White");
        String foregroundColor = settings.getProperty("foregroundColor", "Black");
        bgcolor = viewClass.getColorFromName(backgroundColor);
        fgcolor = viewClass.getColorFromName(foregroundColor);
        //Creating a string array to store the commands
        String[][] commands = {
                { "Select All", "CTRL + A" },
                { "Cut", "CTRL + X" },
                { "Copy", "CTRL + C" },
                { "Paste", "CTRL + V" },
                { "Undo", "CTRL + Z" },
                { "Redo", "CTRL + Y" },
                { "Find", "CTRL + F" },
                { "Save", "CTRL + S" },
                { "Save As", "CTRL + SHIFT + S" },
                { "Exit", "CTRL + ESC" },
                { "Remove tab", "SHIFT * ESC" },
                { "Exit", "CTRL + ESC" }
        };
        //Iterating over the commands and setting them up inside the GUI components
        for (String[] command : commands) {
            JTextField infoField = new JTextField(command[0]);
            JTextField commandField = new JTextField(command[1]);
            aestheticfix(infoField);
            aestheticfix(commandField);
            panel.add(infoField);
            panel.add(commandField);
        }
        //Adding the components to the dialog
        dialog.add(panel);
        dialog.setVisible(true);
    }
    //Method to aesthetically fix the text field
    public void aestheticfix(JTextField textField) {
        textField.setEditable(false);
        textField.setFont(new Font("Cascadia Code", Font.BOLD, 15));
        textField.setBorder(null);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBackground(bgcolor);
        textField.setForeground(fgcolor);
    }
    //Method to show the about dialog
    public void about() {
        //Creating a dialog GUI and setting up the GUI components
        JDialog dialog = new JDialog();
        dialog.setSize(500, 200);
        dialog.setTitle("About The Project");
        dialog.setLayout(new BorderLayout());
        //Creating a text area to show the about text
        JTextArea textarea = new JTextArea(
                "This text editor is a personal project made as a learning tool,\n" +
                        " designed for learning and testing Object-Oriented Programming\n" +
                        "principles but also for having fun along the learning process by experimenting.\n" +
                        "This project is inspired by Microsoft's Notepad++ and VSCode.\n\n" +
                        "~ Marjus Metushi, 2024");
        //Fixing the text area aesthetically
        textarea.setFont(new Font("Cascadia Code", Font.BOLD, 15));
        textarea.setEditable(false);
        textarea.setBackground(Color.black);
        textarea.setForeground(Color.green);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setMargin(new Insets(20, 20, 20, 20));
        //Creating a scroll pane and adding the text area to it
        JScrollPane scrollPane = new JScrollPane(textarea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        //Adding the scroll pane to the dialog and applying minor changes to the GUI
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setBackground(Color.black);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
