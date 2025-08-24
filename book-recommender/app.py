import pandas as pd
import numpy as np
from dotenv import load_dotenv

from langchain_community.document_loaders import TextLoader
from langchain_openai import OpenAIEmbeddings
from langchain_text_splitters import CharacterTextSplitter
from langchain_chroma import Chroma

import gradio as gr

load_dotenv()

books = pd.read_csv("books_with_emotions.csv")
books["large_thumbnail"] = books["thumbnail"] + "&fife=w800"
books["large_thumbnail"] = np.where(
    books["large_thumbnail"].isna(),
    "cover-not-found.jpg",
    books["large_thumbnail"],
)

raw_documents = TextLoader("tagged_descriptions.txt").load()
text_splitter = CharacterTextSplitter(separator="\n", chunk_size=1, chunk_overlap=0)
documents = text_splitter.split_documents(raw_documents)
db_books = Chroma.from_documents(documents, OpenAIEmbeddings())


def retrieve_semantic_recommendations(
        query: str,
        category: str = None,
        tone: str = None,
        initial_top_k: int = 50,
        final_top_k: int = 16,
) -> pd.DataFrame:
    recs = db_books.similarity_search(query, k=initial_top_k)
    books_list = [int(rec.page_content.strip('"').split()[0]) for rec in recs]
    book_recs = books[books["isbn13"].isin(books_list)].head(initial_top_k)

    if category != "All":
        book_recs = book_recs[book_recs["simple_categories"] == category].head(final_top_k)
    else:
        book_recs = book_recs.head(final_top_k)

    if tone == "Happy":
        book_recs.sort_values(by="joy", ascending=False, inplace=True)
    elif tone == "Surprising":
        book_recs.sort_values(by="surprise", ascending=False, inplace=True)
    elif tone == "Angry":
        book_recs.sort_values(by="anger", ascending=False, inplace=True)
    elif tone == "Suspenseful":
        book_recs.sort_values(by="fear", ascending=False, inplace=True)
    elif tone == "Sad":
        book_recs.sort_values(by="sadness", ascending=False, inplace=True)

    return book_recs


def recommend_books(
        query: str,
        category: str,
        tone: str
):
    if not query.strip():
        return []

    recommendations = retrieve_semantic_recommendations(query, category, tone)
    results = []

    for _, row in recommendations.iterrows():
        description = row["description"]
        truncated_desc_split = description.split()
        truncated_description = " ".join(truncated_desc_split[:30]) + "..."

        authors_split = row["authors"].split(";")
        if len(authors_split) == 2:
            authors_str = f"{authors_split[0]} and {authors_split[1]}"
        elif len(authors_split) > 2:
            authors_str = f"{', '.join(authors_split[:-1])}, and {authors_split[-1]}"
        else:
            authors_str = row["authors"]

        caption = f"**{row['title_and_subtitle']}**\n*by {authors_str}*\n\n{truncated_description}"
        results.append((row["large_thumbnail"], caption))
    return results


categories = ["All"] + sorted(books["simple_categories"].unique())
tones = ["All"] + ["Happy", "Surprising", "Angry", "Suspenseful", "Sad"]

custom_css = """
/* Global styles */
.gradio-container {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
}

/* Main container styling */
.main-container {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    margin: 1rem;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

/* Header styling */
.header-title {
    background: linear-gradient(45deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-align: center;
    font-size: 3rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-title {
    background: linear-gradient(45deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-align: center;
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-subtitle {
    text-align: center;
    color: #666;
    font-size: 1.2rem;
    margin-bottom: 2rem;
    font-weight: 300;
}

/* Input section styling */
.input-section {
    background: rgba(255, 255, 255, 0.7);
    border-radius: 15px;
    padding: 2rem;
    margin-bottom: 2rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: visible;
}

/* Button styling */
.submit-button {
    background: linear-gradient(45deg, #667eea, #764ba2) !important;
    border: none !important;
    border-radius: 25px !important;
    padding: 12px 30px !important;
    font-weight: 600 !important;
    font-size: 1.1rem !important;
    text-transform: uppercase !important;
    letter-spacing: 1px !important;
    transition: all 0.3s ease !important;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
}

.submit-button:hover {
    transform: translateY(-2px) !important;
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6) !important;
}

/* Gallery styling */
.gallery-container {
    background: rgba(255, 255, 255, 0.8);
    border-radius: 15px;
    padding: 2rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* Input field styling */
.gr-textbox, .gr-dropdown {
    border-radius: 10px !important;
    border: 2px solid rgba(102, 126, 234, 0.3) !important;
    transition: all 0.3s ease !important;
}

.gr-textbox:focus, .gr-dropdown:focus {
    border-color: #667eea !important;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

/* Fix dropdown positioning issues */
.gr-dropdown {
    position: relative !important;
    z-index: 1000 !important;
}

/* Dropdown menu positioning fix */
.gr-dropdown .absolute {
    position: absolute !important;
    z-index: 1001 !important;
    top: 100% !important;
    left: 0 !important;
    right: 0 !important;
    margin-top: 4px !important;
    max-height: 200px !important;
    overflow-y: auto !important;
}

/* Alternative dropdown menu selector */
.gr-dropdown [role="listbox"] {
    position: absolute !important;
    z-index: 1001 !important;
    top: 100% !important;
    left: 0 !important;
    right: 0 !important;
    margin-top: 4px !important;
    max-height: 200px !important;
    overflow-y: auto !important;
}

/* Additional dropdown container fix */
.gr-dropdown > div:last-child {
    position: absolute !important;
    z-index: 1001 !important;
    top: 100% !important;
    left: 0 !important;
    right: 0 !important;
    margin-top: 4px !important;
    max-height: 200px !important;
    overflow-y: auto !important;
}

/* Ensure dropdown options are visible */
.gr-dropdown-option {
    z-index: 1002 !important;
}

/* Gallery item styling */
.gallery-item {
    border-radius: 10px;
    overflow: hidden;
    transition: transform 0.3s ease;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.gallery-item:hover {
    transform: scale(1.05);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* Recommendation section title */
.recommendations-title {
    color: #333;
    font-size: 2rem;
    font-weight: 600;
    margin-bottom: 1rem;
    text-align: center;
}

/* Loading animation */
@keyframes pulse {
    0% { opacity: 1; }
    50% { opacity: 0.5; }
    100% { opacity: 1; }
}

.loading {
    animation: pulse 2s infinite;
}

/* Responsive design */
@media (max-width: 768px) {
    .header-title {
        font-size: 2rem;
    }

    .input-section {
        padding: 1rem;
    }

    .main-container {
        margin: 0.5rem;
        padding: 1rem;
    }
}
"""

with gr.Blocks(css=custom_css, theme=gr.themes.Soft()) as dashboard:
    with gr.Column(elem_classes="main-container"):
        gr.HTML("""
            <div class="header-title">BookMind</div>
            <div class="header-subtitle">Discover your next favorite book through AI-powered recommendations</div>
        """)

        with gr.Column(elem_classes="input-section"):
            gr.HTML("""
                        <div class="search-title">Tell us what you're looking for</div>
                    """)

            with gr.Row():
                with gr.Column(scale=3):
                    user_query = gr.Textbox(
                        label="Describe your ideal book",
                        placeholder="e.g., A heartwarming story about friendship and personal growth...",
                        lines=2,
                        max_lines=3
                    )

                with gr.Column(scale=1):
                    category_dropdown = gr.Dropdown(
                        choices=categories,
                        label="Category",
                        value="All",
                        # elem_classes="category-dropdown"
                    )

                with gr.Column(scale=1):
                    tone_dropdown = gr.Dropdown(
                        choices=tones,
                        label="Emotional Tone",
                        value="All",
                        # elem_classes="tone-dropdown"
                    )

            with gr.Row():
                with gr.Column():
                    submit_button = gr.Button(
                        "✨ Find My Perfect Books ✨",
                        variant="primary",
                        elem_classes="submit-button",
                        size="lg"
                    )

        with gr.Column(elem_classes="gallery-container"):
            gr.HTML('<div class="recommendations-title">Your Personalized Recommendations</div>')
            output = gr.Gallery(
                label="Recommended Books",
                columns=4,
                rows=4,
                height="auto",
                show_label=False,
                elem_classes="book-gallery"
            )

            gr.HTML("""
                <div style="text-align: center; margin-top: 1rem; color: #666; font-style: italic;">
                    💡 Click on any book cover to see more details
                </div>
            """)

    submit_button.click(
        fn=recommend_books,
        inputs=[user_query, category_dropdown, tone_dropdown],
        outputs=output
    )

    user_query.submit(
        fn=recommend_books,
        inputs=[user_query, category_dropdown, tone_dropdown],
        outputs=output
    )

if __name__ == "__main__":
    dashboard.launch(
        server_name="0.0.0.0",
        server_port=7860,
        share=False,
        show_error=True
    )