from collections import defaultdict
import spacy

class SearchHandler:

    

    def __init__(self):
        pass

    

if __name__ == '__main__':
    text = "Preface \n \nTo help students develop their generic competencies, as well as prepare them for professional \npractice in the workplace, for further academic pursuits and for lifelong learning, the University \nhas a mandatory Capstone Project learning experience requirement for all four-year undergraduate \nprogrammes. Students are expected to consolidate their learning experiences accumulated over \ntheir entire undergraduate study in the Capstone Project. \n \nThis handbook aims to provide you with a better understanding of the Capstone Project, including \nthe allocation, proposal and report requirements, presentation arrangements, etc."
    nlp = spacy.load("en_core_web_sm") 
    doc = nlp(text)
    for sent in doc.sents:
        print(sent.start)
        print("--------------")