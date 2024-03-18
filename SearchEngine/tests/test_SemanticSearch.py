import pytest
from src.SemanticSearch import SemanticSearch
from unittest.mock import Mock

@pytest.fixture
def setup(mock_queue):
    return SemanticSearch(queue=mock_queue)

@pytest.fixture
def mock_queue(mocker):
    mock = Mock()
    mocker.patch.object(mock, 'is_halt_signal', return_value=False)
    return mock

@pytest.fixture
def sents():
    return {0: 'this is a test document.', 25: 'please give me an a.', 45: 'this doucment is for testing only'}

def test_query_textual_similarity(setup, sents):
    embedding_1 = setup.model.encode(sents[0], convert_to_tensor=True)
    embedding_2 = setup.model.encode(sents[0], convert_to_tensor=True)
    assert setup.query_textual_similarity(embedding_1, embedding_2) in range(0,101)

def test_search(setup, sents):
    setup.search(id='1', query="this file is for unit testing", sentences=sents, threshold=40)
    output_result_args = [args for args, kwargs in setup.queue.output_result.call_args_list]
    assert setup.queue.output_result.call_count == 2
    assert ('1', 0, 24) in output_result_args
    assert ('1', 45, 78) in output_result_args
