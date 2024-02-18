from kafka import KafkaProducer

queue = KafkaProducer(bootstrap_servers='localhost:9092')

def output_result(id, start_index:str, end_index:str):
    queue.send('queue', key=bytes("{}".format(id), 'utf-8'), value=bytes("{}/{}".format(start_index, end_index), 'utf-8'))
    queue.flush()

def end_of_init():
    queue.send('queue', key=b"", value=b"I")
    queue.flush()

def end_of_search(id):
    queue.send('queue', key=bytes("{}".format(id), 'utf-8'), value=b"E")
    queue.flush()

