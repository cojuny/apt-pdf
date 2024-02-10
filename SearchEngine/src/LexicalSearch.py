import KnuthMorrisPratt as KMP
from AhoCorasick import AhoCorasick as AC
from ResultQueue import output_result

class LexicalSearch:
    def __init__(self) -> None:
        pass

    
    def kmp_search(self, id: str, target:str, scope:str, text:str, words:dict, sents:dict): 
        
        if not scope:
            res = KMP.search(text, target)
            for p in res:
                output_result(id, start_index=p, end_index=p+len(target))
        elif scope[0] == "w":
            for word, pos in words.items():
                res = KMP.search(word, target)
                if res:
                    for p in pos:
                        output_result(id, start_index=p, end_index=p+len(word))
        elif scope[0] == "s":
            for p, sent in sents.items():
                res = KMP.search(sent, target)
                if res:
                    output_result(id, start_index=p, end_index=p+len(sent))
                    

    def ac_search(self, id: str, targets:list, connectors:list, scope:str, text:str, words:dict, sents:dict):
        ac = AC(targets)
        
        if not scope:
            res = ac.search(text)
            for keyword, pos in res.items():
                for p in pos:
                    output_result(id, start_index=p, end_index=p+len(keyword))
        elif scope[0] == "w":
            for word, pos in words.items():
                res = ac.search(word)
                if res and self.is_connector_valid(targets, connectors, res):
                    for p in pos:
                        output_result(id, start_index=p, end_index=p+len(word))
        elif scope[0] == "s":
            for p, sent in sents.items():
                res = ac.search(sent)
                if res and self.is_connector_valid(targets, connectors, res):
                    output_result(id, start_index=p, end_index=p+len(sent))



    def is_connector_valid(self, targets:list, connectors:list, res:dict) -> bool:
        flag = True
        for i in range(len(connectors)):
            if connectors[i][0] == "A" and (not res[targets[i]]):
                flag = False
                break
            elif connectors[i][0] == "N" and res[targets[i]]:
                flag = False
                break
        return flag