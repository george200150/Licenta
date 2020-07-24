'''
Created on 23 iul. 2020

@author: George
'''

#!/usr/bin/env python
import pika
import time
import json


connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
print(' [*] Waiting for messages. To exit press CTRL+C')



def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)
    
    parsedJSON = json.loads(body) # - from bytes to string
    print(parsedJSON)
    
    time.sleep(body.count(b'.'))
    print(" [x] Done")
    ch.basic_ack(delivery_tag=method.delivery_tag)


channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='Licenta.PythonQueue', on_message_callback=callback)

channel.start_consuming()