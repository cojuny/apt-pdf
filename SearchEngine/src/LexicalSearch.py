import KnuthMorrisPratt as KMP
from AhoCorasick import AhoCorasick as AC
from itertools import permutations


class LexicalSearch:
    def __init__(self, queue) -> None:
        self.queue = queue

    def kmp_search(self, id: str, target: str, scope: str, text: str, words: dict, sents: dict):

        if scope[0] == 'e':
            res = KMP.search(text, target)
            for p in res:
                if self.queue.is_halt_signal():
                    return
                self.queue.output_result(id, p, p+len(target))
        elif scope[0] == "w":
            for word, pos in words.items():
                res = KMP.search(word, target)
                if res:
                    for p in pos:
                        if self.queue.is_halt_signal():
                            return
                        self.queue.output_result(id, p, p+len(word))
        elif scope[0] == "s":
            for p, sent in sents.items():
                res = KMP.search(sent, target)
                if res:
                    if self.queue.is_halt_signal():
                        return
                    self.queue.output_result(id, p, p+len(sent))

    def ac_search(self, id: str, targets: list, connectors: list, scope: str, text: str, words: dict, sents: dict):
        if scope[0] == 'e':
            targets = self.preprocess_ac_exact(targets, connectors)
            ac = AC(targets)
            res = ac.search(text)
            for keyword, pos in res.items():
                for p in pos:
                    if self.queue.is_halt_signal():
                        return
                    self.queue.output_result(id, p, p+len(keyword))
        elif scope[0] == "w":
            ac = AC(targets)
            for word, pos in words.items():
                res = ac.search(word)
                if res and self.is_connector_valid(targets, connectors, res):
                    for p in pos:
                        if self.queue.is_halt_signal():
                            return
                        self.queue.output_result(id, p, p+len(word))
        elif scope[0] == "s":
            ac = AC(targets)
            for p, sent in sents.items():
                res = ac.search(sent)
                if res and self.is_connector_valid(targets, connectors, res):
                    if self.queue.is_halt_signal():
                        return
                    self.queue.output_result(id, p, p+len(sent))

    def is_connector_valid(self, targets: list, connectors: list, res: dict) -> bool:
        flag = True
        for i in range(len(connectors)):
            if connectors[i][0] == "A" and (not res[targets[i]]):
                flag = False
                break
            elif connectors[i][0] == "N" and res[targets[i]]:
                flag = False
                break
        return flag

    def preprocess_ac_exact(self, targets: list, connectors: list) -> list:
        res = []

        # remove all of the NOT operators
        for i in range(len(connectors)-1, -1, -1):
            if connectors[i][0] == 'N':
                connectors.pop(i)
                targets.pop(i)

        # find all the AND and them
        and_pos = []
        for i, c in enumerate(connectors):
            if c[0] == 'A':
                and_pos.append(i)

        # chain the AND positions if they are adjacent:
        and_pos_chain = []
        if not and_pos:
            pass
        else:
            # Initialize start and j to the first position
            start = j = and_pos[0]
            for k in and_pos[1:]:  # Iterate through the rest of positions
                if k - j == 1:
                    j = k  # Correctly update j to current position k
                else:
                    and_pos_chain.append((start, j))  # Add the completed chain
                    start = j = k  # Update start and j to the new chain start
            and_pos_chain.append((start, j))

        # generate permutation for AND chain
        visited = set()
        for start, end in and_pos_chain:
            substring = targets[start-1:end+1]
            for c in substring:
                visited.add(c)
            k = permutations(substring)
            for i in k:
                res.append(''.join(i))

        for item in set(targets) - visited:
            res.append(item)

        return res
