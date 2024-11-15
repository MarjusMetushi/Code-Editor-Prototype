import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.awt.*;

public class FileClass extends JPanel {
    // Declaring variables and setting up the FileClass
    static JTabbedPane tabbedPane;
    static Map<File, JTextArea> fileTabMap = new HashMap<>();
    Map<Integer, File> filePathMap = new HashMap<>();
    DefaultTreeModel treeModel;
    JTree fileTree;
    String parentDirPath = "";
    static terminalClass terminalClass;
    JTextArea textArea;
    JTextField inputArea;
    public static Process process;
    public static SwingWorker<Void, String> worker;
    private Properties settings;
    public static boolean isRunning = false;
    public static String CurrentUniversalPath = "";

    @SuppressWarnings("static-access")
    public FileClass(JTabbedPane tabbedPane, terminalClass terminalclass) {
        this.tabbedPane = tabbedPane;
        this.terminalClass = terminalclass;
        this.textArea = terminalClass.textArea;
        this.inputArea = terminalClass.textField;
    }

    // Method to load a new directory
    @SuppressWarnings("static-access")
    public void loadNewDirectory(File directory) throws IOException {
        // Create a new root node and tree model
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory.getName());
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        // Remove everything and add the directory to the tree
        root.removeAllChildren();
        addDirectoryToTree(directory, root);
        // Reload the tree
        treeModel.reload();
    }

    // Method to add a directory to the tree
    private void addDirectoryToTree(File directory, DefaultMutableTreeNode parent) {
        // Create a new directory node and add it to the parent node
        DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(directory.getName());
        parent.add(dirNode);
        // Get the files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            // Iterate over the files
            for (File file : files) {
                // Handle the file based on whether it is a directory or not
                if (file.isDirectory()) {
                    addDirectoryToTree(file, dirNode);
                } else {
                    addFileToTree(file, dirNode);
                }
            }
        }
    }

    // Method to save the current file
    public static void saveFile() {
        // Get the selected index
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            // Get the scroll pane and text area
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            scrollPane.setBorder(null);
            JTextArea currentTextArea = (JTextArea) scrollPane.getViewport().getView();
            // Iterate over the file tab map
            for (Map.Entry<File, JTextArea> entry : fileTabMap.entrySet()) {
                // Check if the text area found is the same as the current text area
                if (entry.getValue() == currentTextArea) {
                    File currentFile = entry.getKey();
                    try {
                        // Write the text area to the file
                        Files.write(Paths.get(currentFile.getAbsolutePath()), currentTextArea.getText().getBytes());
                        terminalClass.appendText("File saved successfully!");
                    } catch (IOException e) {
                        terminalClass.appendText("Error: " + e.getMessage());
                    }
                    return;
                }
            }
        } else {
            terminalClass.appendText("No file selected to save.");
        }
    }

    // Method to save all the files
    public void saveAllFiles() {
        // Iterate over the file tab map
        for (Map.Entry<File, JTextArea> entry : fileTabMap.entrySet()) {
            File currentFile = entry.getKey();
            try {
                // Write the text area to the files
                Files.write(Paths.get(currentFile.getAbsolutePath()), entry.getValue().getText().getBytes());
                terminalClass.appendText("File saved successfully!");
            } catch (IOException e) {
                terminalClass.appendText("Error: " + e.getMessage());
            }
        }
    }

    // Method to save the file as the user's desired file format and name
    public static void saveAsFile() {
        // Get the selected index
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            // Get the scroll pane and text area
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            scrollPane.setBorder(null);
            JTextArea currentTextArea = (JTextArea) scrollPane.getViewport().getView();
            // Create a file chooser and get the user's selection
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setDialogTitle("Save As");
            int userSelection = fileSaver.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Get the file to be saved and write it to a file
                File fileToSave = fileSaver.getSelectedFile();
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(currentTextArea.getText());
                    // Keep track of the file and text area
                    fileTabMap.put(fileToSave, currentTextArea);
                    tabbedPane.setTitleAt(selectedIndex, fileToSave.getName());
                    terminalClass.appendText("File saved successfully!");
                } catch (IOException e) {
                    terminalClass.appendText("Error: " + e.getMessage());
                }
            }
        } else {
            terminalClass.appendText("No file selected to save.");
        }
    }

    // Method to run the program
    public void run() throws IOException {
        // Get the selected index
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            // Get the scroll pane and text area
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            scrollPane.setBorder(null);
            JTextArea currentTextArea = (JTextArea) scrollPane.getViewport().getView();
            // Iterate over the file tab map
            for (Map.Entry<File, JTextArea> entry : fileTabMap.entrySet()) {
                // Check if the text area found is the same as the current text area
                if (entry.getValue() == currentTextArea) {
                    // Get the current file
                    File currentFile = entry.getKey();
                    // Get the file extension
                    String extension = getFileExtension(currentFile);
                    // Handle the file based on the file extension
                    switch (extension) {
                        case "java":
                            executeInBackground(() -> {
                                try {
                                    compileAndRunJava(currentFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        case "py":
                            executeInBackground(() -> {
                                try {
                                    try {
                                        compileAndRunPython(currentFile);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        case "c":
                            executeInBackground(() -> {
                                try {
                                    compileAndRunC(currentFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        case "cpp":
                            executeInBackground(() -> {
                                try {
                                    compileAndRunCpp(currentFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        case "js":
                            executeInBackground(() -> {
                                try {
                                    compileAndRunJS(currentFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        default:
                            terminalClass.appendText("Unsupported file extension: " + extension);
                    }
                    return;
                }
            }
        } else {
            terminalClass.appendText("No file selected to run.");
        }
    }

    // Method to execute a task in the background
    private void executeInBackground(Runnable task) {
        // Create a SwingWorker to execute the task in the background
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    terminalClass.appendText("Error occurred: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // Method to compile and run a python file
    public void compileAndRunPython(File pythonFile) throws IOException, InterruptedException {
        // Get the project root and lib directory
        Path projectRoot = Paths.get(CurrentUniversalPath).toAbsolutePath();
        Path libPath = projectRoot.resolve("lib");

        String pythonCommand = "python";
        // Get the script path
        String scriptPath = pythonFile.getAbsolutePath();

        String runCommand;
        // Format the run command based on whether the lib directory exists or not
        if (Files.exists(libPath)) {
            String libPathString = libPath.toString();
            runCommand = String.format("PYTHONPATH=\"%s\" %s \"%s\"", libPathString, pythonCommand, scriptPath);
        } else {
            runCommand = String.format("%s \"%s\"", pythonCommand, scriptPath);
        }
        // Run the runCommand
        terminalClass.appendText("Running Python command...");
        runCommand(runCommand);

        terminalClass.appendText("Python command executed.");
    }

    // Method to compile and run a c file
    public void compileAndRunC(File cFile) throws IOException {
        // Get the root, bin directory, and lib directory
        Path root = Paths.get(CurrentUniversalPath).toAbsolutePath();
        Path binDir = root.resolve("bin");
        Path libPath = root.resolve("lib");
        boolean useBin = Files.exists(binDir);
        String outputDir = useBin ? binDir.toString() : cFile.getParent();
        // Get the executable path
        String executablePath = outputDir + "/a.exe";
        String compileCommand;
        // Get the library files
        File[] libraryFiles = new File(libPath.toString())
                .listFiles((dir, name) -> name.endsWith(".lib") || name.endsWith(".a"));
        // Create a string builder to store the libraries
        StringBuilder libraries = new StringBuilder();
        if (libraryFiles != null) {
            for (File libFile : libraryFiles) {
                String libName = libFile.getName().replaceFirst("[.][^.]+$", "");
                libraries.append(" -l").append(libName);
            }
        }
        // Format the compile command
        compileCommand = String.format("gcc -o \"%s\" -I\"%s\" \"%s\" -L\"%s\"%s",
                executablePath,
                libPath.toString(),
                cFile.getAbsolutePath(),
                libPath.toString(),
                libraries.toString());

        // Append the text to the terminal and compile the compileCommand
        terminalClass.appendText("Compiling C file...");
        runCommand(compileCommand);
        // After compilation, check if the executable exists and run it
        if (Files.exists(Paths.get(executablePath))) {
            terminalClass.appendText("Compilation successful. Running the executable...\n");
            runCommand(executablePath);
        } else {
            terminalClass.appendText("Compilation failed or executable not found.\n");
        }
        terminalClass.appendText("C file compiled and executed.");
    }

    // Method to compile and run a c++ file
    public void compileAndRunCpp(File cppFile) throws IOException {
        // Get the root, bin directory, and lib directory
        Path root = Paths.get(CurrentUniversalPath).toAbsolutePath();
        Path binDir = root.resolve("bin");
        Path libPath = Paths.get("lib").toAbsolutePath();
        boolean useBin = Files.exists(binDir);
        String outputDir = useBin ? binDir.toString() : cppFile.getParent();
        // Get the executable path
        String executablePath = outputDir + "/a.out";
        File[] libraryFiles = new File(libPath.toString()).listFiles((dir, name) ->
        // Check if the file is a library file (.lib, .a, or .so)
        name.endsWith(".lib") || name.endsWith(".a") || name.endsWith(".so"));
        // Create a string builder to store the libraries
        StringBuilder libraries = new StringBuilder();
        if (libraryFiles != null) {
            for (File libFile : libraryFiles) {
                String libName = libFile.getName().replaceFirst("[.][^.]+$", "");
                libraries.append(" -l").append(libName);
            }
        }
        // Format the compile command
        String compileCommand = String.format("g++ -o \"%s\" -I\"%s\" \"%s\" -L\"%s\" %s",
                executablePath,
                libPath.toString(),
                cppFile.getAbsolutePath(),
                libPath.toString(),
                libraries.toString());
        // Append the text to the terminal and compile the compileCommand
        terminalClass.appendText("Compiling C++ file...\n");
        runCommand(compileCommand);
        // After compilation, check if the executable exists and run it
        if (Files.exists(Paths.get(executablePath))) {
            terminalClass.appendText("Compilation successful. Running the executable...\n");
            runCommand(executablePath);
        } else {
            terminalClass.appendText("Compilation failed or executable not found.\n");
        }
    }

    // Method to compile and run a javascript file using node
    public void compileAndRunJS(File jsFile) throws IOException {
        terminalClass.appendText("Executing JavaScript file...");
        // Create the run command for running a javascript file using node and pass it
        // to the runCommand method
        String runCommand = String.format("node %s", jsFile.getAbsolutePath());
        runCommand(runCommand);
        terminalClass.appendText("JavaScript file executed.");
    }

    // Method to run a command
    @SuppressWarnings("static-access")
    public static void runCommand(String initialCommand) {
        try {
            // Split the command into parts
            String[] commandParts = initialCommand.split(" ");
            // Create a process builder and start the process
            ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
            Process process = processBuilder.start();
            // Create a buffered reader for the input stream and error stream as well as a
            // print writer for the output stream
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            PrintWriter writer = new PrintWriter(process.getOutputStream(), true);
            // Create a thread to read the output stream and print it to the terminal
            Thread outputThread = new Thread(() -> {
                String line;
                try {
                    while ((line = outputReader.readLine()) != null) {
                        terminalClass.appendText(line + "\n");
                    }
                } catch (Exception e) {
                    terminalClass.appendText("Error reading output: " + e.getMessage());
                }
            });
            // Create a thread to read the error stream and print it to the terminal
            Thread errorThread = new Thread(() -> {
                String line;
                try {
                    while ((line = errorReader.readLine()) != null) {
                        terminalClass.appendText("Error: " + line + "\n");
                    }
                } catch (Exception e) {
                    terminalClass.appendText("Error reading error output: " + e.getMessage());
                }
            });
            // Start the threads
            outputThread.start();
            errorThread.start();
            // Add an action listener to the text field for input to interact with the
            // program
            terminalClass.textField.addActionListener(e -> {
                // Handle input
                String userInput = terminalClass.textField.getText();
                if (userInput.trim().equals("stopinstance")) {
                    process.destroy();
                    terminalClass.appendText("Process stopped.");
                    terminalClass.textField.setText("");
                    return;
                } else if (!userInput.trim().isEmpty()) {
                    writer.println(userInput);
                    writer.flush();
                    terminalClass.textField.setText("");
                }
                if (!process.isAlive()) {
                    try {
                        terminalClass.runCommand(userInput);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            // Join the threads and destroy the process
            outputThread.join();
            errorThread.join();
            process.destroy();
            terminalClass.appendText("Process stopped.");
            terminalClass.textField.setText("");
            outputThread.interrupt();
            errorThread.interrupt();

        } catch (IOException | InterruptedException e) {
            terminalClass.appendText("Command execution error: " + e.getMessage());
        }
    }

    // Get the extention of a filed
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    // Method to compile and run a java file
    private void compileAndRunJava(File javaFile) throws IOException {
        // Get the root, bin directory, and lib directory
        Path projectRoot = Paths.get(CurrentUniversalPath).toAbsolutePath();
        Path binDir = projectRoot.resolve("bin");
        Path libPath = projectRoot.resolve("lib");
        boolean useBin = Files.exists(binDir);
        String outputDir = useBin ? binDir.toString() : javaFile.getParent();
        // Get the class name
        String className = javaFile.getName().replace(".java", "");
        // Get the compile files path as a string
        StringBuilder compileFiles = new StringBuilder();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(javaFile.getParentFile().toPath(), "*.java")) {
            for (Path path : stream) {
                compileFiles.append(path.toAbsolutePath().toString()).append(" ");
            }
        }
        // Create a string to create a classpath for the java libraries (.jar files)
        String libClasspath = "";
        if (Files.exists(libPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(libPath, "*.jar")) {
                StringBuilder libClasspathBuilder = new StringBuilder();
                for (Path lib : stream) {
                    libClasspathBuilder.append(lib.toString()).append(";");
                }
                libClasspath = libClasspathBuilder.toString();
            }
        }
        // Format the string based on whether the file is in the bin directory or not
        String compileCommand = useBin
                ? String.format("javac --release 17 -cp \"%s/*\" -d %s %s", libPath, binDir,
                        compileFiles.toString().trim())
                : String.format("javac --release 17 -cp \"%s/*\" %s", libPath, compileFiles.toString().trim());

        String mainClass = useBin ? className : className;
        String runCommand;
        // Format the run command based on the operating system
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            runCommand = String.format("java -cp \"%s;%s\" %s", outputDir, libClasspath, mainClass);
        } else {
            runCommand = String.format("java -cp \"%s:%s\" %s", outputDir, libClasspath, mainClass);
        }
        // Append the text to the terminal and compile both compileCommand and
        // runCommand
        terminalClass.appendText("Compiling Java file...");
        runCommand(compileCommand);
        runCommand(runCommand);
        // Let the user know that the file was compiled and executed successfully
        terminalClass.appendText("Java file compiled and executed.");
    }

    // Method to add a file to the tree
    private void addFileToTree(File file, DefaultMutableTreeNode parent) {
        // Create a new file node and add it to the parent node
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
        parent.add(fileNode);
        // Keep track of the file
        filePathMap.put(fileNode.hashCode(), file);
    }

    // Method to change the color of the tree
    public void changeTreeColor(Color color) {
        if (fileTree != null) {
            fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Color getTextNonSelectionColor() {
                    return color;
                }

                @Override
                public Color getTextSelectionColor() {
                    return color;
                }
            });
        } else {
            System.out.println("File tree is not initialized.");
        }
    }

    // Method to setup the tree listener
    public void setupTreeListener() {
        // Getting the settings and loading them
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("settings.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bgcolor = properties.getProperty("backgroundColor");
        fileTree.setBackground(getColorFromString(bgcolor));
        // Adding a tree selection listener to the file tree
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    return;
                }
                // Get the selected file from the file path map and open it in a new tab
                File selectedFile = filePathMap.get(selectedNode.hashCode());
                if (selectedFile != null) {
                    openFileInTab(selectedFile);
                }
            }
        });
    }

    // Method to open a file in a new tab
    @SuppressWarnings("static-access")
    private void openFileInTab(File file) {
        try {
            // If the file is already open in a tab open it in the current tab
            if (fileTabMap.containsKey(file)) {
                RSyntaxTextArea existingTextArea = (RSyntaxTextArea) fileTabMap.get(file);
                existingTextArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 10));
                int tabIndex = getTabIndexByTextArea(existingTextArea);
                if (tabIndex != -1) {
                    tabbedPane.setSelectedIndex(tabIndex);
                    return;
                }
            }
            // Load the settings and apply them to the text area
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
            // Read the file content and create a new text area
            String fileContent = new String(Files.readAllBytes(file.toPath()));
            RSyntaxTextArea textArea = new RSyntaxTextArea(fileContent);
            textArea.setFont(new Font("Segoe UI", Font.TYPE1_FONT, 10));
            textArea.setCodeFoldingEnabled(true);
            // If the settings are not null load the theme and apply it
            if (settings != null) {
                String theme = settings.getProperty("textAreaTheme", "Dark");
                String themePath = "Dark".equals(theme)
                        ? "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"
                        : "/org/fife/ui/rsyntaxtextarea/themes/default.xml";

                try (InputStream themeStream = getClass().getResourceAsStream(themePath)) {
                    if (themeStream != null) {
                        Theme theme1 = Theme.load(themeStream);
                        theme1.apply(textArea);
                    } else {
                        terminalClass.appendText("Theme file not found at path: " + themePath);
                    }
                } catch (IOException ex) {
                    terminalClass.appendText("Error loading theme: " + ex.getMessage());
                }
            }
            // Create a new scroll pane and add it to the tabbed pane
            RTextScrollPane scrollPane = new RTextScrollPane(textArea);
            scrollPane.setLineNumbersEnabled(true);
            tabbedPane.addTab(file.getName(), scrollPane);
            tabbedPane.setSelectedComponent(scrollPane);
            // Keep track of the file and text area
            fileTabMap.put(file, textArea);

        } catch (Exception e) {
            terminalClass.appendText("Unexpected error: " + e.getMessage());
        }
    }

    // Method to get the tab index of a text area
    private int getTabIndexByTextArea(JTextArea textArea) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(i);
            JViewport viewport = scrollPane.getViewport();
            Component view = viewport.getView();
            if (view == textArea) {
                return i;
            }
        }
        return -1;
    }

    // Method to open a file or folder
    public void openFileOrFolder(JPanel leftPanel) throws IOException {
        // Getting the settings
        Properties properties = new Properties();
        // Loading the settings
        try (FileInputStream input = new FileInputStream("settings.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Getting the background and foreground colors
        String bgcolor = properties.getProperty("backgroundColor");
        String fgcolor = properties.getProperty("foregroundColor");
        Color backgroundColor = getColorFromString(bgcolor);
        Color foregroundColor = getColorFromString(fgcolor);
        // Creating a file chooser and getting the user's selection
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int res = fileChooser.showOpenDialog(null);
        // If the user approves the selection
        if (res == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = fileChooser.getSelectedFile();
            // If the file tree and tree model are null
            if (fileTree == null || treeModel == null) {
                // Create a new tree model and tree
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(file.getName());
                treeModel = new DefaultTreeModel(root);
                fileTree = new JTree(treeModel);
                fileTree.setCellRenderer(createCustomTreeCellRenderer(backgroundColor, foregroundColor));
                fileTree.setBorder(null);
                // Create a scroll pane and add it to the left panel
                JScrollPane scrollPane = new JScrollPane(fileTree);
                scrollPane.setName("fileTreeScrollPane");
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setBounds(0, 40, leftPanel.getWidth(), leftPanel.getHeight() - 50);
                scrollPane.setBackground(backgroundColor);
                scrollPane.setBorder(null);
                // Add the scroll pane to the left panel
                leftPanel.add(scrollPane);
                leftPanel.setBorder(null);
            } else {
                // Otherwise set the user object of the root node to the file name
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
                root.setUserObject(file.getName());
                // Remove all children of the root node
                root.removeAllChildren();
            }
            // If the file is a directory add it to the tree as a directory otherwise add it
            // as a file
            if (file.isDirectory()) {
                addDirectoryToTree(file, (DefaultMutableTreeNode) treeModel.getRoot());
            } else {
                addFileToTree(file, (DefaultMutableTreeNode) treeModel.getRoot());
            }
            // Update the currentUniversalPath to keep track of the current directory
            CurrentUniversalPath = file.getAbsolutePath();
            treeModel.reload();
            setupTreeListener();
            // Remove all components from the left panel
            for (Component comp : leftPanel.getComponents()) {
                if (comp instanceof JScrollPane && "fileTreeScrollPane".equals(comp.getName())) {
                    leftPanel.remove(comp);
                    JScrollPane newScrollPane = new JScrollPane(fileTree);
                    newScrollPane.setBorder(null);
                    newScrollPane.setName("fileTreeScrollPane");
                    newScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    newScrollPane.setBounds(0, 40, leftPanel.getWidth(), leftPanel.getHeight() - 50);
                    newScrollPane.setBackground(backgroundColor);

                    leftPanel.add(newScrollPane);
                    break;
                }
            }
            // Repaint and revalidate the left panel
            leftPanel.revalidate();
            leftPanel.repaint();
            leftPanel.setBorder(null);
        }
    }

    // Method to retrieve color from string
    private Color getColorFromString(String colorString) {
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

    // Method to create a custom tree cell renderer
    private DefaultTreeCellRenderer createCustomTreeCellRenderer(Color backgroundColor, Color textColor) {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
                        hasFocus);
                if (comp instanceof JComponent) {
                    ((JComponent) comp).setOpaque(true);
                }
                if (selected) {
                    comp.setBackground(Color.GREEN);
                    setTextSelectionColor(textColor);
                } else {
                    comp.setBackground(backgroundColor);
                    setTextNonSelectionColor(textColor);
                }
                return comp;
            }
        };
    }

    // Method to create a new instance of the program
    public void newWindow() {
        new editor().setVisible(true);
    }

    // Method to exit the program
    public void exit() {
        System.exit(0);
    }
}
