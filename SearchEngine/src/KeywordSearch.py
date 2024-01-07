#https://docs.python.org/3/glossary.html#term-dictio
from ResultQueue import output_result
from nltk.corpus import wordnet

class KeywordSearch:
    
    def __init__(self) -> None:
        pass

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
            if target_pos:
                if pos[index] == target_pos:
                    output_result(id, index, index+len(target))
                else:
                    continue
            else:
                output_result(id, index, index+len(target))
                


    def search_pos_only(self, id: str, words: dict, pos: dict, target_pos):
        for key in words.keys():
            for index in words[key]:        
                if target_pos == pos[index]:
                    output_result(id, index, index+len(key))
    
    def gen_synonyms(self, word) -> set[str]:
        res = set()
        for syn in wordnet.synsets(word):
            for word in syn.lemma_names():
                if '_' in word:
                    continue
                res.add(word)
        return res