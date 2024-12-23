import threading
import queue
import time
import random

class Producer(threading.Thread):
    def __init__(self, q):
        threading.Thread.__init__(self)
        self.q = q

    def run(self):
        for _ in range(10):
            item = random.randint(1, 100)
            self.q.put(item)
            print(f'Produced: {item}')
            time.sleep(random.random())

class Consumer(threading.Thread):
    def __init__(self, q):
        threading.Thread.__init__(self)
        self.q = q

    def run(self):
        for _ in range(10):
            item = self.q.get()
            print(f'Consumed: {item}')
            self.q.task_done()
            time.sleep(random.random())

if __name__ == "__main__":
    q = queue.Queue()
    producer = Producer(q)
    consumer = Consumer(q)

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
