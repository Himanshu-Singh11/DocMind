import os
import fitz  # PyMuPDF
import docx  # python-docx
import pandas as pd
import json

def extract_pdf(file_path):
    """Extract text from a PDF file."""
    text = ""
    try:
        # Open the PDF file
        doc = fitz.open(file_path)
        # Iterate through each page and extract text
        for page in doc:
            text += page.get_text() + "\n"
        return text
    except Exception as e:
        return f"Error extracting PDF: {str(e)}"

def extract_docx(file_path):
    """Extract text from a DOCX file."""
    text = ""
    try:
        # Load the document
        doc = docx.Document(file_path)
        # Join all paragraphs into a single string
        for para in doc.paragraphs:
            text += para.text + "\n"
        return text
    except Exception as e:
        return f"Error extracting DOCX: {str(e)}"

def extract_txt(file_path):
    """Extract text from a TXT or Markdown file."""
    try:
        # Open the file and read its contents
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except Exception as e:
        return f"Error extracting text file: {str(e)}"

def extract_csv(file_path):
    """Extract text from a CSV dataset and include a summary."""
    try:
        # Read the CSV file into a DataFrame
        df = pd.read_csv(file_path)
        # Convert DataFrame to a string representation
        content = df.to_string()
        # Add summary to the top of the content
        summary = get_dataset_summary(df)
        return summary + "\n\n--- Full Data ---\n\n" + content
    except Exception as e:
        return f"Error extracting CSV: {str(e)}"

def extract_excel(file_path):
    """Extract text from an Excel file (XLSX/XLS) and include a summary."""
    try:
        # Read the Excel file into a DataFrame
        df = pd.read_excel(file_path)
        # Convert DataFrame to a string representation
        content = df.to_string()
        # Add summary to the top of the content
        summary = get_dataset_summary(df)
        return summary + "\n\n--- Full Data ---\n\n" + content
    except Exception as e:
        return f"Error extracting Excel: {str(e)}"

def extract_json(file_path):
    """Extract text from a JSON file."""
    try:
        # Open and load the JSON file
        with open(file_path, 'r', encoding='utf-8') as file:
            data = json.load(file)
        # Convert the JSON object back to a formatted string
        return json.dumps(data, indent=4)
    except Exception as e:
        return f"Error extracting JSON: {str(e)}"

def extract_code(file_path):
    """Extract text from a source code file."""
    try:
        # Treat source code as plain text
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except Exception as e:
        return f"Error extracting Code file: {str(e)}"

def get_dataset_summary(df):
    """
    Generate a summary of a pandas DataFrame.
    Returns details like rows, columns, data types, missing values, etc.
    """
    try:
        # Gather basic dataset statistics
        num_rows = len(df)
        num_cols = len(df.columns)
        col_names = ", ".join(df.columns.tolist())
        missing_values = df.isnull().sum().sum()
        duplicate_rows = df.duplicated().sum()
        first_5_rows = df.head(5).to_string()
        
        # Format the summary nicely
        summary = (
            f"Dataset Summary:\n"
            f"- Number of rows: {num_rows}\n"
            f"- Number of columns: {num_cols}\n"
            f"- Column names: {col_names}\n"
            f"- Data types:\n{df.dtypes.to_string()}\n"
            f"- Total missing values: {missing_values}\n"
            f"- Total duplicate rows: {duplicate_rows}\n\n"
            f"First 5 rows:\n{first_5_rows}"
        )
        return summary
    except Exception as e:
        return f"Error generating summary: {str(e)}"

def detect_file_type(filename):
    """Detect the file type based on its extension."""
    if '.' not in filename:
        return "unknown"
    return filename.rsplit('.', 1)[1].lower()

def process_file(file_path, extension):
    """
    Route the file to the correct extraction function based on its extension.
    This function is called directly from app.py.
    """
    if extension == 'pdf':
        return extract_pdf(file_path)
    elif extension in ['docx', 'doc']:
        return extract_docx(file_path)
    elif extension in ['txt', 'md']:
        return extract_txt(file_path)
    elif extension == 'csv' or extension == 'tsv':
        return extract_csv(file_path)
    elif extension in ['xlsx', 'xls']:
        return extract_excel(file_path)
    elif extension == 'json':
        return extract_json(file_path)
    elif extension in ['py', 'java', 'c', 'cpp', 'js', 'html', 'css']:
        return extract_code(file_path)
    else:
        return "Unsupported file type."
