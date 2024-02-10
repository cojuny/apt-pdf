# Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). Introduction to Algorithms (3rd Edition). In VII Selected Topics, 32 String Matching (3rd ed., pp. 1002-1012). MIT Press.
# Knuth, D. E., Morris, J., & Pratt, V. R. (1977). Fast Pattern Matching in Strings. SIAM Journal on Computing, 6(2), 323–350. https://doi.org/10.1137/0206024


def compute_prefix_function(P):
        m = len(P)
        pi = [0] * m
        k = 0
        for q in range(2, m+1):
            while k > 0 and P[k] != P[q-1]:
                k = pi[k - 1]
            if P[k] == P[q-1]:
                k = k + 1
            pi[q-1] = k
        return pi

def search(T, P):

    res = []

    n = len(T)
    m = len(P)
    pi = compute_prefix_function(P)
    q = 0  
    for i in range(n):  
        while q > 0 and P[q] != T[i]:
            q = pi[q - 1] 
        if P[q] == T[i]:
            q = q + 1  
        if q == m:  
            res.append(i-m+1)
            q = pi[q - 1]  

    return res

if __name__ == "__main__":
    keywords = "aa"
    text = "aaaaaa"

    res = search(text, keywords)


    print(res)