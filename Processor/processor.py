'''
Created on 24 iul. 2020

@author: George
'''
import pika
import json
from threading import Thread

class MainProcessor:
    def process(self, jsonBitmap):
        predictionsList = []
        
        # // whatever ...
        print(jsonBitmap)
        h = jsonBitmap['height']
        w = jsonBitmap['width']
        pixelsList = jsonBitmap['pixels']
        # // whatever ...
        
        predictionsList.append({'character': 'a', 'percentage': 100})
        predictionsList.append({'character': 'b', 'percentage': 80})
        predictionsList.append({'character': 'c', 'percentage': 20})
        
        return predictionsList


mainProcessor = MainProcessor()

def process(completeMessageJSON):
    
    jsonBitmap = completeMessageJSON['bitmap']
    jsonToken = completeMessageJSON['token']
    
    listPredictions = mainProcessor.process(jsonBitmap)
    
    formattedMessage = {"preds": listPredictions, "token": jsonToken}
    message = json.dumps(formattedMessage)
    
    # PUBLISHER
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    
    #message = {"preds": [{"character": "a", "percentage": 100}, {"character": "b", "percentage": 70}], "token": {"message": "123hash123_TOKEN_456hash456"}}
    #stringMessage = json.dumps(message)
    
    channel.basic_publish(exchange='JavaExchange.IN', routing_key='to.java.routing.key', body=message, properties=pika.BasicProperties(
    delivery_mode=2,  # make message persistent
        ))
    print(" [x] Sent %r" % message)
    connection.close()



# LISTENER
def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)
    
    parsedMessageString = json.loads(body) # - from bytes to string
    parsedMessageJSON = json.loads(parsedMessageString) # - from string to dict
    print(parsedMessageJSON)
    
    #t = Thread(target=process, args=(parsedMessageJSON))
    #t.start()
    #threads.append(t) - if desired to be in a list
    process(parsedMessageJSON)
    #t.join()
    
    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
print(' [*] Waiting for messages. To exit press CTRL+C')

channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

channel.start_consuming()


