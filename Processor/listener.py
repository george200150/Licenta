'''
Created on 23 iul. 2020

@author: George
'''

#!/usr/bin/env python
import pika
import time

# rabbitmqctl set_permissions -p guest ".*" ".*" ".*"

queue = 'Licenta.PythonQueue'
exchange = 'Licenta.IN'
routing_key = 'to.python.routing.key'
arguments = '{"x-message-ttl":172800000,"x-dead-letter-routing-key":"to.python.routing.key","x-dead-letter-exchange":"PythonExchange.DL"}'

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

#channel

#channel.queue_declare(queue='Licenta.PythonQueue', durable=True)
#channel.queue_bind(queue, exchange, routing_key, '')
print(' [*] Waiting for messages. To exit press CTRL+C')


def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)
    time.sleep(body.count(b'.'))
    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)


channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

channel.start_consuming()