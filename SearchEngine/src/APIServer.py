from SearchManager import SearchManager
from flask import Flask, request, jsonify
import os, signal


app = Flask(__name__)
manager = SearchManager()


@app.route('/text', methods=['POST'])
def upload_text():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            manager.add_document(text=data['text'], id=data['id'])
            return jsonify({'message': '/text success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/lexical', methods=['POST'])
def search_lexical():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            manager.lexical_search(
                id=data['id'],
                targets=data.get('targets'),
                connectors=data.get('connectors'),
                scope=data['scope']
            )
            return jsonify({'message': '/lexical success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})


@app.route('/keyword', methods=['POST'])
def search_keyword():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            manager.keyword_search(
                id=data['id'], 
                target=data.get('target', None), 
                target_pos=data.get('target_pos', None), 
                synonyms=data['synonyms'])
            return jsonify({'message': '/keyword success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/semantic', methods=['POST'])
def search_semantic():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            manager.semantic_search(
                id=data['id'], 
                query=data['query'],
                threshold=data['threshold']
                )
            return jsonify({'message': '/semantic success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/del', methods=['POST'])
def del_document():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            manager.del_document(id=data['id'])
            return jsonify({'message': '/del success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/shutdown', methods=['POST'])
def shutdownServer():
    os.kill(os.getpid(), signal.SIGINT)
    return jsonify({'message': '/shutdown success'})

if __name__ == '__main__':
    app.run(debug=False, host='127.0.0.1', port=5050)