#!/usr/bin/env python3

import json
import requests
import flask
from kafka import KafkaConsumer
import postgres_helpers
from threading import Thread


KAFKA_BROKER = 'ec2-35-162-75-2.us-west-2.compute.amazonaws.com'
KAFKA_PORT = '9092'

def transfer_consumer():
    consumer = KafkaConsumer('pg_transfer',
                            group_id='sherlock', 
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        print("** Consuming transfer results **")
        msg = message.value
        postgres_helpers.consume_upsert(msg)
        print("** Transfer results consumed **")

def label_consumer():
    consumer = KafkaConsumer('pg_label',
                            group_id='sherlock',
                            bootstrap_servers=[KAFKA_BROKER + ":" + KAFKA_PORT],
                            auto_offset_reset='earliest',
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')))

    for message in consumer:
        print("** Consuming labels **")
        msg = message.value
        cls_count, avg_prob, conf_scores = postgres_helpers.label_calcs(msg['results'])
        postgres_helpers.stat_update(msg['model_name'], msg['imageset_name'], cls_count, conf_scores)
        postgres_helpers.add_confidence(msg['model_name'], avg_prob)
        print("** Labels consumed **")

def main():
    Thread(target = transfer_consumer).start()
    Thread(target = label_consumer).start()
    print("** Running PostgreSQL consumers **")

if __name__ == "__main__":
    main()