import javax.swing.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class viewClass {
    //Declaring variables and setting up the viewClass
    RSyntaxTextArea textArea;
    editor editorInstance;
    JDialog dialog;
    JButton closeButton;
    JLabel labels;
    JPanel mainPanel, selectorPanel, headerPanel, footerPanel, leftPanel, panel1;
    JComboBox<String> fontColorSelector, textAreaThemeSelector, appThemeSelector;
    JLabel titleLabel;
    JTabbedPane tabbedPane;
    FileClass fileclass;
    JTree filetree;
    ArrayList<JLabel> labelslist = new ArrayList<>();;
    terminalClass terminalClass;
    JTextArea terminalArea;
    JMenuBar mb;
    JButton newFileButton, button, run;
    JMenu fileMenu, editMenu, viewMenu, helpMenu;
    JMenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem, undoMenuItem, redoMenuItem,
            cutMenuItem, copyMenuItem, pasteMenuItem, selectAllMenuItem, findMenuItem, AppearanceMenuItem,
            CommandMenuItem, AboutMenuItem;
    Properties settings;
    terminalClass tc;
    JComboBox<String> comboBox;
    //Constructor for the viewClass to access other classes by taking the arguments
    @SuppressWarnings("static-access")
    public viewClass(RSyntaxTextArea textArea, JTabbedPane tabbedPane, FileClass fileclass, terminalClass terminalClass,
            editor editor, JPanel leftPanel, JPanel panel1, JButton run) {
        this.textArea = editor.textArea;
        this.leftPanel = editor.leftPanel;
        this.editorInstance = editor;
        this.tabbedPane = tabbedPane;
        this.fileclass = fileclass;
        this.filetree = fileclass.fileTree;
        this.terminalClass = terminalClass;
        this.terminalArea = terminalClass.textArea;
        this.mb = editor.menuBar;
        this.leftPanel = editor.leftPanel;
        this.newMenuItem = editor.newMenuItem;
        this.openMenuItem = editor.openMenuItem;
        this.saveMenuItem = editor.saveMenuItem;
        this.saveAsMenuItem = editor.saveAsMenuItem;
        this.exitMenuItem = editor.exitMenuItem;
        this.undoMenuItem = editor.undoMenuItem;
        this.redoMenuItem = editor.redoMenuItem;
        this.cutMenuItem = editor.cutMenuItem;
        this.copyMenuItem = editor.copyMenuItem;
        this.pasteMenuItem = editor.pasteMenuItem;
        this.selectAllMenuItem = editor.selectAllMenuItem;
        this.findMenuItem = editor.findMenuItem;
        this.AppearanceMenuItem = editor.AppearanceMenuItem;
        this.CommandMenuItem = editor.CommandMenuItem;
        this.AboutMenuItem = editor.AboutMenuItem;
        this.fileMenu = editor.fileMenu;
        this.editMenu = editor.editMenu;
        this.viewMenu = editor.viewMenu;
        this.helpMenu = editor.helpMenu;
        this.newFileButton = editor.newFileButton;
        this.panel1 = editor.panel1;
        this.run = run;
        //Loading the settings and applying the saved settings
        loadSettings();
        applySavedSettings();
    }
    //Method to show the appearance settings dialog
    public void appearance() {
        //Creating a dialog GUI and setting up the GUI components
        dialog = new JDialog();
        dialog.setSize(600, 400);
        dialog.setTitle("Appearance Settings");
        //Creating a panel to store the main components
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(30, 30, 30));
        //Creating a panel to store the header components
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 30, 30));
        titleLabel = new JLabel("Customize The Appearance");
        titleLabel.setForeground(new Color(144, 238, 144));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        //Creating a panel to store the selector components
        selectorPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        selectorPanel.setBackground(new Color(30, 30, 30));
        //Creating a string array to store the options for the font color selector
        String[] optionsColor = { "Red", "Blue", "Yellow", "Green", "Black", "White" };
        String[] optionsTheme = { "Dark", "Light" };
        fontColorSelector = createStyledComboBox(optionsColor);
        textAreaThemeSelector = createStyledComboBox(optionsTheme);
        appThemeSelector = createStyledComboBox(optionsTheme);
        //Adding the components to the selector panel
        selectorPanel.add(createStyledLabel("Font color:"));
        selectorPanel.add(fontColorSelector);
        selectorPanel.add(createStyledLabel("Text area theme:"));
        selectorPanel.add(textAreaThemeSelector);
        selectorPanel.add(createStyledLabel("App theme:"));
        selectorPanel.add(appThemeSelector);
        //Adding an action listener to the app theme selector to change the theme
        appThemeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Getting the selected theme and changing the background and foreground colors as well as saving the settings
                String selectedTheme = (String) appThemeSelector.getSelectedItem();
                if (selectedTheme.equals("Dark")) {
                    changeBackground("Black");
                    saveSettings("Black", "White", "Dark");
                } else if (selectedTheme.equals("Light")) {
                    changeBackground("White");
                    saveSettings("White", "Black", "Default");
                }
                //Before closing the dialog, apply the theme and update the components and save all files
                applyTheme();
                updateComponent();
                fileclass.saveAllFiles();
                System.exit(0);
            }
        });
        //Adding an action listener to the font color selector to change the foreground color and save the settings
        fontColorSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedColor = (String) fontColorSelector.getSelectedItem();
                changeForeground(selectedColor);
                saveSettings(null, selectedColor, null);
                textArea.revalidate();
                textArea.repaint();
                fileclass.saveAllFiles();
            }
        });
        //Adding an action listener to the text area theme selector to change the theme and save the settings
        textAreaThemeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Getting the selected theme, loading the settings and applying the theme
                String selectedTheme = (String) textAreaThemeSelector.getSelectedItem();
                Properties properties = new Properties();
                if (selectedTheme.equals("Dark")) {
                    saveSettings(properties.getProperty("backgroundColor"), properties.getProperty("foregroundColor"),
                            "Dark");
                    changeForeground("White");
                    changeBackground("Black");
                } else if (selectedTheme.equals("Light")) {
                    saveSettings(properties.getProperty("backgroundColor"), properties.getProperty("foregroundColor"),
                            "Default");
                    changeForeground("Black");
                    changeBackground("White");
                }
                JOptionPane.showMessageDialog(null, "Please restart the application for the changes to take effect.");
                applyTheme();
                updateComponent();
                fileclass.saveAllFiles();
                System.exit(0);
            }
        });
        //Creating a panel to store the footer components
        footerPanel = new JPanel();
        footerPanel.setBackground(new Color(30, 30, 30));
        closeButton = createStyledButton("Apply & Close");
        //Adding an action listener to the close button to close the dialog
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        footerPanel.add(closeButton);
        //Adding the components to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(selectorPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        //Getting the settings and loading them
        settings = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/settings.properties")) {
            if (input != null) {
                settings.load(input);
            } else {
                terminalClass.appendText("Settings file not found.");
            }
        } catch (IOException ex) {
            terminalClass.appendText("Error loading settings: " + ex.getMessage());
        }
        //Getting the background color and applying the settings
        String backgroundColor = settings.getProperty("backgroundColor", "White");
        //Applying the settings
        if (backgroundColor.equals("Black")) {
            changeDialogBG(Color.BLACK);
            changeLabelsBGColor(Color.BLACK);
            changeDialogFG(Color.WHITE);
            changeLabelsFGColor(Color.WHITE);
        } else {
            changeDialogBG(Color.WHITE);
            changeLabelsBGColor(Color.WHITE);
            changeDialogFG(Color.BLACK);
            changeLabelsFGColor(Color.BLACK);
        }
        //Making sure the dialog is visible and centered
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    //Method to create a styled combo box for selecting options
    private JComboBox<String> createStyledComboBox(String[] options) {
        comboBox = new JComboBox<>(options);
        comboBox.setForeground(titleLabel.getForeground());
        comboBox.setBackground(titleLabel.getBackground());
        comboBox.setBorder(BorderFactory.createEmptyBorder());
        comboBox.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 14));
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return comboBox;
    }
    //Method to create a styled label for the title
    private JLabel createStyledLabel(String text) {
        labels = new JLabel(text);
        labels.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 14));
        labels.setForeground(titleLabel.getForeground());
        labelslist.add(labels);
        return labels;
    }
    //Method to create a styled button for the close button
    private JButton createStyledButton(String text) {
        button = new JButton(text);
        button.setForeground(titleLabel.getForeground());
        button.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(null);
        return button;
    }
    //Method to change the foreground color of the whole program
    @SuppressWarnings("static-access")
    public void changeForeground(String selectedColor) {
        Color color = getColorFromName(selectedColor);

        if (fileclass != null && fileclass.fileTree != null) {
            fileclass.changeTreeColor(color);
        } else {
            System.out.println("File tree or fileclass is null.");
        }

        textArea.setForeground(color);
        terminalArea.setForeground(color);
        tabbedPane.setForeground(color);
        mb.setForeground(color);
        leftPanel.setForeground(color);
        newMenuItem.setForeground(color);
        openMenuItem.setForeground(color);
        saveMenuItem.setForeground(color);
        saveAsMenuItem.setForeground(color);
        exitMenuItem.setForeground(color);
        undoMenuItem.setForeground(color);
        redoMenuItem.setForeground(color);
        cutMenuItem.setForeground(color);
        copyMenuItem.setForeground(color);
        pasteMenuItem.setForeground(color);
        selectAllMenuItem.setForeground(color);
        findMenuItem.setForeground(color);
        AppearanceMenuItem.setForeground(color);
        CommandMenuItem.setForeground(color);
        AboutMenuItem.setForeground(color);
        fileMenu.setForeground(color);
        editMenu.setForeground(color);
        viewMenu.setForeground(color);
        helpMenu.setForeground(color);
        newFileButton.setForeground(color);
        fileclass.changeTreeColor(color);
        tc.textField.setForeground(color);
        leftPanel.setForeground(color);
        panel1.setForeground(color);
        textArea.setForeground(color);
        run.setForeground(color);
        editor.removeFileButton.setForeground(color);
        editor.renameFileButton.setForeground(color);
        if (filetree != null) {
            filetree.setForeground(color);
        }
        //Changing the foreground color of the dialog and its components
        changeDialogFG(color);
        changeDialogBG(color);
    }
    //Method to change the foreground color of the dialog and its components
    private void changeDialogFG(Color color) {
        if (dialog != null) {
            dialog.setForeground(color);
            mainPanel.setForeground(color);
            selectorPanel.setForeground(color);
            fontColorSelector.setForeground(color);
            textAreaThemeSelector.setForeground(color);
            appThemeSelector.setForeground(color);
            titleLabel.setForeground(color);
            headerPanel.setForeground(color);
            footerPanel.setForeground(color);
            closeButton.setForeground(color);
            comboBox.setForeground(color);
            labels.setForeground(color);
            changeLabelsFGColor(color);
        }
    }
    //Method to change the background color of the dialog and its components
    private void changeDialogBG(Color color) {
        if (dialog != null) {
            dialog.setBackground(color);
            mainPanel.setBackground(color);
            selectorPanel.setBackground(color);
            fontColorSelector.setBackground(color);
            textAreaThemeSelector.setBackground(color);
            appThemeSelector.setBackground(color);
            titleLabel.setBackground(color);
            headerPanel.setBackground(color);
            footerPanel.setBackground(color);
            closeButton.setBackground(color);
            comboBox.setBackground(color);
            labels.setBackground(color);
        }
    }
    //Method to change the foreground color of the labels
    public void changeLabelsFGColor(Color color) {
        labels.setForeground(color);
        for (JLabel label : labelslist) {
            label.setForeground(color);
        }
    }
    //Method to change the background color of the labels
    public void changeLabelsBGColor(Color color) {
        labels.setBackground(color);
        for (JLabel label : labelslist) {
            label.setBackground(color);
        }
    }
    //Method to update the components
    private void updateComponent() {
        mainPanel.revalidate();
        mainPanel.repaint();

        leftPanel.revalidate();
        leftPanel.repaint();

        textArea.revalidate();
        textArea.repaint();

        mb.revalidate();
        mb.repaint();
    }
    //Method to change the background color of the whole program
    @SuppressWarnings("static-access")
    public void changeBackground(String selectedColor) {
        Color color = getColorFromName(selectedColor);

        textArea.setBackground(color);
        terminalArea.setBackground(color);
        tabbedPane.setBackground(color);
        mb.setBackground(color);
        leftPanel.setBackground(color);
        newMenuItem.setBackground(color);
        openMenuItem.setBackground(color);
        saveMenuItem.setBackground(color);
        saveAsMenuItem.setBackground(color);
        exitMenuItem.setBackground(color);
        undoMenuItem.setBackground(color);
        redoMenuItem.setBackground(color);
        cutMenuItem.setBackground(color);
        copyMenuItem.setBackground(color);
        pasteMenuItem.setBackground(color);
        selectAllMenuItem.setBackground(color);
        findMenuItem.setBackground(color);
        AppearanceMenuItem.setBackground(color);
        CommandMenuItem.setBackground(color);
        AboutMenuItem.setBackground(color);
        fileMenu.setBackground(color);
        editMenu.setBackground(color);
        viewMenu.setBackground(color);
        helpMenu.setBackground(color);
        newFileButton.setBackground(color);
        tc.textField.setBackground(color);
        leftPanel.setBackground(color);
        panel1.setBackground(color);
        editor.removeFileButton.setBackground(color);
        editor.renameFileButton.setBackground(color);

        if (filetree != null) {
            filetree.setBackground(color);
        }
        if (dialog != null) {
            dialog.setBackground(color);
            mainPanel.setBackground(color);
            selectorPanel.setBackground(color);
            fontColorSelector.setBackground(color);
            textAreaThemeSelector.setBackground(color);
            appThemeSelector.setBackground(color);
            titleLabel.setBackground(color);
            headerPanel.setBackground(color);
            footerPanel.setBackground(color);
            closeButton.setBackground(color);
            comboBox.setBackground(color);
            labels.setBackground(color);
        }

        Color foregroundColor = getForegroundColorForBackground(color);

        textArea.setForeground(foregroundColor);
        terminalArea.setForeground(foregroundColor);
        tabbedPane.setForeground(foregroundColor);
        mb.setForeground(foregroundColor);
        leftPanel.setForeground(foregroundColor);
        newMenuItem.setForeground(foregroundColor);
        openMenuItem.setForeground(foregroundColor);
        saveMenuItem.setForeground(foregroundColor);
        saveAsMenuItem.setForeground(foregroundColor);
        exitMenuItem.setForeground(foregroundColor);
        undoMenuItem.setForeground(foregroundColor);
        redoMenuItem.setForeground(foregroundColor);
        cutMenuItem.setForeground(foregroundColor);
        copyMenuItem.setForeground(foregroundColor);
        pasteMenuItem.setForeground(foregroundColor);
        selectAllMenuItem.setForeground(foregroundColor);
        findMenuItem.setForeground(foregroundColor);
        AppearanceMenuItem.setForeground(foregroundColor);
        CommandMenuItem.setForeground(foregroundColor);
        AboutMenuItem.setForeground(foregroundColor);
        fileMenu.setForeground(foregroundColor);
        editMenu.setForeground(foregroundColor);
        viewMenu.setForeground(foregroundColor);
        helpMenu.setForeground(foregroundColor);
        newFileButton.setForeground(foregroundColor);
        tc.textField.setForeground(foregroundColor);
        leftPanel.setForeground(foregroundColor);
        panel1.setForeground(foregroundColor);
        textArea.setForeground(foregroundColor);
        run.setForeground(foregroundColor);
        editor.removeFileButton.setForeground(foregroundColor);
        editor.renameFileButton.setForeground(foregroundColor);

        if (filetree != null) {
            filetree.setForeground(foregroundColor);
        }
        if (dialog != null) {
            dialog.setForeground(foregroundColor);
            mainPanel.setForeground(foregroundColor);
            selectorPanel.setForeground(foregroundColor);
            fontColorSelector.setForeground(foregroundColor);
            textAreaThemeSelector.setForeground(foregroundColor);
            appThemeSelector.setForeground(foregroundColor);
            titleLabel.setForeground(foregroundColor);
            headerPanel.setForeground(foregroundColor);
            footerPanel.setForeground(foregroundColor);
            closeButton.setForeground(foregroundColor);
            comboBox.setForeground(foregroundColor);
            labels.setForeground(foregroundColor);
            changeLabelsFGColor(foregroundColor);
        }
    }
    //Method to get the foreground color for a given background color
    private Color getForegroundColorForBackground(Color backgroundColor) {
        int brightness = (backgroundColor.getRed() * 299 + backgroundColor.getGreen() * 587
                + backgroundColor.getBlue() * 114) / 1000;

        return brightness > 186 ? Color.BLACK : Color.WHITE;
    }
    //Method to get color from string
    public static Color getColorFromName(String colorName) {
        switch (colorName) {
            case "Red":
                return Color.RED;
            case "Blue":
                return Color.BLUE;
            case "Yellow":
                return Color.YELLOW;
            case "Green":
                return Color.GREEN;
            case "Black":
                return Color.BLACK;
            case "White":
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
    //Method to load the settings
    public void loadSettings() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("settings.properties")) {
            properties.load(input);
            applyComponentSettings(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Method to apply the saved settings
    private void applyComponentSettings(Properties properties) {
        String backgroundColor = properties.getProperty("backgroundColor", "White");
        String foregroundColor = properties.getProperty("foregroundColor", "Black");
        //Applying the settings
        changeBackground(backgroundColor);
        changeForeground(foregroundColor);

        textArea.setBackground(getColorFromName(properties.getProperty("textAreaTheme", "White")));
        System.out.println("TextArea Background: " + textArea.getBackground());
    }
    //Method to apply the saved settings
    public void applySavedSettings() {
        try (InputStream input = new FileInputStream("settings.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            //Getting the saved settings
            String backgroundColor = prop.getProperty("backgroundColor", "White");
            String foregroundColor = prop.getProperty("foregroundColor", "Black");
            String textAreaTheme = prop.getProperty("textAreaTheme", "Default");
            String appTheme = prop.getProperty("appTheme", "Light");
            //Applying the saved settings
            changeBackground(backgroundColor);
            changeForeground(foregroundColor);
            //Getting and applying the theme
            if (textAreaTheme.equals("Dark")) {
                editor.theThemeString = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";
            } else {
                editor.theThemeString = "/org/fife/ui/rsyntaxtextarea/themes/default.xml";
            }

            applyTheme();
            //Changing the foreground color, background and text area font
            if (appTheme.equals("Dark")) {
                changeBackground("Black");
            } else {
                changeBackground("White");
            }
            changeForeground(foregroundColor);
            textArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 14));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //Method to apply the theme
    public void applyTheme() {
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(editor.theThemeString));
            theme.apply(textArea);
            textArea.revalidate();
            textArea.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Method to save the settings
    public void saveSettings(String backgroundColor, String foregroundColor, String textAreaTheme) {
        Properties properties = new Properties();
        //Loading the settings 
        try (FileInputStream input = new FileInputStream("settings.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Saving the settings
        try (FileOutputStream output = new FileOutputStream("settings.properties")) {
            if (backgroundColor != null) {
                properties.setProperty("backgroundColor", backgroundColor);
            }
            if (foregroundColor != null) {
                properties.setProperty("foregroundColor", foregroundColor);
            }
            if (textAreaTheme != null) {
                properties.setProperty("textAreaTheme", textAreaTheme);
            }

            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
