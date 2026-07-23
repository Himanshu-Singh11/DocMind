import os
import requests
from dotenv import load_dotenv
from flask import Flask, request, jsonify

# Load environment variables from .env file
load_dotenv()
from werkzeug.utils import secure_filename
from file_handler import process_file
from gemini_client import generate_answer

# Initialize Flask application
app = Flask(__name__)
# Ensure JSON responses don't escape unicode characters (like Bengali text)
app.json.ensure_ascii = False
app.config['JSON_AS_ASCII'] = False

# Configure upload folder
UPLOAD_FOLDER = '../uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Global variable to store extracted text during the session
# This ensures we store only one file at a time without a database.
session_context = {
    "extracted_text": "",
    "current_file": None,
    "file_type": None
}

@app.route('/', methods=['GET'])
def home():
    """
    Check if the backend is running.
    """
    return jsonify({"status": "success", "message": "Backend Running"}), 200

@app.route('/upload', methods=['POST'])
def upload_file():
    """
    Handle file uploads, detect type, extract content, and store in memory.
    """
    if 'file' not in request.files:
        return jsonify({"status": "error", "message": "No file part in the request."}), 400
    
    file = request.files['file']
    
    if file.filename == '':
        return jsonify({"status": "error", "message": "No selected file."}), 400
    
    if file:
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        
        # Store file in uploads/
        file.save(filepath)
        
        # Detect file type automatically
        file_extension = filename.rsplit('.', 1)[1].lower() if '.' in filename else ''
        
        try:
            # Extract content based on file type
            extracted_text = process_file(filepath, file_extension)
            
            # If unsupported file was passed
            if extracted_text == "Unsupported file type.":
                return jsonify({"status": "error", "message": "Unsupported file format."}), 400
                
            # Replace previous memory with the new file
            session_context["extracted_text"] = extracted_text
            session_context["current_file"] = filename
            session_context["file_type"] = file_extension
            
            return jsonify({
                "status": "success",
                "filename": filename,
                "file_type": file_extension,
                "message": "File uploaded successfully."
            }), 200
        except Exception as e:
            return jsonify({"status": "error", "message": f"Failed to process file: {str(e)}"}), 500

@app.route('/ask', methods=['POST'])
def ask_question():
    """
    Receive a question, combine it with context, and generate an answer.
    """
    data = request.get_json()
    
    if not data or 'question' not in data:
        return jsonify({"status": "error", "message": "No question provided."}), 400
    user_question = data['question']
    context = session_context.get("extracted_text", "")
    file_type = session_context.get("file_type", "")
    
    # Check for simple dataset questions
    dataset_extensions = ['csv', 'xlsx', 'xls', 'tsv', 'json']
    simple_keywords = ['rows', 'columns', 'column names', 'data types', 'missing values', 'duplicate', 'first 5', 'summary', 'average', 'maximum', 'minimum']
    
    if file_type in dataset_extensions and any(kw in user_question.lower() for kw in simple_keywords):
        # Our file_handler prepends the summary before "--- Full Data ---"
        # We can extract and return just the summary directly without calling Gemini.
        summary_part = context.split("--- Full Data ---")[0].strip()
        return jsonify({
            "status": "success",
            "answer": summary_part
        }), 200
    
    try:
        warning_msg = ""
        # Truncate context if it is too large for the model (e.g., > 20,000 chars)
        if len(context) > 20000:
            context = context[:20000]
            warning_msg = "\n\n(Warning: Only a portion of the uploaded document was analyzed because it exceeded the model's context limit.)"
            
        # Send to Gemini
        answer = generate_answer(context, user_question)
        
        # If Gemini client returned an error string, bubble it up as an error status
        if answer.startswith("Error:"):
            return jsonify({"status": "error", "message": answer}), 500
            
        return jsonify({
            "status": "success",
            "answer": answer + warning_msg
        }), 200
        
    except Exception as e:
        return jsonify({"status": "error", "message": f"Failed to communicate with AI: {str(e)}"}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """
    Health Check API to verify Flask and Gemini configuration.
    """
    try:
        # Check if Gemini API key exists in environment variables
        api_key = os.environ.get("GEMINI_API_KEY")
        
        if api_key and len(api_key.strip()) > 0:
            return jsonify({
                "status": "success",
                "backend": "running",
                "ai": "Gemini",
                "model": "gemini-2.5-flash"
            }), 200
        else:
            return jsonify({
                "status": "warning",
                "backend": "running",
                "ai": "Gemini",
                "message": "Gemini API Key not configured."
            }), 200
            
    except Exception as e:
        return jsonify({
            "status": "error",
            "backend": "running",
            "ai": "Gemini",
            "message": f"Health check failed: {str(e)}"
        }), 500

if __name__ == '__main__':
    app.run(debug=True, port=5000)
