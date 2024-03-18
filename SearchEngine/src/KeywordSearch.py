#https://docs.python.org/3/glossary.html#term-dictio
from nltk.corpus import wordnet

class KeywordSearch:
    
    def __init__(self, queue) -> None:
        self.queue = queue

    ##!! Search should have either target == True or target_pos == True !!

    def search(self, id: str, words: dict, pos: dict, target: str, target_pos = None, synonyms = False) -> None:
        if target not in words:
            return
        
        if synonyms:
            syns = self.gen_synonyms(target)
            for syn in syns:
                self.search(id, words, pos, target = syn, target_pos = target_pos)

        res = words[target]
        for index in res:
            if self.queue.is_halt_signal():
                return
            if target_pos:
                if pos[index] == target_pos:
                    self.queue.output_result(id, index, index+len(target))
                else:
                    continue
            else:
                self.queue.output_result(id, index, index+len(target))
                


    def search_pos_only(self, id: str, words: dict, pos: dict, target_pos):
        for key in words.keys():
            for index in words[key]:        
                if target_pos == pos[index]:
                    if self.queue.is_halt_signal():
                        return
                    self.queue.output_result(id, index, index+len(key))
    
    def gen_synonyms(self, word) -> set[str]:
        res = set()
        for syn in wordnet.synsets(word):
            for word in syn.lemma_names():
                if '_' in word:
                    continue
                res.add(word)
        return res
    
