from sentence_transformers import SentenceTransformer, util


class SemanticSearch:

    def __init__(self, queue) -> None:
        self.model = SentenceTransformer('msmarco-distilbert-base-v4')
        self.queue = queue

    def search(self, id: str, query: str, sentences: dict, threshold: int) -> None:
        query_embedding = self.model.encode(query, convert_to_tensor=True)
        for start_index, sentence in sentences.items():
            if self.queue.is_halt_signal():
                return
            sentence_embedding = self.model.encode(
                sentence, convert_to_tensor=True)
            result = self.query_textual_similarity(
                query_embedding=query_embedding, sentence_embedding=sentence_embedding)
            if (result >= threshold):
                self.queue.output_result(
                    id, start_index, start_index+len(sentence))

    def query_textual_similarity(self, query_embedding, sentence_embedding) -> int:
        res = util.semantic_search(query_embedding, sentence_embedding)
        return round(res[0][0]['score'], 2) * 100
