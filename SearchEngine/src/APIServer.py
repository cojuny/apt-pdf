from SearchManager import SearchManager
from flask import Flask, request, jsonify
import os, signal, queue

app = Flask(__name__)
q = queue.Queue()
manager = None

def create_manager():
    global manager
    manager = SearchManager()

def worker():
    while True:
        # Wait for a task from the queue and process it
        task = q.get()
        try:
            action = task['action']
            data = task['data']
            if action == 'add_document':
                manager.add_document(text=data['text'], id=data['id'])
            elif action == 'lexical_search':
                manager.lexical_search(**data)
            elif action == 'keyword_search':
                manager.keyword_search(**data)
            elif action == 'semantic_search':
                manager.semantic_search(**data)
            elif action == 'del_document':
                manager.del_document(id=data['id'])
            q.task_done()
        except Exception as e:
            print(f"An error occurred: {e}")
            q.task_done()

@app.route('/text', methods=['POST'])
def upload_text():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            q.put({'action': 'add_document', 'data': data})
            return jsonify({'message': '/text success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/lexical', methods=['POST'])
def search_lexical():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            q.put({'action': 'lexical_search', 'data': data})
            return jsonify({'message': '/lexical success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/keyword', methods=['POST'])
def search_keyword():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            # Add task to queue
            q.put({'action': 'keyword_search', 'data': data})
            return jsonify({'message': '/keyword success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/semantic', methods=['POST'])
def search_semantic():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            q.put({'action': 'semantic_search', 'data': data})
            return jsonify({'message': '/semantic success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/halt', methods=['POST'])
def halt():
    manager.queue.halt = True
    while not q.empty():
        try:
            q.get_nowait()  
            q.task_done()  
        except queue.Empty:
            break  

    return jsonify({'error': 'request fail'})

@app.route('/delete', methods=['POST'])
def del_document():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            q.put({'action': 'del_document', 'data': data})
            return jsonify({'message': '/delete success'})
        except Exception as e:
            return jsonify({'error': 'exception fail'})
    return jsonify({'error': 'request fail'})

@app.route('/shutdown', methods=['POST'])
def shutdownServer():
    manager.queue.shutdown()
    os.kill(os.getpid(), signal.SIGINT)
    
    return jsonify({'message': '/shutdown success'})

# if __name__ == '__main__':
#     t = Thread(target=worker)
#     t.daemon = True  
#     t.start()

#     app.run(debug=False, host='127.0.0.1', port=5050, use_reloader=False)
