import json
import requests
import flask
from kafka import KafkaConsumer
from time import sleep


KAFKA_BROKER = 'localhost'
KAFKA_PORT = '9092'


def api_request(json_info, ip_address, port):
    url = 'http://' + ip_address + ":" + port + "/inceptionV3/" + json_info["job_type"]
    info_files = {
        'train_bucket_name':  json_info['bucket_name'],
        'train_bucket_prefix':  json_info['bucket_prefix']
    }
    response = requests.post(url, data = info_files)
    return response

consumer = KafkaConsumer('ml_job',
                        group_id='sherlock', 
                        bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                        auto_offset_reset='earliest',
                        value_deserializer=lambda m: json.loads(m.decode('utf-8')))


for message in consumer:
    print(message.value)
    request_info = message.value
    api_request(request_info, KAFKA_BROKER, '3031')
    sleep(20)