import pytest
from src.ResultQueue import ResultQueue
from unittest.mock import Mock

@pytest.fixture
def setup(mocker):
    mocked_producer = Mock()
    mocker.patch('src.ResultQueue.KafkaProducer', return_value=mocked_producer)
    return ResultQueue()

def test_no_broker_exception():
    with pytest.raises(Exception):
        ResultQueue("no broker available")

def test_output_result(setup):
    setup.output_result('123', '1', '2')
    setup.queue.send.assert_called_once_with('queue', key=b'123', value=b'1/2')
    setup.queue.flush.assert_called_once()

def test_end_of_init(setup):
    setup.end_of_init()
    setup.queue.send.assert_called_once_with('queue', key=b'', value=b'I')
    setup.queue.flush.assert_called_once()

def test_end_of_search(setup):
    setup.end_of_search('1')
    setup.queue.send.assert_called_once_with('queue', key=b'1', value=b'E')
    setup.queue.flush.assert_called_once()
    
def test_shutdown(setup):
    setup.shutdown()
    setup.queue.close.assert_called_once()

def test_is_halt_signal(setup):
    assert setup.is_halt_signal() == False
    setup.halt = True
    assert setup.is_halt_signal() == True
    assert setup.halt == False