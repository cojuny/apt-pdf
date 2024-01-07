from sentence_transformers import SentenceTransformer, util
from ResultQueue import output_result
class SemanticSearch:

    def __init__(self) -> None:
        self.model = SentenceTransformer('all-MiniLM-L6-v2')

    def search(self,id: str, query: str, sentences: dict, threshold: int) -> None:
        query_embedding = self.model.encode(query, convert_to_tensor=True)
        for start_index, sentence in sentences.items():
            sentence_embedding = self.model.encode(sentence, convert_to_tensor=True)
            result = self.query_textual_similarity(query_embedding=query_embedding, sentence_embedding=sentence_embedding)
            if (result >= threshold ):
                #print(result, ' :: ', sentence)
                output_result(id, start_index, start_index+len(sentence))
            
    
    def query_textual_similarity(self, query_embedding, sentence_embedding) -> int:
        res = util.semantic_search(query_embedding, sentence_embedding)
        return round(res[0][0]['score'], 2) * 100
        #round(item['score'], 2), sentences[item['corpus_id']]


    
    '''
    import nltk
from nltk.tokenize import word_tokenize, sent_tokenize

file_path = '/home/junyoung/files/apt-nlp/src/robert_outline.txt'
lines = []

with open(file_path, 'r') as file:
    for line in file:
        lines.append(line.strip())

# Tokenize the text into words
words = []

for line in lines:
    words.extend(word_tokenize(line))  # Use extend instead of append

# Join the words into a single string
text = ' '.join(words)

# Tokenize the text into sentences
sentences = sent_tokenize(text)


from sentence_transformers import SentenceTransformer, util
model = SentenceTransformer('msmarco-distilbert-base-v4')

corpus_embeddings = model.encode(sentences, show_progress_bar=True, convert_to_tensor=True)

query = "generative AI"
query_embedding = model.encode(query, convert_to_tensor=True)

result = util.semantic_search(query_embedding, corpus_embeddings)

print('query: '+query)
for item in result[0]:
    print(round(item['score'], 2), sentences[item['corpus_id']])
    
    '''

