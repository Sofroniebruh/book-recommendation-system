### BookMind — AI-Powered Book Recommendation System

Discover your next favorite book with semantic search, emotion-aware re-ranking, and a polished Gradio UI. This project showcases applied NLP, vector search, and product-minded UX packaged for quick evaluation by recruiters and hiring teams.

---

### What this project demonstrates
- **End-to-end product**: A working app with a clean UI (`Gradio`) and a containerized deployable setup (`Docker`).
- **Semantic retrieval**: Uses `OpenAIEmbeddings` and `Chroma` to build a vector index from curated book descriptions for intent-based search.
- **Emotion-aware re-ranking**: Re-orders candidates based on dominant emotions (joy, surprise, anger, fear, sadness) to match the user's desired tone.
- **Practical data engineering**: Enriched metadata (categories, authors, thumbnails), preprocessed CSVs, and a tagged corpus used for retrieval.
- **Modern Python stack**: LangChain ecosystem, Gradio 5, Python 3.11.

---

### How it works (high level)
1. Loads book metadata from `books_with_emotions.csv` and augments thumbnails for display.
2. Builds a `Chroma` vector store from `tagged_descriptions.txt` using `OpenAIEmbeddings` (requires `OPENAI_API_KEY`).
3. For a user query, retrieves top-K semantically similar books and optionally filters by category.
4. Applies tone-specific sorting using precomputed emotion scores to produce final recommendations.
5. Presents results in a responsive gallery with cover images and succinct captions.

Key entrypoint: `app.py`

---

### UI Preview
- Title: BookMind — "Discover your next favorite book through AI-powered recommendations"
- Inputs: free-text query, category dropdown, emotional tone dropdown
- Output: 4x4 gallery of recommended books with covers and captions

---

### Tech stack
- Python 3.11
- Gradio 5
- LangChain (community/openai/text-splitters) + Chroma
- Pandas / NumPy
- Docker (slim image)

---

### Repository layout
- `app.py`: Gradio app, retrieval + re-ranking, and UI assembly
- `books_with_emotions.csv`: Book metadata with emotion scores and categories
- `tagged_descriptions.txt`: Corpus for semantic indexing (isbn13 + description tags)
- `cover-not-found.jpg`: Fallback cover image
- `Dockerfile`: Container build for production-like runs
- `requirements.txt`: Python dependencies
- Notebooks (`*.ipynb`): Exploration, vector search, sentiment analysis prototypes

---

### Prerequisites
- An OpenAI API key with embedding access.
  - Environment variable: `OPENAI_API_KEY`
- Docker installed and running on your machine.

---

### Quick start (Docker)
1) Create a `.env` file at the project root with your key:
```
OPENAI_API_KEY=your_key...
```

2) Build the container:
```
docker-compose up --build
```

3) Open the app:
```
http://localhost:7860
```

---

### Local (non-Docker) run
```
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
export OPENAI_API_KEY=your_key...
python app.py
```
App will be available at `http://127.0.0.1:7860`.

---

### Security and costs
- Uses OpenAI embeddings at runtime on container start (to index documents). Ensure your key has access and be mindful of token costs.

---


