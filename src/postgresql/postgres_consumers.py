import json
import requests
import flask
from kafka import KafkaConsumer


KAFKA_BROKER = 'ec2-35-162-75-2.us-west-2.compute.amazonaws.com'
KAFKA_PORT = '9092'

def sherlock_consumer(topic, group_id):
    consumer = KafkaConsumer(topic,
                            group_id=group_id, 
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        print(message.value)
        request_info = message.value
        api_request(request_info, 'localhost', '3031')