import pytest
import spacy
from unittest.mock import patch
from src.Document import Document


@pytest.fixture
def processor():
    yield spacy.load("en_core_web_sm")

@pytest.fixture
def setup(processor):
    return Document(text="tHIS IS a test Document. Please give me an A.",
                     id='1', 
                     processor=processor)

def test__init__(setup):
    assert setup.text == "this is a test document. please give me an a."
    assert setup.id == '1'
    assert setup.sents != None
    assert setup.words != None
    assert setup.pos != None

def test_gen_words(setup):
    test_dict = {'this': [0], 
                 'is': [5], 
                 'a': [8, 43], 
                 'test': [10], 
                 'document': [15], 
                 '.': [23, 44], 
                 'please': [25], 
                 'give': [32], 
                 'me': [37], 
                 'an': [40]}
    words = setup.gen_words()
    assert test_dict == words

def test_gen_sents(setup, processor):
    test_dict = {}
    test_dict[0] = 'this is a test document.'
    test_dict[25] = 'please give me an a.'
    sents = setup.gen_sents(processor)
    assert test_dict == sents

def test_gen_pos(setup):
    test_dict = {0: 'DET', 
                5: 'VERB',
                8: 'DET', 
                10: 'NOUN', 
                15: 'NOUN', 
                23: '.', 
                25: 'VERB', 
                32: 'VERB', 
                37: 'PRON', 
                40: 'DET', 
                43: 'DET', 
                44: '.'}
    pos = setup.gen_pos()
    assert test_dict == pos 