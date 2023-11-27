from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer

app = Flask(__name__)
model = SentenceTransformer('sentence-transformers/all-MiniLM-L12-v2')

@app.route("/embedding", methods=['POST'])
def generate_embedding():
    text = request.json['text']
    embedding = model.encode([text])
    return jsonify(embedding=embedding.tolist()), 200