### This is a Java-based GUI code editor prototype designed for developers and learners.
### It supports popular programming languages, offers syntax highlighting, auto-closing brackets, 
### and many essential features for a seamless coding experience.

## In this project I used RSyntaxTextArea (https://github.com/bobbylight/RSyntaxTextArea.git) for the text area dedicated to writing text.

# Core Features
  - Support for JavaScript, Java, C++, C, Python, and text editing.
  - Syntax highlighting and language-specific syntax support.
  - Code folding for improved code readability.
# User Interface
  - Predefined themes and customizable appearance.
  - Tree-like structure for file and folder navigation.
  - Tabbed interface with controls for managing multiple files.
  - Quick access panels for search, find-and-replace, and customization.
  - File Management
  - New, Save, Save As, Open File, and Open Folder functionality.
# Code Execution
  - Run button to execute code for supported languages.
  - Integrated terminal for executing code in the current folder.
  - Additional Utilities
  - Auto-closing brackets and basic editor controls (Undo, Redo, Cut, Copy, Paste).
  - Help and About sections for assistance and documentation.

### Requirements
- Java 8 or later
- RSyntaxTextArea (https://github.com/bobbylight/RSyntaxTextArea.git) library (bundled in this project as a JAR file)

### Installation
- Clone the repository https://github.com/MarjusMetushi/Code-Editor-Prototype.git
- cd Code-Editor_prototype
- javac -cp "path/.../RSyntaxTextArea.jar" -d bin src/**/*.java
- java -cp "bin:path/.../RSyntaxTextArea.jar" app

### Usage
- Open a file from the supported language with one of the following extensions (.java / .py / .cpp / .c / .js)
- Click the run button or use the command line.Navigate to the directory containing your project files using the cd command followed by the path to the directory.

### Contributions are welcome!
# Follow these steps if you are interested in contributing:
- Fork the repository
- Clone your fork
- cd path/to/fork
- Set upstream to keep your fork updated with the original repository
- Create a feature branch
- Make changes and test them
- Commit the changes
- Push to your fork
- Open a pull request
- Done! You have successfully contributed!

