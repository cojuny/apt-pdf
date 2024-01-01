from flask import Flask, request, jsonify
import json, os, signal

app = Flask(__name__)

class PDFHandler:
    @classmethod
    def save_json(cls, text):
        print(len(text['text']))
        with open('text.json', 'w', encoding='utf-8') as f:
            json.dump(text, f, ensure_ascii=False, indent=4)

  


@app.route('/text', methods=['POST'])
def upload_text():
    if request.headers['Content-Type'] == 'application/json':
        
        try:
            data = request.json
            
            
            if data:
                
                PDFHandler.save_json(data)
                return jsonify({'message': 'PDF text received successfully'})
            else:
                return jsonify({'error': 'Invalid JSON format or missing text field'})
        except Exception as e:
            print(str(e))
            return jsonify({'error': f'Error processing JSON: {str(e)}'})

    return jsonify({'error': 'Invalid content type'})

@app.route('/shutdown', methods=['POST'])
def shutdownServer():
    os.kill(os.getpid(), signal.SIGINT)
    return jsonify({ "success": True, "message": "Server is shutting down..." })

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=5050)