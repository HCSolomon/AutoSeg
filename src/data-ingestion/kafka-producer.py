import json
import requests
import flask
from kafka import KafkaProducer


KAFKA_BROKER = 'localhost'
KAFKA_PORT = '9092'


producer = KafkaProducer(bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT])
producer = KafkaProducer(value_serializer=lambda m: json.dumps(m).encode('utf-8'))

data = {
    'bucket_name': 'test-sherlock', 
    'bucket_prefix': 'models/test-model',
    'job_type': 'transfer'
    }

for i in range(1):
    producer.send('ml_job', data).get(timeout=20)