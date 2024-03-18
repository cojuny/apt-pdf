import pytest
from src.KeywordSearch import KeywordSearch
from unittest.mock import Mock

@pytest.fixture
def setup(mock_queue):
    return KeywordSearch(queue=mock_queue)

@pytest.fixture
def mock_queue(mocker):
    mock = Mock()
    mocker.patch.object(mock, 'is_halt_signal', return_value=False)
    return mock

@pytest.fixture
def words():
    return {'this': [0], 'is': [5], 'a': [8, 43], 'test': [10], 'document': [15], '.': [23, 44], 'please': [25], 'give': [32], 'me': [37], 'an': [40]}

@pytest.fixture
def pos():
    return {0: 'DET', 5: 'VERB', 8: 'DET', 10: 'NOUN', 15: 'NOUN', 23: '.', 25: 'VERB', 32: 'VERB', 37: 'PRON', 40: 'DET', 43: 'NOUN', 44: '.'}
    
def test_search_target_only(setup, words, pos) :
    setup.search(id='1', words=words, pos=pos, target='a')
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 2
    assert ('1', 8, 9) in output_result_args
    assert ('1', 43, 44) in output_result_args

def test_search_target_only_no_result(setup, words, pos) :
    setup.search(id='1', words=words, pos=pos, target='nothing')    
    assert not(setup.queue.output_result.called)

def test_search_with_pos(setup, words, pos) :
    setup.search(id='1', words=words, pos=pos, target='a', target_pos="NOUN")
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 1
    assert ('1', 43, 44) in output_result_args

def test_search_pos_only(setup, words, pos):
    setup.search_pos_only(id='1', words=words, pos=pos, target_pos="DET")
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 3
    assert ('1', 0, 4) in output_result_args
    assert ('1', 8, 9) in output_result_args
    assert ('1', 40, 42) in output_result_args

def test_gen_synonyms(setup):
    syns = setup.gen_synonyms('please')
    assert syns == {'please', 'delight'}