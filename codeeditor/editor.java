import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class editor extends JFrame {
    // Set up and variable declaration for the UI
    public static String theThemeString = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public static RSyntaxTextArea textArea = new RSyntaxTextArea(20, 20);
    editor editor = editor.this;
    terminalClass terminalClass = new terminalClass();
    FileClass fileclass = new FileClass(tabbedPane, terminalClass);
    editClass editclass = new editClass(terminalClass);
    helpClass helpClass = new helpClass();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JMenu viewMenu = new JMenu("View");
    JMenu helpMenu = new JMenu("Help");
    JMenuBar menuBar = new JMenuBar();
    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton Run = new JButton("Run");
    JButton newFileButton = new JButton("Add Tab");
    public static JButton removeFileButton = new JButton("Remove Tab");
    public static JButton renameFileButton = new JButton("Rename Tab");
    public static int currentTabIndex = 0;
    JPanel leftPanel = new JPanel();
    JMenuItem newMenuItem = new JMenuItem("New");
    JMenuItem openMenuItem = new JMenuItem("Open");
    JMenuItem saveMenuItem = new JMenuItem("Save");
    JMenuItem saveAsMenuItem = new JMenuItem("Save As");
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    JMenuItem undoMenuItem = new JMenuItem("Undo");
    JMenuItem redoMenuItem = new JMenuItem("Redo");
    JMenuItem cutMenuItem = new JMenuItem("Cut");
    JMenuItem copyMenuItem = new JMenuItem("Copy");
    JMenuItem pasteMenuItem = new JMenuItem("Paste");
    JMenuItem selectAllMenuItem = new JMenuItem("Select All");
    JMenuItem findMenuItem = new JMenuItem("Find Panel");
    JMenuItem AppearanceMenuItem = new JMenuItem("Appearance");
    JMenuItem CommandMenuItem = new JMenuItem("Commands");
    JMenuItem AboutMenuItem = new JMenuItem("About");
    JPanel rightPanel = new JPanel();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;
    JPanel bottomPanel = terminalClass.bottomPanel;
    viewClass viewClass = new viewClass(textArea, tabbedPane, fileclass, terminalClass, editor, leftPanel, panel1, Run);

    // The constructor for the UI
    @SuppressWarnings("static-access")
    editor() {
        // Setting up the frame
        setTitle("Text Editor");
        setSize(width - 40, height - 40);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        viewClass = new viewClass(textArea, tabbedPane, fileclass, terminalClass, editor, leftPanel, panel1, Run);
        viewClass.loadSettings();
        // Create a division for the panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(tabbedPane);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setDividerLocation(height * 2 / 3);
        splitPane.setResizeWeight(0.8);

        textArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 10));
        getContentPane().add(splitPane, BorderLayout.CENTER);
        // Set up the panels
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(width / 8, height));
        rightPanel.setPreferredSize(new Dimension(width - width / 8, height));

        rightPanel.add(splitPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        // Function to allow for more tabs
        addNewTab("Untitled");
        // Adding everything together to the frame
        addComponents();
        editclass.addKeyListener();

    }

    // Method to get the currently selected text area
    public static RSyntaxTextArea getSelectedTextArea() {
        Component selectedTab = tabbedPane.getSelectedComponent();
        if (selectedTab instanceof RTextScrollPane) {
            return (RSyntaxTextArea) ((RTextScrollPane) selectedTab).getTextArea();
        }
        return null;
    }

    // Method to change the theme of the text area
    @SuppressWarnings("static-access")
    public static void changeTextArea(String color, JTabbedPane tabbedPane) {
        // Getting the selected text area
        editor editorInstance = (editor) SwingUtilities.getWindowAncestor(tabbedPane);
        editClass.addKeyListener();
        RSyntaxTextArea selectedTextArea = editorInstance.getSelectedTextArea();
        // Setting up the theme
        if (selectedTextArea != null) {
            selectedTextArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 10));
            try {
                String themePath = color.equalsIgnoreCase("Black")
                        ? "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"
                        : "/org/fife/ui/rsyntaxtextarea/themes/default.xml";
                Theme theme = Theme.load(editor.class.getResourceAsStream(themePath));
                theme.apply(selectedTextArea);
            } catch (IOException e) {
                e.printStackTrace();
            }

            selectedTextArea.revalidate();
            selectedTextArea.repaint();
        }
    }

    // Method to allow the user for a new tab
    public void addNewTab(String title) {
        // Setting up the text area
        RSyntaxTextArea newTextArea = new RSyntaxTextArea();
        newTextArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 10));
        newTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        newTextArea.setCodeFoldingEnabled(true);
        // Loading the theme
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(theThemeString));
            theme.apply(newTextArea);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RTextScrollPane scrollPane = new RTextScrollPane(newTextArea);
        tabbedPane.add(title, scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
    }

    // Method to add the components to the frame
    public void addComponents() {
        setJMenuBar(menuBar);
        panel1.add(Run);
        // Setting up the buttons
        Run.setBorder(BorderFactory.createEmptyBorder());
        Run.setFont(new Font("Cascadia Code", Font.TYPE1_FONT, 12));
        Run.setBackground(null);
        Run.setFocusable(false);
        // aesthetic fix for the buttons
        aestheticButtons(fileMenu);
        aestheticButtons(editMenu);
        aestheticButtons(viewMenu);
        aestheticButtons(helpMenu);
        // Adding the menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        menuBar.add(panel1);

        // Adding the functionality to run the program by pressing the run button
        Run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component selectedTab = tabbedPane.getSelectedComponent();
                if (selectedTab instanceof JScrollPane) {
                    try {
                        fileclass.run();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        // Setting up the bottom panel and adding the menu items to the menu bar's menus
        bottomPanel.setPreferredSize(new Dimension(width, height / 4));
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);
        addFileMenuItems();
        addEditMenuItems();
        addViewMenuItems();
        addHelpMenuItems();

        leftPanel.setLayout(null);
        // aesthetic fix for the buttons of the left panel
        aestheticButtonsLeftPanel(newFileButton);
        aestheticButtonsLeftPanel(removeFileButton);
        aestheticButtonsLeftPanel(renameFileButton);
        leftPanel.revalidate();
        leftPanel.repaint();
        // Adding functionality to the newFileButton to add a new tab
        newFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewTab("New File" + currentTabIndex);
                currentTabIndex++;
            }
        });
        // Adding functionality to the removeFileButton to remove the selected tab
        removeFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePane(tabbedPane.getSelectedIndex());
            }
        });
        // Adding functionality to the renameFileButton to rename the selected tab
        renameFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog("Enter new title");
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), title);
            }
        });
        // Setting up the bounds of the buttons and placement
        newFileButton.setBounds(00, 10, 70, 20);
        newFileButton.setBorder(BorderFactory.createEmptyBorder());
        newFileButton.setMargin(new Insets(0, 5, 0, 5));
        newFileButton.setFocusable(false);
        removeFileButton.setBounds(70, 10, 70, 20);
        removeFileButton.setBorder(BorderFactory.createEmptyBorder());
        removeFileButton.setMargin(new Insets(0, 5, 0, 5));
        removeFileButton.setFocusable(false);
        renameFileButton.setBounds(140, 10, 90, 20);
        renameFileButton.setBorder(BorderFactory.createEmptyBorder());
        renameFileButton.setMargin(new Insets(0, 5, 0, 5));
        renameFileButton.setFocusable(false);
        // Adding the buttons to the left panel
        leftPanel.add(newFileButton);
        leftPanel.add(removeFileButton);
        leftPanel.add(renameFileButton);
    }

    // Method to delete a tab
    public static void deletePane(int index) {
        tabbedPane.remove(tabbedPane.getComponentAt(index));
    }

    // Method to aesthetically fix the buttons of the left panel
    public void aestheticButtonsLeftPanel(JButton button) {
        button.setFont(new Font("Cascadia Code", Font.TYPE1_FONT, 12));
        button.setOpaque(true);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(null);
    }

    // Method to aesthetically fix the buttons of the menus
    public void aestheticButtons(JMenu menuElement) {
        menuElement.setFont(new Font("Cascadia Code", Font.TYPE1_FONT, 12));
    }

    // Method to add functionalities for the menu items
    public void addFileMenuItems() {
        MenuItemAesthetic(newMenuItem);
        fileMenu.add(newMenuItem);
        // Adding functionality to the newMenuItem to create a new window
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileclass.newWindow();
            }
        });

        MenuItemAesthetic(openMenuItem);
        fileMenu.add(openMenuItem);
        // Adding functionality to the openMenuItem to open a file or folder
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    fileclass.openFileOrFolder(leftPanel);
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });

        MenuItemAesthetic(saveMenuItem);
        fileMenu.add(saveMenuItem);
        // Adding functionality to the saveMenuItem to save the file
        saveMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                fileclass.saveFile();
            }
        });

        MenuItemAesthetic(saveAsMenuItem);
        fileMenu.add(saveAsMenuItem);
        // Adding functionality to the saveAsMenuItem to save the file as
        saveAsMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                fileclass.saveAsFile();
            }
        });

        MenuItemAesthetic(exitMenuItem);
        fileMenu.add(exitMenuItem);
        // Adding functionality to the exitMenuItem to exit the program
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileclass.exit();
            }
        });
    }

    // Method to add functionalities for the edit menu items
    public void addEditMenuItems() {
        MenuItemAesthetic(undoMenuItem);
        editMenu.add(undoMenuItem);
        // Adding functionality to the undoMenuItem to undo the last action
        undoMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                editclass.undo();
            }
        });

        MenuItemAesthetic(redoMenuItem);
        editMenu.add(redoMenuItem);
        // Adding functionality to the redoMenuItem to redo the last action
        redoMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                editclass.redo();
            }
        });

        MenuItemAesthetic(cutMenuItem);
        editMenu.add(cutMenuItem);
        // Adding functionality to the cutMenuItem to cut the selected text
        cutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editclass.cut();
            }
        });

        MenuItemAesthetic(copyMenuItem);
        editMenu.add(copyMenuItem);
        // Adding functionality to the copyMenuItem to copy the selected text
        copyMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editclass.copy();
            }
        });

        MenuItemAesthetic(pasteMenuItem);
        editMenu.add(pasteMenuItem);
        // Adding functionality to the pasteMenuItem to paste the selected text
        pasteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editclass.paste();
            }
        });

        MenuItemAesthetic(selectAllMenuItem);
        editMenu.add(selectAllMenuItem);
        // Adding functionality to the selectAllMenuItem to select all the text
        selectAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editclass.selectAll();
            }
        });

        MenuItemAesthetic(findMenuItem);
        editMenu.add(findMenuItem);
        // Adding functionality to the findMenuItem to find the selected text
        findMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                editclass.find();
            }
        });
    }

    // Method to add functionalities for the view menu items
    public void addViewMenuItems() {

        MenuItemAesthetic(AppearanceMenuItem);
        viewMenu.add(AppearanceMenuItem);
        // Adding functionality to the AppearanceMenuItem to change the theme
        AppearanceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewClass.appearance();
            }
        });
    }

    // Method to add functionalities for the help menu items
    public void addHelpMenuItems() {
        MenuItemAesthetic(CommandMenuItem);
        helpMenu.add(CommandMenuItem);
        // Adding functionality to the CommandMenuItem to open the command window
        CommandMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    helpClass.command();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        MenuItemAesthetic(AboutMenuItem);
        helpMenu.add(AboutMenuItem);
        // Adding functionality to the AboutMenuItem to open the about window
        AboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpClass.about();
            }
        });
    }

    // Method to aesthetically fix the menu items
    public void MenuItemAesthetic(JMenuItem menuItem) {
        menuItem.setFont(new Font("Cascadia Code", Font.TYPE1_FONT, 12));
    }
}
