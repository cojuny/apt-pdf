from kafka import KafkaProducer


class ResultQueue:
    def __init__(self) -> None:
        try:
            self.queue = KafkaProducer(bootstrap_servers='localhost:9092')
        except:
            print("no broker available.")
        self.halt = False

    def output_result(self, id, start_index: str, end_index: str):
        self.queue.send('queue', key=bytes("{}".format(id), 'utf-8'),
                        value=bytes("{}/{}".format(start_index, end_index), 'utf-8'))
        self.queue.flush()

    def end_of_init(self):
        self.queue.send('queue', key=b"", value=b"I")
        self.queue.flush()

    def end_of_search(self, id):
        self.queue.send('queue', key=bytes(
            "{}".format(id), 'utf-8'), value=b"E")
        self.queue.flush()

    def shutdown(self):
        self.queue.close()

    def is_halt_signal(self):
        if self.halt:
            self.halt = False
            return True
        return False
