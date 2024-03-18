import pytest
import json
from unittest.mock import patch
from src.APIServer import app

@pytest.fixture
def client():
    with app.test_client() as client:
        yield client

def test_upload_text(client):
    mock_data = {'text': 'sample text', 'id': '1'}
    with patch('queue.Queue.put') as mock_put:
        response = client.post('/text', data=json.dumps(mock_data), content_type='application/json')
        assert response.status_code == 200
        mock_put.assert_called_once()
        put_args, put_kwargs = mock_put.call_args
        task = put_args[0]  # Assuming the first argument to put() is the task
        assert task['action'] == 'add_document'
        assert task['data'] == mock_data

def test_search_lexical(client):
    mock_data = {'data': 'sample data'}  # Replace with your specific data
    with patch('queue.Queue.put') as mock_put:
        response = client.post('/lexical', data=json.dumps(mock_data), content_type='application/json')
        assert response.status_code == 200
        mock_put.assert_called_once()
        put_args, put_kwargs = mock_put.call_args
        task = put_args[0]
        assert task['action'] == 'lexical_search'
        assert task['data'] == mock_data

def test_search_keyword(client):
    mock_data = {'data': 'sample data'}  # Replace with your specific data
    with patch('queue.Queue.put') as mock_put:
        response = client.post('/keyword', data=json.dumps(mock_data), content_type='application/json')
        assert response.status_code == 200
        mock_put.assert_called_once()
        put_args, put_kwargs = mock_put.call_args
        task = put_args[0]
        assert task['action'] == 'keyword_search'
        assert task['data'] == mock_data

def test_search_semantic(client):
    mock_data = {'data': 'sample data'}  # Replace with your specific data
    with patch('queue.Queue.put') as mock_put:
        response = client.post('/semantic', data=json.dumps(mock_data), content_type='application/json')
        assert response.status_code == 200
        mock_put.assert_called_once()
        put_args, put_kwargs = mock_put.call_args
        task = put_args[0]
        assert task['action'] == 'semantic_search'
        assert task['data'] == mock_data

def test_del_document(client):
    mock_data = {'data': 'sample data'}  # Replace with your specific data
    with patch('queue.Queue.put') as mock_put:
        response = client.post('/delete', data=json.dumps(mock_data), content_type='application/json')
        assert response.status_code == 200
        mock_put.assert_called_once()
        put_args, put_kwargs = mock_put.call_args
        task = put_args[0]
        assert task['action'] == 'del_document'
        assert task['data'] == mock_data

