from collections import defaultdict
from nltk import pos_tag
from nltk.tokenize import word_tokenize
from copy import deepcopy

class Document:
    def __init__(doc, text: str, id: str, processor) -> None:
        doc.text = text.lower()
        doc.id = id
        doc.sents = doc.gen_sents(processor)
        doc.words = doc.gen_words()
        doc.pos = doc.gen_pos()

    def gen_sents(doc, processor):
        res = defaultdict(str)
        for sent in processor(doc.text).sents:
            res[sent.start_char] = sent.text
        return res

    def gen_words(doc):
        def spans(txt):
            tokens = word_tokenize(txt)
            offset = 0
            for token in tokens:
                offset = txt.find(token, offset)
                yield token, offset
                offset += len(token)
        res = defaultdict(list)
        for word, i in spans(doc.text):
            res[word.lower()].append(i) 
        return res

    def gen_pos(doc):
        res = defaultdict(int)
        reference = deepcopy(doc.words)

        for word, tag in pos_tag(word_tokenize(doc.text), tagset='universal'):
            index = reference[word.lower()].pop(0)
            res[index] = tag
        return res