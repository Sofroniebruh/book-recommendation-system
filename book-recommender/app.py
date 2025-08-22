from typing import Optional, List
from flask import Flask, jsonify
import pandas as pd
from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import CharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
from langchain_core.documents import Document

app = Flask(__name__)

DOCUMENTS: List[Document] = []
DB: Optional[Chroma] = None
BOOKS: Optional[pd.DataFrame] = None


def load_data():
    global DOCUMENTS, BOOKS

    try:
        BOOKS = pd.read_csv("books_cleaned.csv")
        raw_text = TextLoader("tagged_descriptions.txt").load()
        text_splitter = CharacterTextSplitter(chunk_size=1, chunk_overlap=0, separator="\n")
        DOCUMENTS = text_splitter.split_documents(raw_text)
        print(f"Loaded {len(DOCUMENTS)} documents and {len(BOOKS)} books")
    except Exception as e:
        print(f"Error loading data: {e}")
        raise


def init_db():
    global DOCUMENTS, DB

    if not DOCUMENTS:
        load_data()

    if DB is None:
        try:
            DB = Chroma.from_documents(
                DOCUMENTS,
                embedding=OpenAIEmbeddings(),
            )
            print("Database initialized successfully")
        except Exception as e:
            print(f"Error loading DB: {e}")
            raise


def retrieve_semantic_recommendations(query: str, top_k: int = 10) -> pd.DataFrame:
    global DB, BOOKS

    if DB is None:
        init_db()

    if BOOKS is None:
        load_data()

    try:
        recs = DB.similarity_search(query, top_k)
        books_list = []

        for rec in recs:
            try:
                isbn = int(rec.page_content.strip('"').split()[0])
                books_list.append(isbn)
            except (ValueError, IndexError) as e:
                print(f"Error parsing ISBN from: {rec.page_content[:50]}... Error: {e}")
                continue

        filtered_books = BOOKS[BOOKS["isbn13"].isin(books_list)].head(top_k)
        return filtered_books

    except Exception as e:
        print(f"Error in retrieve_semantic_recommendations: {e}")
        return pd.DataFrame()


@app.route("/search/<string:search_query>", methods=["GET"])
def search(search_query):
    try:
        if not search_query:
            return jsonify({"error": "No search query provided"}), 400

        recommendations = retrieve_semantic_recommendations(search_query)

        if recommendations.empty:
            return jsonify({
                "query": search_query,
                "result": [],
                "message": "No recommendations found"
            }), 200

        return jsonify({
            "query": search_query,
            "count": len(recommendations),
            "result": recommendations.to_dict(orient="records"),
        }), 200
    except Exception as e:
        print(f"Search error: {e}")
        return jsonify({"error": f"Internal Server Error: {str(e)}"}), 500


@app.route("/health", methods=["GET"])
def health_check():
    """Health check endpoint to verify service status"""
    return jsonify({
        "status": "healthy",
        "documents_loaded": len(DOCUMENTS) if DOCUMENTS else 0,
        "books_loaded": len(BOOKS) if BOOKS is not None else 0,
        "db_initialized": DB is not None
    }), 200


if __name__ == "__main__":
    print("Loading data...")
    try:
        load_data()
        print("Done. Initializing DB...")
        init_db()
        print("Done. Starting Flask app...")
        app.run(port=5002, debug=True)
    except Exception as e:
        print(f"Failed to initialize: {e}")
