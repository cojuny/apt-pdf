from flask import Flask, request, jsonify

app = Flask(__name__)

class PDFHandler:
    @classmethod
    def save_text(cls, text):
        # You can implement your logic here to save the text to a file or database
        print("Received PDF Text:")
        print(text)

@app.route('/text', methods=['POST'])
def upload_text():
    if request.headers['Content-Type'] == 'application/json':
        try:
            data = request.json
            pdf_text = data.get('text')
            
            if pdf_text:
                PDFHandler.save_text(pdf_text)
                return jsonify({'message': 'PDF text received successfully'})
            else:
                return jsonify({'error': 'Invalid JSON format or missing text field'})
        except Exception as e:
            return jsonify({'error': f'Error processing JSON: {str(e)}'})

    return jsonify({'error': 'Invalid content type'})

if __name__ == '__main__':
    app.run(debug=True)