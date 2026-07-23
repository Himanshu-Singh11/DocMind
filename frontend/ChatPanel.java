import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ChatPanel extends JPanel {
    private JPanel messagesContainer;
    private JScrollPane scrollPane;
    
    // Dark Theme Colors
    private final Color PANEL_BG = new Color(30, 32, 41);
    private final Color AI_BUBBLE_BG = new Color(42, 45, 56);
    private final Color USER_BUBBLE_BG = new Color(24, 119, 242);
    private final Color TEXT_COLOR = new Color(240, 240, 245);
    private final Color LABEL_COLOR = new Color(150, 150, 160);

    public ChatPanel() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BG);

        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setBackground(PANEL_BG);
        messagesContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        scrollPane = new JScrollPane(messagesContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PANEL_BG);

        add(scrollPane, BorderLayout.CENTER);
        
        addMessage("Welcome to DocMind AI! How can I assist you with your documents or data today?", false);
    }

    class BubblePanel extends JPanel {
        private Color bgColor;
        
        public BubblePanel(Color bgColor) {
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16)); 
            g2.dispose();
        }
    }

    // Default method for system messages or user messages
    public void addMessage(String text, boolean isUser) {
        addMessage(text, isUser, false);
    }

    public void addMessage(String text, boolean isUser, boolean showCopyButton) {
        JPanel messageBlock = new JPanel();
        messageBlock.setLayout(new BoxLayout(messageBlock, BoxLayout.Y_AXIS));
        messageBlock.setBackground(PANEL_BG);
        messageBlock.setBorder(new EmptyBorder(0, 0, 20, 0)); 
        
        JLabel nameLabel = new JLabel(isUser ? "You" : "DocMind AI") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(LABEL_COLOR);
        nameLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(0, 5, 5, 5));
        
        JTextArea messageArea = new JTextArea(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageArea.setOpaque(false);
        messageArea.setBorder(new EmptyBorder(12, 16, 12, 16));

        int maxWidth = 600;
        FontMetrics fm = messageArea.getFontMetrics(messageArea.getFont());
        
        int width = 0;
        for (String line : text.split("\n")) {
            width = Math.max(width, fm.stringWidth(line));
        }
        
        int finalWidth = Math.min(width + 35, maxWidth); 
        messageArea.setSize(new Dimension(finalWidth, Short.MAX_VALUE));
        int finalHeight = messageArea.getPreferredSize().height;
        
        messageArea.setPreferredSize(new Dimension(finalWidth, finalHeight));

        BubblePanel bubble = new BubblePanel(isUser ? USER_BUBBLE_BG : AI_BUBBLE_BG);
        messageArea.setForeground(TEXT_COLOR);
        
        bubble.setLayout(new BorderLayout());
        bubble.add(messageArea, BorderLayout.CENTER);
        
        JPanel bubbleAligner = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        bubbleAligner.setBackground(PANEL_BG);
        bubbleAligner.add(bubble);
        bubbleAligner.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        
        messageBlock.add(nameLabel);
        messageBlock.add(bubbleAligner);
        
        if (!isUser && showCopyButton) {
            JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actionBar.setBackground(PANEL_BG);
            actionBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel copyBtn = new JLabel("📋 Copy") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    super.paintComponent(g);
                }
            };
            copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            copyBtn.setForeground(LABEL_COLOR);
            copyBtn.setBorder(new EmptyBorder(5, 5, 0, 0));
            copyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            copyBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                    copyBtn.setText("✅ Copied");
                    new Timer(2000, evt -> copyBtn.setText("📋 Copy")).start();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    copyBtn.setForeground(TEXT_COLOR);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    copyBtn.setForeground(LABEL_COLOR);
                }
            });
            
            actionBar.add(copyBtn);
            messageBlock.add(actionBar);
        }

        messagesContainer.add(messageBlock);
        messagesContainer.revalidate();
        messagesContainer.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    public void clearChat() {
        messagesContainer.removeAll();
        messagesContainer.revalidate();
        messagesContainer.repaint();
    }
    
    public void removeLastMessage() {
        if (messagesContainer.getComponentCount() > 0) {
            messagesContainer.remove(messagesContainer.getComponentCount() - 1);
            messagesContainer.revalidate();
            messagesContainer.repaint();
        }
    }

    
    public int getMessageCount() {
        return messagesContainer.getComponentCount();
    }
}
