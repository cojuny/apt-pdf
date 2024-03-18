import pytest
from src.SearchManager import SearchManager
from unittest.mock import Mock

@pytest.fixture
def setup(mocker):
    mocker.patch('src.SearchManager.spacy', return_value=Mock())
    mocker.patch('src.SearchManager.ResultQueue', return_value=Mock())
    mocker.patch('src.SearchManager.LexicalSearch', return_value=Mock())
    mocker.patch('src.SearchManager.KeywordSearch', return_value=Mock())
    mocker.patch('src.SearchManager.SemanticSearch', return_value=Mock())
    mocker.patch('src.SearchManager.Document', return_value=Mock())
    return SearchManager()

def test__init__(setup):
    setup.queue.end_of_init.assert_called_once()
    
def test_add_document(setup):
    setup.add_document('sampe_text', '1')
    assert setup.documents['1'] != None
    assert len(setup.documents) > 0
    assert setup.queue.end_of_init.call_count == 2


def test_del_document(setup):
    setup.add_document('sampe_text', '1')
    assert not setup.del_document('2')
    assert setup.del_document('1') 
    assert len(setup.documents) == 0
    assert setup.queue.end_of_init.call_count == 4

def test_lexical_search(setup):
    setup.add_document('sampe_text', '1')
    setup.lexical_search('1', ['va1'], ['OR'], 'word')
    setup.lexical_search('1', ['va1','val2'], ['OR','AND'], 'word')
    setup.lexical.kmp_search.assert_called_once()
    setup.lexical.ac_search.assert_called_once()
    assert setup.queue.end_of_search.call_count == 2

def test_keyword_search(setup):
    setup.add_document('sampe_text', '1')
    setup.keyword_search(id='1', target_pos='NOUN', synonyms=False)
    setup.keyword_search(id='1', target='val1', synonyms=True)
    setup.keyword.search_pos_only.assert_called_once()
    setup.keyword.search.assert_called_once()
    assert setup.queue.end_of_search.call_count == 2

def test_semantic_search(setup):
    setup.add_document('sampe_text', '1')
    setup.semantic_search(id='1', query='val1', threshold=50)
    setup.semantic.search.assert_called_once()
    setup.queue.end_of_search.assert_called_once()

