'''
Created on 24 iul. 2020

@author: George
'''
import pika
import json
from concurrent.futures.thread import ThreadPoolExecutor
from random import randint

class PredictionMapper:
    @staticmethod
    def map(pixel):
        pixDict = {'character': pixel[0], 'percentage': pixel[1]}
        return pixDict

class PixelMapper:
    @staticmethod
    def map(pixel):
        r = int(pixel['r'])
        g = int(pixel['g'])
        b = int(pixel['b'])
        return [r,g,b]

class HardProcessor:
    @staticmethod
    def process(height, width, RGBpixels):
        predictionsList = [['a', randint(70,100)],['c', randint(70,100)],['e', randint(70,100)],['r', randint(70,100)]]
        return predictionsList

class MainProcessor:
    def process(self, jsonBitmap):
        
        # // whatever ...
        print(jsonBitmap)
        h = jsonBitmap['height']
        w = jsonBitmap['width']
        pixelsList = jsonBitmap['pixels']
        pixels = []
        for pixel in pixelsList:
            pixels.append(PixelMapper.map(pixel))
        
        predictionsList = HardProcessor.process(h,w,pixels)
        predictionsListFormatted = []
        
        for prediction in predictionsList:
            predictionsListFormatted.append(PredictionMapper.map(prediction))
                
        return predictionsListFormatted


#import time
# just for testing concurency

def process(completeMessageJSON):
    
    print("IN PROCESS: ", completeMessageJSON)
    
    # THIS IS TEMPORARY
    #time.sleep(10)
    # this proved that concurency is not good enough. execution is serialized.
    
    jsonBitmap = completeMessageJSON['bitmap']
    jsonToken = completeMessageJSON['token']
    
    listPredictions = mainProcessor.process(jsonBitmap)
    
    formattedMessage = {"preds": listPredictions, "token": jsonToken}
    message = json.dumps(formattedMessage)
    
    # PUBLISHER
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    
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
    
    # this is good concurency
    executor.submit(process, parsedMessageJSON)
    
    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)


if __name__ == '__main__':
    mainProcessor = MainProcessor()
    executor = ThreadPoolExecutor(5)
    
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    print(' [*] Waiting for messages. To exit press CTRL+C')
    
    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)
    
    channel.start_consuming()

