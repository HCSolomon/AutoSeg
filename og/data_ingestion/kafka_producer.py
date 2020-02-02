import json
import requests
import flask
import uuid
from kafka import KafkaProducer
from time import sleep


KAFKA_BROKER = 'ec2-35-162-75-2.us-west-2.compute.amazonaws.com'
KAFKA_PORT = '9092'


def input_producer(bucket_name, model_name, bucket_prefix, job_type):
    producer = KafkaProducer(bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                        value_serializer=lambda m: json.dumps(m).encode('utf-8'))
    data = {
        'bucket_name': bucket_name,
        'model_name': model_name,
        'bucket_prefix': bucket_prefix,
        'job_type': job_type
        }
    producer.send('ml_job', data).get(timeout=30)
    return data