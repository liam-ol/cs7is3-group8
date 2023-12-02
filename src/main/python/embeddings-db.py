import json
import sqlite3
from sentence_transformers import SentenceTransformer

model = SentenceTransformer('sentence-transformers/all-MiniLM-L12-v2')

conn = sqlite3.connect('../../../db/documents-bert.db')
read_cursor = conn.cursor()
write_cursor = conn.cursor()

iter_count = 0
read_cursor.execute("SELECT * FROM docs WHERE id NOT IN (SELECT id FROM embeddings);")
try:
    while True:
        batch = read_cursor.fetchmany(100)
        data = []
        for doc in batch:
            embedding = model.encode([f'{doc[1]} {doc[2]} {doc[3]} {doc[4]}'])
            data.append((doc[0], json.dumps(dict(embedding=embedding.tolist()))))
        write_cursor.executemany("INSERT INTO embeddings VALUES (?, ?)", data)
        iter_count += 1
        print(f"Finished iteration: {iter_count}")
except Exception as e:
    print(e)
finally:
    conn.commit()
    read_cursor.close()
    write_cursor.close()
    conn.close()