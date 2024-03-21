from collections import defaultdict
import spacy
from Document import Document
from LexicalSearch import LexicalSearch
from KeywordSearch import KeywordSearch
from SemanticSearch import SemanticSearch
from ResultQueue import ResultQueue


class SearchManager:

    def __init__(self):
        self.documents = defaultdict(Document)
        self.token_processor = spacy.load("en_core_web_sm")
        self.queue = ResultQueue()
        self.lexical = LexicalSearch(self.queue)
        self.keyword = KeywordSearch(self.queue)
        self.semantic = SemanticSearch(self.queue)

        self.queue.end_of_init()

    def lexical_search(self, id: str, targets: list, connectors: list, scope: str):
        doc = self.documents[id]
        targets = [target.lower() for target in targets]

        if len(targets) <= 1:
            self.lexical.kmp_search(
                id=id,
                target=targets[0],
                scope=scope,
                text=doc.text,
                words=doc.words,
                sents=doc.sents)
        else:
            self.lexical.ac_search(
                id=id,
                targets=targets,
                connectors=connectors,
                scope=scope,
                text=doc.text,
                words=doc.words,
                sents=doc.sents)
        self.queue.end_of_search(id)

    def keyword_search(self, id: str, target: str = None, target_pos: str = None, synonyms: bool = False):
        doc = self.documents[id]
        if not target:
            self.keyword.search_pos_only(
                id=id,
                words=doc.words,
                pos=doc.pos,
                target_pos=target_pos
            )
        else:
            self.keyword.search(
                id=id,
                words=doc.words,
                pos=doc.pos,
                target=target.lower(),
                target_pos=target_pos,
                synonyms=synonyms
            )
        self.queue.end_of_search(id)

    def semantic_search(self, id: str, query: str, threshold: int):
        doc = self.documents[id]
        self.semantic.search(
            id=id,
            sentences=doc.sents,
            query=query,
            threshold=threshold
        )
        self.queue.end_of_search(id)

    def add_document(self, text: str, id: str) -> None:
        self.documents[id] = Document(text, id, self.token_processor)
        self.queue.end_of_init()

    def del_document(self, id: str) -> bool:
        if id in self.documents:
            del self.documents[id]
            self.queue.end_of_init()
            return True
        else:
            self.queue.end_of_init()
            return False
