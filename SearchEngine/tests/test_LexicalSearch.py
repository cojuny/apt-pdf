import pytest
from src.LexicalSearch import LexicalSearch
from unittest.mock import Mock

@pytest.fixture
def setup(mock_queue):
    return LexicalSearch(queue=mock_queue)

@pytest.fixture
def mock_queue(mocker):
    mock = Mock()
    mocker.patch.object(mock, 'is_halt_signal', return_value=False)
    return mock

@pytest.fixture
def text():
    return "this is a test document. please give me an a."

@pytest.fixture
def words():
    return {'this': [0], 'is': [5], 'a': [8, 43], 'test': [10], 'document': [15], '.': [23, 44], 'please': [25], 'give': [32], 'me': [37], 'an': [40]}

@pytest.fixture
def sents():
    return {0: 'this is a test document.', 25: 'please give me an a.'}

def test_kmp_search_e(setup, text, words, sents):
    setup.kmp_search(id='1', target='me', scope="exact", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 2
    assert ('1', 19, 21) in output_result_args
    assert ('1', 37, 39) in output_result_args

def test_kmp_search_w(setup, text, words, sents):
    setup.kmp_search(id='1', target='me', scope="word", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 2
    assert ('1', 15, 23) in output_result_args
    assert ('1', 37, 39) in output_result_args

def test_kmp_search_s(setup, text, words, sents):
    setup.kmp_search(id='1', target='please', scope="sentence", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 1
    assert ('1', 25, 45) in output_result_args

def test_preprocess_ac_exact(setup):
    assert setup.preprocess_ac_exact(['e','s', 'e', 'm', 'exclude'],['OR', 'AND', 'OR', 'AND', 'NOT']) == ['es', 'se', 'em', 'me']

def test_ac_search_e(setup, text, words, sents):
    setup.ac_search(id='1', targets=['e','s', 'e', 'm', 'exclude'], connectors=['OR', 'AND', 'OR', 'AND', 'NOT'], scope="exact", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 4
    assert ('1', 11, 13) in output_result_args
    assert ('1', 19, 21) in output_result_args
    assert ('1', 37, 39) in output_result_args
    assert ('1', 29, 31) in output_result_args

def test_ac_search_w(setup, text, words, sents):
    setup.ac_search(id='1', targets=['do', 'cu', 'a'], connectors=['OR', 'AND', 'NOT'], scope="word", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 1
    assert ('1', 15, 23) in output_result_args

def test_ac_search_s(setup, text, words, sents):
    setup.ac_search(id='1', targets=['m', 'e', 'a', 'this'], connectors=['OR', 'AND', 'OR', 'NOT'], scope="sentence", text=text, words=words, sents=sents)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 1
    assert ('1', 25, 45) in output_result_args

