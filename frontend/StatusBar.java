import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel systemStatus;
    private JLabel uploadedFile;

    public StatusBar() {
        // Use a FlowLayout aligned to the left with some padding
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        setBackground(new Color(245, 245, 245)); // Light gray background
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY)); // Top border

        // Initialize status labels
        systemStatus = new JLabel("Checking status...");
        uploadedFile = new JLabel("File: None");

        // Set font for readability
        Font statusFont = new Font("SansSerif", Font.PLAIN, 12);
        systemStatus.setFont(statusFont);
        uploadedFile.setFont(statusFont);

        // Add to the panel
        add(systemStatus);
        add(uploadedFile);
    }

    // Methods to update statuses from other parts of the app
    public void setSystemStatus(String status) {
        systemStatus.setText(status);
    }

    public void setUploadedFile(String filename) {
        uploadedFile.setText("File: " + filename);
    }
}
