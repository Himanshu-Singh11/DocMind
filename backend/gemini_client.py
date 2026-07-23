import os
from google import genai
from google.genai import types

def generate_answer(context, question):
    """
    Connects to Google Gemini API using the GEMINI_API_KEY environment variable.
    Combines the uploaded document context and user question into a prompt.
    """
    
    # Check for API key
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        return "Error: GEMINI_API_KEY environment variable not found. Please set it before running the server."
        
    try:
        # Initialize the Gemini client
        # The client automatically picks up the GEMINI_API_KEY environment variable
        client = genai.Client()
        
        # Build the structured prompt
        if context:
            prompt = f"""
You are an AI Document Assistant.

Use the uploaded document or dataset as the primary source.

Context:
{context}

User Question:
{question}

Instructions:
- Answer using the uploaded content whenever possible.
- If the answer is not present in the uploaded content, clearly mention: "I could not find this information in the uploaded file."
- Then continue with "General Knowledge:" and answer using your general knowledge.
- Never claim information exists in the uploaded file when it does not.
- Keep answers clear and use plain text formatting. DO NOT use markdown formatting like **, *, or # because the UI cannot render it. Use regular new lines instead.
"""
        else:
            # Case 1: General Gemini Chat (No file uploaded)
            prompt = f"""
You are a helpful AI Assistant.

User Question:
{question}

Instructions:
- Answer the user's question clearly and concisely.
- Keep answers clear and use plain text formatting. DO NOT use markdown formatting like **, *, or # because the UI cannot render it. Use regular new lines instead.
"""
        
        # Call the latest Gemini model (gemini-2.5-flash)
        response = client.models.generate_content(
            model='gemini-2.5-flash',
            contents=prompt,
        )
        
        # Ensure we got a response back
        if response and response.text:
            return response.text.strip()
        else:
            return "Error: Gemini returned an empty response."
            
    except Exception as e:
        # Catch network errors, authentication errors, or API failures
        return f"Error: Failed to connect to Gemini API. Details: {str(e)}"
