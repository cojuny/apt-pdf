
from collections import defaultdict


class AhoCorasick:

    # Aho, A. V., & Corasick, M. J. (1975). Efficient string matching: an aid to bibliographic search. In Communications of the ACM (Vol. 18, Issue 6, pp. 333–340). ACM. https://doi.org/10.1145/360825.360855

    def __init__(self, keywords) -> None:
        self.goto = {} # g(tuple(state, char)) -> state
        self.fail = {} # f(state) -> state
        self.output = defaultdict(list) # output(state) -> list(keywords)

        # function constructions
        self.construct_goto(keywords)
        self.construct_failure()
    

    def search(self, a):
        # Algorithm 1. Pattern matching machine 
        state = 0
        res = defaultdict(list)

        for i, ai in enumerate(a):

            # if goto function fails, follow fail function
            while (state, ai) not in self.goto and state != 0:
                state = self.fail[state]
            
            # next valid state
            state = self.goto.get((state, ai), 0) # default value is 0 for self loop on state 0

            # if output function has output
            if self.output[state]:
                for keyword in self.output[state]:
                    res[keyword].append(i - len(keyword) + 1) # end index to start index

        return res

    def construct_goto(self,keywords):
        # Algorithm 2: Construction of the goto function
        def enter(a):
            state, j, m = 0, 0, len(a)
            
            # if goto function fails, j is the new start point
            while j < m and (state, a[j]) in self.goto:
                state = self.goto[(state, a[j])] 
                j += 1

            # add to the tree in order
            for p in range(j, m):
                self.newstate += 1
                self.goto[(state, a[p])] = self.newstate
                state = self.newstate
            self.output[state].append(a)

        # enter each keyword to goto and output functions
        self.newstate = 0         
        for keyword in keywords:
            enter(keyword)

         # self loop at state 0 for all other chars
        for keyword in keywords:
            for a in keyword:
                if not (0, a) in self.goto:
                    self.goto[(0, a)] = 0

    def construct_failure(self):
        # Algorithm 3. Construction of the failure function
        queue = []

        # for s such that depth <= 1, fail(s) = 0
        for (i, a), s in self.goto.items():
            if i > 0 or s == 0:
                continue
            queue.append(s)
            self.fail[s] = 0

        # for depth > 1 refer to the longest matching state as fail(s) 
        while queue:
            r = queue.pop(0)
            # for each a such that g(r,a) = s is not fail
            for (i, a), s in self.goto.items():
                if i != r:
                    continue
                if s not in self.fail:
                    # append next state
                    queue.append(s)
                    # traversal to get the longest matching state
                    state = self.fail[r]
                    while (state, a) not in self.goto:
                        state = self.fail[state]
                    self.fail[s] = self.goto[(state,a)]
                    self.output[s].extend(self.output[self.fail[s]])

if __name__ == "__main__":
    keywords = ["aa"]
    text = "aaaaaa"

    aho = AhoCorasick(keywords)
    res = aho.search(text)

    print(res)