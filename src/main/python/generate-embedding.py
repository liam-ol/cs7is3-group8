import sys
import json
from sentence_transformers import SentenceTransformer

if __name__ == '__main__':

    model_name = sys.argv[1]
    query = sys.argv[2]

    if model_name == 'miniLM':
        model = SentenceTransformer('sentence-transformers/all-MiniLM-L12-v2')
    elif model_name == 'bert':
        model = SentenceTransformer('sentence-transformers/all-distilroberta-v1')
    else:
        model = None

    if model:
        embedding = model.encode([query])
        for dim in embedding.tolist()[0]:
            print(dim)