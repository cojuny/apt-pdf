from SearchHandler import SearchHandler
from flask import Flask, request, jsonify
import os, signal

app = Flask(__name__)
handler = SearchHandler()


@app.route('/text', methods=['POST'])
def upload_text():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            if data:
                handler.add_document(text=data['text'], id=data['id'])
                return jsonify({'message': 'PDF text received successfully'})
            else:
                return jsonify({'error': 'Invalid JSON Format!!'})
        except Exception as e:
            return jsonify({'error': 'Unexpected Error Occurred!!'})
    return jsonify({'error': 'Invalid Content-Type!!'})

@app.route('/lexical', methods=['POST'])
def search_lexical():
    response = ""
    if request.headers['Content-Type'] == 'application/json':
        try:
            print(request.json)
            response = jsonify({'message': 'PDF text received successfully'})
        except Exception as e:
            print(str(e))
            response = jsonify({'error': 'Invalid request format'})

    return response

@app.route('/keyword', methods=['POST'])
def search_keyword():
    response = ""
    if request.headers['Content-Type'] == 'application/json':
        try:
            print(request.json)
            response = jsonify({'message': 'PDF text received successfully'})
        except Exception as e:
            print(str(e))
            response = jsonify({'error': 'Invalid request format'})

    return response

@app.route('/semantic', methods=['POST'])
def search_semantic():
    print("received")
    response = ""
    if request.headers['Content-Type'] == 'application/json':
        try:
            print(request.json)
            response = jsonify({'message': 'PDF text received successfully'})
        except Exception as e:
            print(str(e))
            response = jsonify({'error': 'Invalid request format'})

    return response

@app.route('/shutdown', methods=['POST'])
def shutdownServer():
    os.kill(os.getpid(), signal.SIGINT)
    return jsonify({ "success": True, "message": "Server is shutting down..." })

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=5050)