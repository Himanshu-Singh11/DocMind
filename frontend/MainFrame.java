import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    
    private SidebarPanel sidebarPanel;
    private ChatPanel chatPanel;
    private InputPanel inputPanel;
    private StatusBar statusBar;

    public MainFrame() {
        setTitle("DocMind AI - Intelligent Document & Dataset Assistant");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        sidebarPanel = new SidebarPanel();
        chatPanel = new ChatPanel();
        inputPanel = new InputPanel();
        statusBar = new StatusBar();

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(chatPanel, BorderLayout.CENTER);
        centerContainer.add(inputPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);
        add(centerContainer, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        // Check backend status on startup and then poll every 5 seconds
        checkBackendConnection();
        
        Timer healthTimer = new Timer(5000, e -> {
            new Thread(() -> {
                checkBackendConnection();
            }).start();
        });
        healthTimer.start();
        
        setupInteractions();
    }
    
    /**
     * Checks if Flask backend is running and Gemini is configured, updating the Status Bar.
     */
    private void checkBackendConnection() {
        String healthResponse = ApiClient.checkHealthStatus();
        
        if (healthResponse == null) {
            // Case 1: Backend not running
            SwingUtilities.invokeLater(() -> statusBar.setSystemStatus("🔴 Backend: Offline"));
        } else if (healthResponse.contains("\"status\": \"warning\"") || healthResponse.contains("\"status\":\"warning\"")) {
            // Case 2: Backend running but Gemini API Key is missing
            SwingUtilities.invokeLater(() -> statusBar.setSystemStatus("🟡 Backend: Connected | AI: Gemini (Missing API Key)"));
        } else if (healthResponse.contains("\"status\": \"success\"") || healthResponse.contains("\"status\":\"success\"")) {
            // Case 3: Backend and Gemini both ready
            SwingUtilities.invokeLater(() -> statusBar.setSystemStatus("🟢 Backend: Connected | AI: Gemini | Model: gemini-2.5-flash"));
        }
    }
    
    /**
     * Wires up UI buttons to actual backend API calls.
     */
    private void setupInteractions() {
        // Clear Chat
        sidebarPanel.getClearBtn().addActionListener(e -> {
            if (chatPanel.getMessageCount() > 1) {
                chatPanel.clearChat();
                chatPanel.addMessage("Chat cleared. How can I assist you today?", false);
            }
        });
        
        // New Chat
        sidebarPanel.getNewChatBtn().addActionListener(e -> {
            if (chatPanel.getMessageCount() > 1) {
                chatPanel.clearChat();
                chatPanel.addMessage("Started a new session. Welcome! How can I assist you with your documents or data today?", false);
            }
        });
        
        // Upload File Action
        java.awt.event.ActionListener uploadAction = e -> {
            File selectedFile = sidebarPanel.openFileChooser();
            if (selectedFile != null) {
                // Show a loading message in chat
                chatPanel.addMessage("Uploading " + selectedFile.getName() + "...", false);
                
                // Call backend API in a background thread to prevent UI freezing
                new Thread(() -> {
                    String response = ApiClient.uploadFile(selectedFile);
                    SwingUtilities.invokeLater(() -> {
                        if (response.contains("\"status\":\"success\"") || response.contains("\"status\": \"success\"")) {
                            sidebarPanel.setUploadedFileName(selectedFile.getName());
                            statusBar.setUploadedFile(selectedFile.getName());
                            checkBackendConnection(); // Refresh status
                            chatPanel.addMessage("File uploaded and processed successfully! Ask me anything about it.", false);
                        } else {
                            chatPanel.addMessage("Upload failed: " + extractMessageFromJson(response), false);
                        }
                    });
                }).start();
            }
        };
        
        sidebarPanel.getUploadBtn().addActionListener(uploadAction);
        inputPanel.getAttachButton().addActionListener(uploadAction);
        
        // Send Question
        inputPanel.getSendButton().addActionListener(e -> {
            String text = inputPanel.getInputArea().getText().trim();
            if (!text.isEmpty()) {
                chatPanel.addMessage(text, true); // Display user question
                inputPanel.getInputArea().setText(""); // Clear input
                
                // Call backend API in a background thread
                new Thread(() -> {
                    // Pre-check for Case 2: If Gemini API key is missing, show a specific message immediately
                    String health = ApiClient.checkHealthStatus();
                    if (health != null && (health.contains("\"status\": \"warning\"") || health.contains("\"status\":\"warning\""))) {
                        SwingUtilities.invokeLater(() -> {
                            chatPanel.addMessage("Gemini API Key is not configured. Please set GEMINI_API_KEY environment variable.", false);
                            checkBackendConnection(); // Refresh status bar
                        });
                        return; // Stop here, don't ask Gemini
                    }
                    
                    SwingUtilities.invokeLater(() -> chatPanel.addMessage("Thinking...", false));
                    
                    String response = ApiClient.askQuestion(text);
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.removeLastMessage(); // Remove "Thinking..."
                        if (response.contains("\"status\":\"error\"") || response.contains("\"status\": \"error\"")) {
                            String errorMsg = extractMessageFromJson(response);
                            if (errorMsg.contains("GEMINI_API_KEY")) {
                                JOptionPane.showMessageDialog(MainFrame.this, "Gemini API Key not configured.", "API Error", JOptionPane.ERROR_MESSAGE);
                            }
                            chatPanel.addMessage("Error: " + errorMsg, false);
                            checkBackendConnection(); // Refresh status
                        } else {
                            checkBackendConnection(); // Refresh status
                            String answer = extractAnswerFromJson(response);
                            chatPanel.addMessage(answer, false, true);
                        }
                    });
                }).start();
            }
        });
    }
    
    /**
     * Simple helper to extract "message" field from JSON without external libraries.
     */
    private String extractMessageFromJson(String json) {
        try {
            int msgIndex = json.indexOf("\"message\"");
            if (msgIndex == -1) return "Unknown error.";
            int start = json.indexOf("\"", msgIndex + 9) + 1;
            int end = json.indexOf("\"", start);
            return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
        } catch (Exception e) {
            return "Failed to parse error response.";
        }
    }
    
    /**
     * Simple helper to extract "answer" field from JSON without external libraries.
     */
    private String extractAnswerFromJson(String json) {
        try {
            int ansIndex = json.indexOf("\"answer\"");
            if (ansIndex == -1) return "No answer field found.";
            
            // Find the colon after the key
            int colonIndex = json.indexOf(":", ansIndex);
            
            // Find the start quote of the value
            int start = json.indexOf("\"", colonIndex) + 1;
            
            // Find the end quote, ignoring escaped quotes
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') {
                    break;
                }
                end++;
            }
            
            String answer = json.substring(start, end);
            
            // Clean up escaped characters
            return answer.replace("\\n", "\n")
                         .replace("\\\"", "\"")
                         .replace("\\\\", "\\");
        } catch (Exception e) {
            return "Failed to parse answer.";
        }
    }
}
