"""
Created on 24 iul. 2020

@author: George
"""
import json
import math
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


class HardProcessor:
    @staticmethod
    def process(height, width, RGBpixels):
        img = Image.new('RGB', (width, height), "black")  # Create a new black image
        pixels = img.load()  # Create the pixel map
        print("height = ", height)
        print("width = ", width)

        for i in range(0, width * height):
            pixels[i % width, i // width] = RGBpixels[i]
        # DEBUG
        img.show()
        img.save('C:/Users/George/bsc/Licenta/Processor/TEMP.png')
        ################################################################################################################

        # TODO: insert cool ML image processing algorithm here
        inference(model,
                  filename='C:/Users/George/bsc/Licenta/Processor/TEMP.png',
                  outputFolder='C:/Users/George/bsc/Licenta/Processor/')

        predImg = Image.open(r'C:/Users/George/bsc/Licenta/Processor/output.png')
        predImg = predImg.convert('RGB')
        predImg.show()

        # resize image due to Network Transport Capacity limitations
        w, h = predImg.size
        aspectRation = w / h

        if h < w:
            h = 100
            w = math.floor(h * aspectRation)
        else:
            w = 100
            h = math.floor(w * aspectRation)

        size = (w, h)
        print('size = ', size)
        predImg.thumbnail(size)

        predClassesR = list(predImg.getdata(0))
        predClassesG = list(predImg.getdata(1))
        predClassesB = list(predImg.getdata(2))
        predImg.show()

        print(predImg.size)
        print("length of R pixels = ", len(predClassesR))
        print("length of G pixels = ", len(predClassesG))
        print("length of B pixels = ", len(predClassesB))

        predClassesRGB = [(x, y, z) for (x, y, z) in zip(predClassesR, predClassesG, predClassesB)]

        predClasses = []
        for rgbPix in predClassesRGB:
            predClasses.append(rgbPix[0])
            predClasses.append(rgbPix[1])
            predClasses.append(rgbPix[2])

        # index = 0
        # formattedPrediction = []
        # while index + 2 < height * width:
        #     r = predClasses[index]
        #     g = predClasses[index + 1]
        #     b = predClasses[index + 2]
        #     formattedPrediction.append((r, g, b))
        #     index += 3

        # map colors to palette

        # mappedPixels = []
        # from ResNeSt.demo import adepallete

        # for pixel in predClasses:
        #     mappedPixels.append(adepallete[pixel])

        ################################################################################################################

        predictionsList = predClasses
        return h, w, predictionsList


class MainProcessor:
    def process(self, jsonBitmap):

        h = jsonBitmap['height']
        w = jsonBitmap['width']
        pixelsList = jsonBitmap['pixels']

        RGBpixels = []  # these are needed for PIL image creation
        index = 0
        try:
            while True:
                r = pixelsList[index]
                g = pixelsList[index + 1]
                b = pixelsList[index + 2]
                index += 3
                RGBpixels.append((r, g, b))
        except IndexError:
            pass  # finished pixels

        print("length of RGBpixels = ", len(RGBpixels))
        h, w, outputBitmap = HardProcessor.process(h, w, RGBpixels)  # ML image processing algoirthm

        print("length of outputBitmap = ", len(outputBitmap))
        return h, w, outputBitmap  # RAW PIXELS ( list of [r,g,b,r,g,b,r,g,b,r,g,b,...,r,g,b] )


# import time
# just for testing concurency

def process(completeMessageJSON):
    # print("IN PROCESS: ", completeMessageJSON)

    # THIS IS TEMPORARY
    # time.sleep(10)
    # this proved that concurency is not good enough. execution is serialized.

    jsonBitmap = completeMessageJSON['bitmap']
    jsonToken = completeMessageJSON['token']

    h, w, listPredictions = mainProcessor.process(jsonBitmap)

    formattedMessage = {"h": h, "w": w, "preds": listPredictions, "token": jsonToken}
    message = json.dumps(formattedMessage)

    # PUBLISHER
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    channel.basic_publish(exchange='JavaExchange.IN', routing_key='to.java.routing.key', body=message,
                          properties=pika.BasicProperties(
                              delivery_mode=2,  # make message persistent
                          ))
    # print(" [x] Sent %r" % message)
    connection.close()


# LISTENER
def callback(ch, method, properties, body):
    # print(" [x] Received %r" % body)

    parsedMessageString = json.loads(body)  # - from bytes to string
    parsedMessageJSON = json.loads(parsedMessageString)  # - from string to dict
    # print(parsedMessageJSON)

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
