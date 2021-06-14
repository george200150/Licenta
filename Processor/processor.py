"""
Created on 24 iul. 2020

@author: George
"""
import json
from concurrent.futures.thread import ThreadPoolExecutor

import pika
from PIL import Image
import cv2
import numpy as np

from ResNeSt.demo import loadModel as ResNeSt_loadModel, inference as resnest_inference
from MDEQ.isolated import loadModel as MDEQ_loadModel, inference as mdeq_inference
from DPT.dpt_multipurpose import loadModel as DPT_loadModel, inference as dpt_inference


def convert_pil_to_cv2_image(pil_img):  # only for DPT format adapting
    cv2_img = cv2.cvtColor(np.array(pil_img), cv2.COLOR_RGB2BGR)
    if cv2_img.ndim == 2:
        cv2_img = cv2.cvtColor(cv2_img, cv2.COLOR_GRAY2BGR)
    return cv2.cvtColor(cv2_img, cv2.COLOR_BGR2RGB) / 255.0


class MachineLearningProcessor:
    @staticmethod
    def process_bitmap(width, height, rgb_pixels, method):
        img = Image.new('RGB', (width, height), "black")
        pixels = img.load()

        for i in range(0, width * height):
            pixels[i % width, i // width] = rgb_pixels[i]

        ################################################################################################################
        # ResNeSt (fast - works fine with any dimension)
        # MDEQ (allows only LANDSCAPE 2048x1024) (problems resizing)
        # DPT (SOTA - accepts any image size)

        try:
            if method == 0:
                model = DPT_loadModel("DE")
                width, height, pred_img = dpt_inference("DE", model, convert_pil_to_cv2_image(img))
                print(' [x] DPT DE')
            elif method == 1:
                model = ResNeSt_loadModel()
                width, height, pred_img = resnest_inference(model, img)
                print(' [x] ResNeSt')
            elif method == 2:
                model = MDEQ_loadModel()
                width, height, pred_img = mdeq_inference(model, img)
                print(' [x] MDEQ')
            elif method == 3:
                model = DPT_loadModel("SS")
                width, height, pred_img = dpt_inference("SS", model, convert_pil_to_cv2_image(img))
                print(' [x] DPT SS')
            else:
                raise Exception("Bad Method!")
        except IndexError:
            img = Image.new('RGB', (400, 400), "black")
            model = ResNeSt_loadModel()
            width, height, pred_img = resnest_inference(model, img)

        pred_img = pred_img.convert('RGB')

        pred_classes_r = list(pred_img.getdata(0))
        pred_classes_g = list(pred_img.getdata(1))
        pred_classes_b = list(pred_img.getdata(2))

        pred_classes = []
        for (r, g, b) in zip(pred_classes_r, pred_classes_g, pred_classes_b):
            pred_classes.append(r)
            pred_classes.append(g)
            pred_classes.append(b)
        ################################################################################################################

        predictions_list = pred_classes
        return width, height, predictions_list


class BitmapFormatAdapter:
    @staticmethod
    def convert_flat_to_rgb(pixels_list):
        rgb_pixels = []  # these are needed for PIL image creation
        index = 0
        while index + 2 < len(pixels_list):
            r = pixels_list[index]
            g = pixels_list[index + 1]
            b = pixels_list[index + 2]
            index += 3
            rgb_pixels.append((r, g, b))
        return rgb_pixels


class QueueProxy:
    # LISTENER
    def listen(self):
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()
        print(' [*] Waiting for messages. To exit press CTRL+C')

        channel.basic_qos(prefetch_count=1)
        channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=self.deserialize_bytes)

        channel.start_consuming()

    def deserialize_bytes(self, ch, method, properties, body):
        parsed_message_string = json.loads(body)  # - from bytes to string
        parsed_message_json = json.loads(parsed_message_string)  # - from string to dict
        self.process_extracted_data_and_publish(parsed_message_json)
        print(" [x] Done")
        ch.basic_ack(delivery_tag=method.delivery_tag)

    def process_extracted_data_and_publish(self, complete_message_json):
        json_bitmap = complete_message_json['bitmap']
        method = complete_message_json['method']['method']
        json_token = complete_message_json['token']
        pixels_list = json_bitmap['pixels']
        rgb_pixels = BitmapFormatAdapter.convert_flat_to_rgb(pixels_list)

        # ML image processing algorithm
        h = json_bitmap['height']
        w = json_bitmap['width']
        width, height, list_predictions = MachineLearningProcessor.process_bitmap(w, h, rgb_pixels, method)

        formatted_message = {"h": height, "w": width, "preds": list_predictions, "token": json_token}
        message = json.dumps(formatted_message)
        self.publish(message)

    # PUBLISHER
    def publish(self, message):
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()
        channel.basic_publish(exchange='JavaExchange.IN', routing_key='to.java.routing.key', body=message,
                              properties=pika.BasicProperties(
                                  delivery_mode=2,  # make message persistent
                              ))
        connection.close()


class Main:
    @staticmethod
    def main():
        proxy = QueueProxy()
        proxy.listen()


if __name__ == '__main__':
    executor = ThreadPoolExecutor(16)
    Main.main()
