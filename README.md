# 🧠 DocMindAI - Your AI Document Assistant

![Java](https://img.shields.io/badge/Frontend-Java_Swing-orange?style=flat-square&logo=java)
![Python](https://img.shields.io/badge/Backend-Python_Flask-blue?style=flat-square&logo=python)
![Gemini](https://img.shields.io/badge/AI_Model-Google_Gemini-green?style=flat-square&logo=google)

**DocMindAI** is a powerful, full-stack desktop application that allows users to instantly chat with their documents. Powered by the Google Gemini AI, it seamlessly reads text from PDFs, Word documents, Excel datasets, and CSV files, enabling users to ask context-aware questions and retrieve data without manual reading.

---

## 🌟 Key Features

*   **Chat with Documents**: Upload files and ask questions to get instant, accurate answers based purely on the document's content.
*   **Multi-Format Support**: Effortlessly processes `.pdf`, `.docx`, `.xlsx`, and `.csv` files.
*   **Foreign Language Support**: Fully supports non-English datasets and unicode characters (e.g., Hindi, Bengali).
*   **Clean Desktop Interface**: A modular, responsive Java Swing UI split into interactive chat panels and sidebars.

---

## 🏗️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Frontend** | Java (Swing) | Desktop graphical user interface (GUI) |
| **Backend** | Python (Flask) | RESTful API server handling uploads and requests |
| **AI Engine** | Google Gemini | `gemini-2.5-flash` model for intelligent text processing |
| **Libraries** | PyMuPDF, Pandas | Extracts text from PDF documents and spreadsheets |

---

## 🚀 Installation & Setup

### Prerequisites
Make sure you have the following installed on your machine:
*   [Java Development Kit (JDK) 11+](https://www.oracle.com/java/technologies/downloads/)
*   [Python 3.8+](https://www.python.org/downloads/)

### 1. Clone the Repository
```bash
git clone https://github.com/Himanshu-Singh11/DocMind.git
cd DocMind
```

### 2. Setup the Python Backend
Navigate to the backend directory, install the required libraries, and set up your environment variables.

```bash
cd backend
pip install -r requirements.txt
```

**Configure API Key:**
Create a `.env` file inside the `backend` folder and add your Google Gemini API key:
```env
GEMINI_API_KEY=your_google_gemini_api_key_here
```

### 3. Run the Backend Server
```bash
python3 app.py
```
*The server will start running on `http://127.0.0.1:5000`*

### 4. Run the Java Frontend
Open a **new terminal window**, navigate to the frontend folder, compile, and execute the application:

```bash
cd frontend
javac *.java
java Main
```

---

## 💡 Usage Guide
1. Launch both the backend server and the Java frontend.
2. Click the **Upload Document** button in the Java interface and select a compatible file.
3. Wait for the upload success message.
4. Type your question in the chat box at the bottom and hit **Send**.
5. Receive AI-generated insights instantly!

---

## 🤝 Contributing
Contributions are always welcome! Feel free to fork the repository, create a feature branch, and submit a pull request. 

---
*Built with ❤️ by Himanshu Singh*
