"""
Created on 24 iul. 2020

@author: George
"""
import json
from concurrent.futures.thread import ThreadPoolExecutor

import pika
from PIL import Image

from ResNeSt.demo import loadModel as ResNeSt_loadModel, inference as ResNeSt_inference
from MDEQ.isolated import loadModel as MDEQ_loadModel, inference as MDEQ_inference


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
    def process(width, height, RGBpixels, method):
        img = Image.new('RGB', (width, height), "black")
        pixels = img.load()

        for i in range(0, width * height):
            pixels[i % width, i // width] = RGBpixels[i]

        ################################################################################################################
        # TODO: insert cool ML image processing algorithm here
        # ResNeSt (works fine with any dimension)
        # MDEQ (allows only LANDSCAPE 2048x1024)

        global model
        if method == 0:
            model = ResNeSt_loadModel()
            width, height, predImg = ResNeSt_inference(model, img)
            print(' [x] ResNeSt')
        elif method == 1:
            model = MDEQ_loadModel()
            width, height, predImg = MDEQ_inference(model, img)
            print(' [x] MDEQ')
        else:
            raise Exception("Bad Method!")

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
        return width, height, predictionsList


class MainProcessor:
    def process(self, jsonBitmap, method):
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

        width, height, outputBitmap = MachineLearningProcessor.process(w, h, RGBpixels, method)  # ML image processing algoirthm
        return width, height, outputBitmap


def process(completeMessageJSON):
    jsonBitmap = completeMessageJSON['bitmap']

    method = completeMessageJSON['method']['method']

    jsonToken = completeMessageJSON['token']

    width, height, listPredictions = mainProcessor.process(jsonBitmap, method)


    formattedMessage = {"h": height, "w": width, "preds": listPredictions, "token": jsonToken}
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

    process(parsedMessageJSON)

    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)


if __name__ == '__main__':
    # global model
    # model = loadModel()

    mainProcessor = MainProcessor()
    executor = ThreadPoolExecutor(5)

    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    print(' [*] Waiting for messages. To exit press CTRL+C')

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

    channel.start_consuming()
