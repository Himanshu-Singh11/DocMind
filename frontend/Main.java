import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Enable global anti-aliasing for smooth, professional fonts
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set the Look and Feel to the system default for a more modern appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set modern look and feel. Using default.");
        }

        // Start the Python Backend automatically
        try {
            // Find absolute path to backend directory (assuming frontend and backend are siblings)
            java.io.File currentDir = new java.io.File(System.getProperty("user.dir"));
            java.io.File backendDir = new java.io.File(currentDir.getParentFile(), "backend");
            
            if (backendDir.exists() && backendDir.isDirectory()) {
                System.out.println("Starting Python backend in: " + backendDir.getAbsolutePath());
                ProcessBuilder pb = new ProcessBuilder("python3", "app.py");
                pb.directory(backendDir);
                
                // Inherit IO so we can see python errors in java console if needed
                // pb.inheritIO();
                Process backendProcess = pb.start();
                
                // Ensure backend is killed when Java app closes
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Shutting down Python backend...");
                    backendProcess.destroy();
                }));
            } else {
                System.err.println("Backend directory not found at: " + backendDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to start Python backend: " + e.getMessage());
            e.printStackTrace();
        }

        // Ensure UI creation happens on the Event Dispatch Thread
        // This is a Swing best practice to prevent UI freezes or glitches
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
