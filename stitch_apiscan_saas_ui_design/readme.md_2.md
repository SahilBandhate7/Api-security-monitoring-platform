📄 AI Contract Analysis System

An AI-powered web application that analyzes legal documents (PDF/DOCX) to generate structured insights, including summaries, clause detection, and risk analysis, with export options in JSON and professional PDF reports.

🚀 Features
📂 Upload legal documents (PDF / DOCX)
🧠 AI-based contract understanding using LLM (Ollama)
📊 Clause extraction & structured analysis
⚠️ Risk detection (High / Medium / Low)
📝 Executive summary generation
📄 Professional multi-page PDF report generation
📦 JSON structured output for integration
⚡ Optimized for near real-time responses
🏗️ Tech Stack
Backend
FastAPI
Uvicorn
LangChain
Ollama (LLM + Embeddings)
ChromaDB (Vector Database)
ReportLab (PDF generation)
PyTesseract (OCR support)
Frontend
React (Vite)
Tailwind CSS
📁 Project Structure
backend/
│── app/
│   ├── main.py
│   ├── api/
│   │   └── routes.py
│   ├── ai/
│   │   ├── vector_store.py
│   │   ├── qa_chain.py
│   │   ├── clause_detector.py
│   │   ├── risk_analyzer.py
│   │   └── contract_summary.py
│
│── knowledge_base/
│   └── ingest.py
│
│── data/
│   ├── uploads/
│   ├── output.pdf
│   └── logo.png

frontend/
│── src/
│── public/
⚙️ Setup Instructions
1️⃣ Clone Repository
git clone <your-repo-url>
cd uci
2️⃣ Backend Setup
cd backend
python -m venv .venv
.venv\Scripts\activate
Install Dependencies
pip install fastapi uvicorn python-multipart reportlab pytesseract pillow langchain langchain-community langchain-ollama chromadb
3️⃣ Install & Run Ollama

Download Ollama from:
👉 https://ollama.com

Run models:

ollama pull llama3
ollama pull nomic-embed-text
4️⃣ Install Tesseract OCR (Optional but recommended)

Download:
👉 https://github.com/tesseract-ocr/tesseract

Set path in code:

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
5️⃣ Run Backend
uvicorn app.main:app --reload

Open:
👉 http://127.0.0.1:8000/docs

6️⃣ Frontend Setup
cd frontend
npm install
npm run dev

Open:
👉 http://localhost:5173

🔄 API Endpoints
Endpoint	Method	Description
/upload	POST	Upload document
/ask	POST	Ask questions
/analyze	POST	Full contract analysis
/risk-analysis	POST	Risk detection
/summary	POST	Generate summary
/download/pdf	GET	Download PDF report
/download/json	GET	Download structured output
📊 Output Example
JSON
{
  "summary": "...",
  "clauses": [
    {
      "title": "Confidentiality",
      "description": "...",
      "risk_level": "High"
    }
  ]
}
PDF Report Includes:
Cover page
Executive Summary
Clause Analysis Table
Risk Insights
Professional formatting with branding
⚡ Performance Optimizations
Chunk-based document processing
Vector search (ChromaDB)
Limited context retrieval (top-k)
Fast LLM inference via Ollama
⚠️ Known Limitations
Complex tables in PDFs may not extract perfectly
Multi-column legal layouts may need OCR
Requires local LLM setup (Ollama)
🔮 Future Enhancements
JWT Authentication
Cloud deployment (Render / AWS)
Multi-document comparison
Clause recommendation engine
Legal compliance scoring
Advanced UI dashboard