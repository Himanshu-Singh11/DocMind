import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ApiClient {

    private static final String BASE_URL = "http://127.0.0.1:5000";
    
    /**
     * Checks the health of both the Flask backend and Ollama.
     * Returns the JSON string response from GET /health.
     */
    public static String checkHealthStatus() {
        try {
            java.net.URI uri = java.net.URI.create(BASE_URL + "/health");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000); // 3 seconds timeout
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                return readResponse(conn);
            }
        } catch (Exception e) {
            // Connection failed, meaning backend is offline
        }
        return null; // Return null if the backend is down
    }

    /**
     * Sends the user's question to the /ask endpoint using a standard JSON POST request.
     */
    public static String askQuestion(String question) {
        try {
            java.net.URI uri = java.net.URI.create(BASE_URL + "/ask");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // Configure the connection for a JSON POST request
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); // Allow sending data
            
            // Create the simple JSON payload manually
            // We escape double quotes inside the question string just to be safe
            String escapedQuestion = question.replace("\"", "\\\"");
            String jsonInputString = "{\"question\": \"" + escapedQuestion + "\"}";
            
            // Write the JSON data to the output stream
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read the response from the server
            return readResponse(conn);
            
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"Failed to connect to backend.\"}";
        }
    }

    /**
     * Uploads a file to the /upload endpoint using multipart/form-data.
     * This is slightly more complex because it mimics an HTML file upload form.
     */
    public static String uploadFile(File file) {
        String boundary = "---DocMindBoundary" + System.currentTimeMillis();
        String CRLF = "\r\n"; // Carriage Return Line Feed

        try {
            java.net.URI uri = java.net.URI.create(BASE_URL + "/upload");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            try (OutputStream output = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {
                 
                // Write the boundary and Content-Disposition headers for the file
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append(CRLF);
                writer.append("Content-Type: application/octet-stream").append(CRLF);
                writer.append(CRLF).flush();
                
                // Write the actual file bytes directly to the output stream
                Files.copy(file.toPath(), output);
                output.flush();
                
                // End the multipart request
                writer.append(CRLF).append("--" + boundary + "--").append(CRLF).flush();
            }
            
            return readResponse(conn);
            
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"Failed to upload file.\"}";
        }
    }

    /**
     * Helper method to read the HTTP response (both successful and error streams).
     */
    private static String readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream is = (status < 400) ? conn.getInputStream() : conn.getErrorStream();
        
        if (is == null) {
            return "{\"status\":\"error\",\"message\":\"No response from server.\"}";
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response.toString();
    }
}
