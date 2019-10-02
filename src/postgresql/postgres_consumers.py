import json
import requests
import flask
from kafka import KafkaConsumer
from postgres_helpers import consume_upsert


KAFKA_BROKER = 'localhost'
KAFKA_PORT = '9092'

def postgres_consumer(topic, group_id):
    consumer = KafkaConsumer(topic,
                            group_id=group_id, 
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        msg = message.value
        id = consume_upsert(msg)