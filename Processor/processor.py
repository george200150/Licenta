"""
Created on 24 iul. 2020

@author: George
"""
import json
from concurrent.futures.thread import ThreadPoolExecutor

import pika
from PIL import Image

from ResNeSt.demo import loadModel, inference


class PredictionMapper:
    @staticmethod
    def map(pixel):
        pixDict = {'R': pixel[0], 'G': pixel[1], 'B': pixel[2]}
        return pixDict


class PixelMapper:
    @staticmethod
    def map(pixel):
        r = int(pixel['R'])
        g = int(pixel['G'])
        b = int(pixel['B'])
        return r, g, b


class MachineLearningProcessor:
    @staticmethod
    def process(width, height, RGBpixels):
        img = Image.new('RGB', (width, height), "black")
        pixels = img.load()

        for i in range(0, width * height):
            pixels[i % width, i // width] = RGBpixels[i]

        img.save('C:/Users/George/bsc/Licenta/Processor/TEMP.png')
        ################################################################################################################

        # TODO: insert cool ML image processing algorithm here
        inference(model,
                  filename='C:/Users/George/bsc/Licenta/Processor/TEMP.png',
                  outputFolder='C:/Users/George/bsc/Licenta/Processor/')

        predImg = Image.open(r'C:/Users/George/bsc/Licenta/Processor/output.png')
        predImg = predImg.convert('RGB')

        predClassesR = list(predImg.getdata(0))
        predClassesG = list(predImg.getdata(1))
        predClassesB = list(predImg.getdata(2))


        predClasses = []
        for (r, g, b) in zip(predClassesR, predClassesG, predClassesB):
            predClasses.append(r)
            predClasses.append(g)
            predClasses.append(b)
        ################################################################################################################

        predictionsList = predClasses
        return predictionsList


class MainProcessor:
    def process(self, jsonBitmap):

        h = jsonBitmap['height']
        w = jsonBitmap['width']
        pixelsList = jsonBitmap['pixels']

        RGBpixels = []  # these are needed for PIL image creation
        index = 0
        while index + 2 < len(pixelsList):
            r = pixelsList[index]
            g = pixelsList[index + 1]
            b = pixelsList[index + 2]
            index += 3
            RGBpixels.append((r, g, b))

        outputBitmap = MachineLearningProcessor.process(w, h, RGBpixels)  # ML image processing algoirthm
        return outputBitmap


# import time
# just for testing concurency

def process(completeMessageJSON):
    # print("IN PROCESS: ", completeMessageJSON)

    # THIS IS TEMPORARY
    # time.sleep(10)
    # this proved that concurency is not good enough. execution is serialized.

    jsonBitmap = completeMessageJSON['bitmap']
    w = jsonBitmap['width']
    h = jsonBitmap['height']

    jsonToken = completeMessageJSON['token']

    listPredictions = mainProcessor.process(jsonBitmap)


    formattedMessage = {"h": h, "w": w, "preds": listPredictions, "token": jsonToken}
    message = json.dumps(formattedMessage)

    # PUBLISHER
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    channel.basic_publish(exchange='JavaExchange.IN', routing_key='to.java.routing.key', body=message,
                          properties=pika.BasicProperties(
                              delivery_mode=2,  # make message persistent
                          ))
    connection.close()


# LISTENER
def callback(ch, method, properties, body):

    parsedMessageString = json.loads(body)  # - from bytes to string
    parsedMessageJSON = json.loads(parsedMessageString)  # - from string to dict

    # this is good concurency
    # executor.submit(process, parsedMessageJSON)

    # DEBUG no concurency
    process(parsedMessageJSON)

    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)


if __name__ == '__main__':
    global model
    model = loadModel()

    mainProcessor = MainProcessor()
    executor = ThreadPoolExecutor(5)

    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    print(' [*] Waiting for messages. To exit press CTRL+C')

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

    channel.start_consuming()
