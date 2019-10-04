import json
import requests
import flask
from kafka import KafkaConsumer
import postgres_helpers


KAFKA_BROKER = 'ec2-35-162-75-2.us-west-2.compute.amazonaws.com'
KAFKA_PORT = '9092'

def transfer_consumer():
    consumer = KafkaConsumer('pg_transfer',
                            group_id='sherlock', 
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        msg = message.value
        postgres_helpers.consume_upsert(msg)

def label_consumer():
    consumer = KafkaConsumer('pg_label',
                            group_id='sherlock',
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        msg = message.value
        cls_count, avg_prob = postgres_helpers.label_calcs(msg['results'])