import json
import requests
import flask
from kafka import KafkaConsumer
from time import sleep


KAFKA_BROKER = 'ec2-35-162-75-2.us-west-2.compute.amazonaws.com'
KAFKA_PORT = '9092'


def api_request(json_info, ip_address, port):
    url = 'http://' + ip_address + ":" + port + "/inceptionV3/" + json_info["job_type"]
    info_files = {
        'bucket_name':  json_info['bucket_name'],
        'bucket_prefix':  json_info['bucket_prefix'],
        'model_name': json_info['model_name']
    }
    response = requests.post(url, data = info_files)
    return response

def sherlock_consumer():
    consumer = KafkaConsumer('ml_job',
                            group_id = 'watsondb', 
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        print(message.value)
        request_info = message.value
        api_request(request_info, 'a545b6191e7c311e980d502a57ca1ca7-396068562.us-west-2.elb.amazonaws.com', '8080')
    