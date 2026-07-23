import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class SidebarPanel extends JPanel {
    private JButton newChatBtn;
    private JButton uploadBtn;
    private JButton clearBtn;
    private JButton exitBtn;
    private JLabel uploadedFileInfo;
    
    // Professional Dark Theme Colors
    private final Color SIDEBAR_BG = new Color(28, 30, 38);
    private final Color BTN_HOVER = new Color(255, 255, 255, 25); // Subtle translucent white
    private final Color BTN_PRESSED = new Color(255, 255, 255, 40);
    private final Color BTN_TEXT = new Color(240, 240, 245);
    private final Color ACCENT_COLOR = new Color(74, 144, 226);

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_BG);
        setBorder(new EmptyBorder(30, 20, 20, 20));
        setPreferredSize(new Dimension(260, 0));

        // High quality logo
        JLabel logoLabel = new JLabel("DocMind AI") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paintComponent(g);
            }
        };
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setForeground(ACCENT_COLOR);
        
        uploadedFileInfo = new JLabel(" ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        uploadedFileInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        uploadedFileInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadedFileInfo.setForeground(new Color(150, 150, 160));

        newChatBtn = createSidebarButton("New Chat");
        uploadBtn = createSidebarButton("Upload File");
        clearBtn = createSidebarButton("Clear Chat");
        exitBtn = createSidebarButton("Exit");

        exitBtn.addActionListener(e -> System.exit(0));

        add(logoLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(uploadedFileInfo);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(newChatBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(uploadBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(clearBtn);
        add(Box.createVerticalGlue()); 
        add(exitBtn);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, 10)); // Very dim if disabled
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else if (getModel().isPressed()) {
                    g2.setColor(BTN_PRESSED);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else if (getModel().isRollover()) {
                    g2.setColor(BTN_HOVER);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                // No background if not hovered - clean, transparent look
                
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setForeground(BTN_TEXT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Plain looks more elegant
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    public File openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Document or Dataset");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Datasets", "csv", "xlsx", "xls", "tsv", "json"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Documents", "pdf", "docx", "doc", "txt", "md"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Source Code", "py", "java", "c", "cpp", "js", "html", "css"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    public void setUploadedFileName(String name) {
        uploadedFileInfo.setText("File: " + name);
    }

    public JButton getUploadBtn() { return uploadBtn; }
    public JButton getClearBtn() { return clearBtn; }
    public JButton getNewChatBtn() { return newChatBtn; }
}
