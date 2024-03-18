from collections import defaultdict
from nltk import pos_tag
from nltk.tokenize import word_tokenize
from copy import deepcopy

class Document:
    def __init__(self, text: str, id: str, processor) -> None:
        self.text = text.lower()
        self.id = id
        self.sents = self.gen_sents(processor)
        self.words = self.gen_words()
        self.pos = self.gen_pos()

    def gen_sents(self, processor):
        res = defaultdict(str)
        for sent in processor(self.text).sents:
            res[sent.start_char] = sent.text
        return res

    def gen_words(self):
        def spans(txt):
            tokens = word_tokenize(txt)
            offset = 0
            for token in tokens:
                offset = txt.find(token, offset)
                yield token, offset
                offset += len(token)
        res = defaultdict(list)
        for word, i in spans(self.text):
            res[word.lower()].append(i) 
        return res

    def gen_pos(self):
        res = defaultdict(int)
        reference = deepcopy(self.words)

        for word, tag in pos_tag(word_tokenize(self.text), tagset='universal'):
            index = reference[word.lower()].pop(0)
            res[index] = tag
        return res
