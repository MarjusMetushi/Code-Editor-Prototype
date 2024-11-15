import javax.swing.SwingUtilities;

public class app {
    public static void main(String[] args) {
        // Start the Program
        SwingUtilities.invokeLater(() -> {
            editor editorInstance = new editor();
            editorInstance.setVisible(true);
        });
    }
}
