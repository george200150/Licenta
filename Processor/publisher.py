'''
Created on 23 iul. 2020

@author: George
'''

#!/usr/bin/env python
import pika
import json

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()


message = {"preds": [{"character": "a", "percentage": 100}, {"character": "b", "percentage": 70}], "token": {"message": "123hash123_TOKEN_456hash456"}}
stringMessage = json.dumps(message)

channel.basic_publish(exchange='JavaExchange.IN', routing_key='to.java.routing.key', body=stringMessage, properties=pika.BasicProperties(
delivery_mode=2,  # make message persistent
    ))
print(" [x] Sent %r" % stringMessage)
connection.close()