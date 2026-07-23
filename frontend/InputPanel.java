import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class InputPanel extends JPanel {
    private JTextArea inputArea;
    private JButton sendButton;
    private JButton attachButton;
    
    // Dark Theme Colors
    private final Color PANEL_BG = new Color(30, 32, 41);
    private final Color INPUT_BG = new Color(42, 45, 56);
    private final Color TEXT_COLOR = new Color(240, 240, 245);
    private final Color BORDER_COLOR = new Color(60, 64, 80);

    public InputPanel() {
        setLayout(new BorderLayout(15, 10));
        setBackground(PANEL_BG); 
        setBorder(new EmptyBorder(20, 30, 25, 30));

        inputArea = new JTextArea(3, 40) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputArea.setBorder(new EmptyBorder(12, 15, 12, 15));
        inputArea.setBackground(INPUT_BG);
        inputArea.setForeground(TEXT_COLOR);
        inputArea.setCaretColor(Color.WHITE); // White cursor for dark background
        
        // Wrap input area in a scroll pane with a custom rounded border
        JScrollPane scrollPane = new JScrollPane(inputArea) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_COLOR); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        scrollPane.setBorder(new EmptyBorder(1, 1, 1, 1)); 
        scrollPane.setBackground(INPUT_BG);
        scrollPane.getViewport().setBackground(INPUT_BG);

        sendButton = new JButton("Send") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(60, 64, 80)); // Disabled color
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(11, 95, 215)); 
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(50, 140, 250)); 
                } else {
                    g2.setColor(new Color(24, 119, 242)); 
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("Send")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("Send", x, y);
                
                g2.dispose();
            }
        };
        
        sendButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false); 
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(100, 0));

        attachButton = new JButton("📎") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(60, 64, 80, 100)); // Disabled dim color
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 64, 80)); 
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 84, 100)); 
                } else {
                    g2.setColor(new Color(42, 45, 56)); 
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("📎")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("📎", x, y);
                
                g2.dispose();
            }
        };
        
        attachButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        attachButton.setContentAreaFilled(false);
        attachButton.setBorderPainted(false); 
        attachButton.setFocusPainted(false);
        attachButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        attachButton.setPreferredSize(new Dimension(50, 0));

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume(); 
                    sendButton.doClick(); 
                }
            }
        });

        add(attachButton, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
    }
    
    public JButton getSendButton() {
        return sendButton;
    }

    public JButton getAttachButton() {
        return attachButton;
    }
    
    public JTextArea getInputArea() {
        return inputArea;
    }
}
