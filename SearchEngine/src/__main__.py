import APIServer
from threading import Thread

def main():
    t = Thread(target=APIServer.worker)
    t.daemon = True  
    t.start()

    APIServer.app.run(debug=True, host='127.0.0.1', port=5050, use_reloader=False)

if __name__ == '__main__':
    main()