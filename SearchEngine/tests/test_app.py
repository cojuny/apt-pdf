import json
from src.app import app

def test_upload_text_valid_json():
    client = app.test_client()

    # Prepare a valid JSON payload
    payload = {'text': 'Your PDF text here'}

    response = client.post('/text',
                           data=json.dumps(payload),
                           content_type='application/json')

    assert response.status_code == 200
    assert 'message' in response.json

def test_upload_text_invalid_json():
    client = app.test_client()

    # Prepare an invalid JSON payload (missing 'text' field)
    payload = {'other_field': 'Some value'}

    response = client.post('/text',
                           data=json.dumps(payload),
                           content_type='application/json')

    assert response.status_code == 200  # Or you can customize this based on your actual behavior
    assert 'error' in response.json

def test_upload_text_invalid_content_type():
    client = app.test_client()

    response = client.post('/text', data='Some data', content_type='text/plain')

    assert response.status_code == 200  # Or you can customize this based on your actual behavior
    assert 'error' in response.json

if __name__ == '__main__':
    import pytest
    pytest.main()