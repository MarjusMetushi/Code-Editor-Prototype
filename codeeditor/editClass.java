import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.FileInputStream;
import javax.swing.text.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import java.awt.*;

public class editClass {
    // Declaring variables
    public static StringBuilder foundstr = new StringBuilder();
    public static Stack<String> undoStack = new Stack<>();
    public static Stack<String> redoStack = new Stack<>();
    public static int lastIndex = 0;
    public static terminalClass terminalClass;
    static Properties properties;

    // Constructor for the editClass
    @SuppressWarnings("static-access")
    public editClass(terminalClass terminalClass) {
        this.terminalClass = terminalClass;
        // Push the text to the undo stack as a base to never run on an empty
        // exception/error
        pushtoUndoStack(editor.textArea.getText());
        // Get the settings and load them
        this.properties = new Properties();
        try {
            properties.load(new FileInputStream("settings.properties"));
        } catch (Exception e) {
            terminalClass.appendText("Error loading settings: " + e.getMessage());
        }

    }

    // Method to undo the last action
    public static void undo() {
        // Get the selected text area
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        if (!undoStack.isEmpty()) {
            // Push the text to the redo stack
            redoStack.push(textArea.getText());
            String last = undoStack.pop();
            // Set the text from the redo stack and repaint and revalidate the text area
            textArea.setText(last);
            textArea.repaint();
            textArea.revalidate();
        }
    }

    // Method to redo the last action
    public static void redo() {
        // Get the selected text area
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        if (!redoStack.isEmpty()) {
            // Push the text to the undo stack
            undoStack.push(editor.textArea.getText());
            String last = redoStack.pop();
            // Set the text from the undo stack and repaint and revalidate the text area
            textArea.setText(last);
            textArea.repaint();
            textArea.revalidate();
        }
    }

    // Method to push the text to the undo stack
    public static void pushtoUndoStack(String text) {
        undoStack.push(text);
    }

    // Method to cut the selected text
    public void cut() {
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        pushtoUndoStack(textArea.getText());
        textArea.cut();
    }

    // Method to copy the selected text
    public void copy() {
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        textArea.copy();
    }

    // Method to paste to the textArea
    public void paste() {
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        // Push the text to the undo stack before pasting
        pushtoUndoStack(textArea.getText());
        textArea.paste();
    }

    // Method to select all the text
    public void selectAll() {
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        textArea.selectAll();
    }

    // Method to replace one by one
    public static void replaceonebyone(String searchText, String replaceText) {
        // Get the selected text area
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        String content = textArea.getText();
        // Get the index of the last occurrence of the search text
        lastIndex = content.indexOf(searchText, lastIndex);
        if (lastIndex != -1) {
            // Select the text and replace it with the replace text
            textArea.select(lastIndex, lastIndex + searchText.length());
            // Replace the selection with the replace text
            textArea.replaceSelection(replaceText);
            // Increment the last index by the length of the replace text
            lastIndex += replaceText.length();
        } else {
            // Set the last index to 0 if no more occurrences are found
            lastIndex = 0;
            terminalClass.appendText("No more occurrences found");
        }
    }

    // Method to find the selected text
    public static void find() {
        // Create a dialog GUI and set up the GUI components
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        JDialog dialog = new JDialog();
        dialog.setTitle("Quick Access");
        dialog.setSize(300, 200);
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.setBackground(Color.BLACK);
        // Create a text field for the search text and a text field for the replace text
        JTextField textField = new JTextField("find text");
        JTextField replaceField = new JTextField("replace with");
        // Fix the text fields aesthetically
        aestheticEdit(textField);
        aestheticEdit(replaceField);
        // Create buttons to find and replace text
        JButton findButton = new JButton("Find");
        JButton replaceButton = new JButton("Replace All");
        JButton findAllButton = new JButton("Find All");
        JButton replaceOneButton = new JButton("Replace");
        // Fix the buttons aesthetically
        aestheticButtons(findButton);
        aestheticButtons(replaceButton);
        aestheticButtons(findAllButton);
        aestheticButtons(replaceOneButton);
        // Add everything to the dialog
        panel.add(textField);
        panel.add(replaceField);
        panel.add(findAllButton);
        panel.add(findButton);
        panel.add(replaceButton);
        panel.add(replaceOneButton);
        dialog.add(panel);
        dialog.setVisible(true);
        // Create a variable to keep track of the last index
        int[] lastIndx = { 0 };
        // Add an action listener to the find button
        findButton.addActionListener(e -> {
            // Set the found string to an empty string and get the input from the text field
            foundstr.setLength(0);
            String input = textField.getText();
            if (!input.isEmpty()) {
                // Get the content of the text area
                String content = textArea.getText();
                // Get the index of the last occurrence of the input
                int index = content.indexOf(input, lastIndx[0]);
                if (index != -1) {
                    // Select the text and append it to the found string
                    textArea.select(index, index + input.length());
                    // Append the selected text to the found string
                    foundstr.append(content.substring(index, index + input.length())).append("\n");
                    // Update the last index
                    lastIndx[0] = index + input.length();
                } else {
                    // Set the last index to 0 if no more occurrences are found
                    lastIndx[0] = 0;
                    terminalClass.appendText("Reached end");
                }
            }
        });

        // Add an action listener to the replace one button
        replaceOneButton.addActionListener(e -> {
            // Get the text to replace and the text to be replaced
            String searchText = textField.getText();
            String replaceText = replaceField.getText();
            if (!searchText.isEmpty() && !replaceText.isEmpty()) {
                // Replace the text
                replaceonebyone(searchText, replaceText);
            } else {
                terminalClass.appendText("There is nothing to replace!");
            }
        });
        // Add an action listener to the find all button
        boolean[] toggle = { false };
        findAllButton.addActionListener(e -> {
            // Set the found string to an empty string and get the input from the text field
            foundstr.setLength(0);
            String input = textField.getText();
            Highlighter hl = textArea.getHighlighter();
            // If the button has been triggered once remove the highlights
            if (toggle[0]) {
                hl.removeAllHighlights();
                terminalClass.appendText("Highlights removed.");
                toggle[0] = false;
            } else {
                // If the input is not empty highlight the text
                hl.removeAllHighlights();
                if (!input.isEmpty()) {
                    // Get the content of the text area
                    String content = textArea.getText();
                    int index = content.indexOf(input);
                    boolean found = false;
                    // While we haven't reached the end of the content and found everything
                    while (index >= 0) {
                        try {
                            // Add a highlight to the text area and append the selected text to the found
                            // string
                            hl.addHighlight(index, index + input.length(), DefaultHighlighter.DefaultPainter);
                            foundstr.append(content.substring(index, index + input.length())).append("\n");
                            found = true;
                        } catch (Exception ex) {
                            terminalClass.appendText("Error: " + ex);
                        }
                        // Update the index
                        index = content.indexOf(input, index + 1);
                    }
                    // If we haven't found anything print that it wasn't found
                    if (!found) {
                        terminalClass.appendText("Text not found");
                    } else {
                        // Otherwise print that all occurrences were found and highlighted
                        terminalClass.appendText("All occurrences found and highlighted.");
                        textArea.select(0, 0);
                        toggle[0] = true;
                    }
                }
            }
        });
        // Action Listener for the replace button
        replaceButton.addActionListener(e -> {
            // Get the search text and replace text
            String searchText = textField.getText();
            String replaceText = replaceField.getText();
            if (!searchText.isEmpty() && !replaceText.isEmpty()) {
                // Replace all occurrences of the search text with the replace text
                String content = textArea.getText();
                if (content.contains(searchText)) {
                    // Replace all occurrences of the search text with the replace text
                    content = content.replace(searchText, replaceText);
                    // Replace the text in the text area
                    textArea.setText(content);
                    terminalClass.appendText("Replaced all occurrences!");
                } else {
                    terminalClass.appendText("No occurrences found!");
                }
            }
        });
    }

    // Method to add a key listener to the text area
    public static void addKeyListener() {
        // Get the selected text area
        RSyntaxTextArea textArea = editor.getSelectedTextArea();
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // If the space key is pressed push the text to the undo stack
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    pushtoUndoStack(textArea.getText());
                }
            }
        });
    }

    // Method to aesthetically fix the text field
    public static void aestheticEdit(JTextField textField) {
        textField.setFont(new Font("Cascadia Code", Font.BOLD, 15));
        try {
            // Get the foreground and background colors from the properties file and apply
            // them
            Color foregroundColor = DecodeColor(properties.getProperty("foregroundColor"));
            Color backgroundColor = DecodeColor(properties.getProperty("backgroundColor"));
            textField.setForeground(foregroundColor);
            textField.setBackground(backgroundColor);
        } catch (Exception e) {
            terminalClass.appendText("Invalid color format in properties file: " + e.getMessage());
        }
    }

    // Method to aesthetically fix the buttons
    public static void aestheticButtons(JButton button) {
        button.setFont(new Font("Cascadia Code", Font.BOLD, 15));
        try {
            // Get the foreground and background colors from the properties file and apply
            // them
            Color buttonForegroundColor = DecodeColor(properties.getProperty("foregroundColor"));
            Color buttonBackgroundColor = DecodeColor(properties.getProperty("backgroundColor"));
            button.setBackground(buttonBackgroundColor);
            button.setForeground(buttonForegroundColor);
        } catch (Exception e) {
            terminalClass.appendText("Invalid color format in properties file: " + e.getMessage());
        }
    }

    // Method to retrieve color from string
    public static Color DecodeColor(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "yellow":
                return Color.YELLOW;
            case "green":
                return Color.GREEN;
            case "black":
                return Color.BLACK;
            case "white":
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
}
