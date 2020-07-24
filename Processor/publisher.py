'''
Created on 23 iul. 2020

@author: George
'''

#!/usr/bin/env python
import pika

connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

#channel.queue_declare(queue='Licenta.JavaQueue', durable=True)

# TODO: parse the JSON correctly
message = '{"preds": [], "token": {"message": ""}}'
channel.basic_publish(
    exchange='JavaExchange.IN',
    routing_key='to.java.routing.key',
    body=message,
    properties=pika.BasicProperties(
        delivery_mode=2,  # make message persistent
    ))
print(" [x] Sent %r" % message)
connection.close()