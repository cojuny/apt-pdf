import APIServer
from threading import Thread

def main():
    APIServer.create_manager()
    t = Thread(target=APIServer.worker)
    t.daemon = True  
    t.start()

    APIServer.app.run(debug=False, host='127.0.0.1', port=5050, use_reloader=False)

if __name__ == '__main__':
    main()