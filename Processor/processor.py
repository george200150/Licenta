"""
Created on 24 iul. 2020

@author: George
"""
import json
from concurrent.futures.thread import ThreadPoolExecutor

import pika
import pytesseract
from PIL import Image

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"


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
        ################################################################################################################

        information = pytesseract.image_to_data(img)  # , lang, config, nice, output_type, timeout, pandas_config)
        print(information)
        print()

        # TODO: this should be thoroughly processed and handled with great care

        outputedText = pytesseract.image_to_string(img)
        print(outputedText)
        ################################################################################################################

        # predictionsList = [['a', randint(70, 100)], ['c', randint(70, 100)], ['e', randint(70, 100)],
        #                    ['r', randint(70, 100)]]
        predictionsList = RGBpixels
        return predictionsList


class MainProcessor:
    def process(self, jsonBitmap):

        # // whatever ...
        # print(jsonBitmap)
        h = jsonBitmap['height']
        w = jsonBitmap['width']
        pixelsList = jsonBitmap['pixels']



        sparsePixels = []
        RGBpixels = []
        # for pixel in pixelsList:
        #     pixels.append(PixelMapper.map(pixel))
        index = 0
        try:
            while True:
                r = pixelsList[index]
                g = pixelsList[index + 1]
                b = pixelsList[index + 2]
                index += 3
                RGBpixels.append((r, g, b))
                sparsePixels.append(r)
                sparsePixels.append(g)
                sparsePixels.append(b)
                pass
        except IndexError:
            pass  # finished pixels

        predictionsList = HardProcessor.process(h, w, RGBpixels)
        predictionsListFormatted = []

        # for prediction in predictionsList:
        #     predictionsListFormatted.append(PredictionMapper.map(prediction))

        # print(len(predictionsListFormatted))
        # return predictionsListFormatted[:100]
        print(len(predictionsList))
        # return predictionsList
        print(sparsePixels)
        return h, w, sparsePixels
        # return predictionsList[:len(predictionsList)//2]  # RAW PIXELS ( list of [r,g,b,r,g,b,r,g,b,r,g,b,...,r,g,b] )


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
    mainProcessor = MainProcessor()
    executor = ThreadPoolExecutor(5)

    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    print(' [*] Waiting for messages. To exit press CTRL+C')

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

    channel.start_consuming()
