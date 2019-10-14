import json
import psycopg2
from psycopg2 import sql
import uuid


def consume_upsert(msg):
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='localhost', 
                                port='1324', 
                                password='default')

        curs = conn.cursor()
        sql = """UPDATE model_info SET (classes, val_acc, train_acc) = (%s,%s,%s) WHERE model_name = %s; 
                INSERT INTO model_info (model_name, imageset_name, classes, val_acc, train_acc)
                SELECT %s, %s, %s, %s, %s
                WHERE NOT EXISTS (SELECT 1 FROM model_info WHERE model_name = %s);"""
        params = (json.dumps(msg['classes']),
                msg['val_acc'],
                msg['train_acc'],
                msg['model_name'],
                msg['model_name'],
                msg['imageset_name'],
                json.dumps(msg['classes']),
                msg['val_acc'],
                msg['train_acc'],
                msg['model_name']
                )
        curs.execute(sql, params)
        conn.commit()
        curs.close()
        conn.close()

def label_calcs(labels):
        cls_count = {}
        conf_scores = {}
        sum_probs = 0
        for label in labels:
                cls_count[label[0]['label']] = cls_count.get(label[0]['label'], 0) + 1
                conf_scores[label[0]['label']] = conf_scores.get(label[0]['label'], 0) + label[0]['prob']
                sum_probs += label[0]['prob']
        for label in conf_scores:
                conf_scores[label] = conf_scores[label]/cls_count[label]
        
        return cls_count, sum_probs/len(labels), conf_scores

def add_confidence(model_name, avg_prob):
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='localhost', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        sql = """UPDATE model_info SET avg_prob = %s WHERE model_name = %s;"""
        params = (avg_prob, model_name)
        curs.execute(sql, params)
        conn.commit()
        curs.close()
        conn.close()


def stat_update(model_name, imageset_name, cls_count, conf_scores):
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='localhost', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        for label in cls_count:
                sql_update = """INSERT INTO label_count(model_name, label, count, imageset_name, confidence_score)
                                VALUES(%s, %s, %s, %s, %s)
                                ON CONFLICT (model_name, label, imageset_name) DO UPDATE
                                SET (count,confidence_score) = (label_count.count + %s, %s) 
                                WHERE (label_count.model_name, label_count.label, label_count.imageset_name)= (%s, %s, %s);"""
                params = (model_name,
                        label,
                        cls_count[label],
                        imageset_name,
                        conf_scores[label],
                        cls_count[label],
                        conf_scores[label],
                        model_name,
                        label,
                        imageset_name)
                curs.execute(sql_update, params)
                conn.commit()
        curs.close()
        conn.close()

def get_counts(model_name, imageset_name):
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='10.0.0.13', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        sql = """SELECT label, count
                FROM label_count
                WHERE (model_name, imageset_name) = (%s, %s);"""
        curs.execute(sql, (model_name, imageset_name))
        counts = curs.fetchall()
        return counts

def get_latest():
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='10.0.0.13', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        sql = """SELECT * FROM model_info ORDER BY train_time DESC LIMIT 5;"""
        curs.execute(sql)
        latest = curs.fetchall()
        return latest

def get_models_and_labels():
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='10.0.0.13', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        sql = """SELECT model_name FROM model_info;"""
        curs.execute(sql)
        models = curs.fetchall()
        sql = """SELECT imageset_name FROM model_info;"""
        curs.execute(sql)
        imagesets = curs.fetchall()
        return models, imagesets

def main():
        msg = {'model_name': 'test-name', 'imageset_name': 'test-imageset', 'classes': {'0': 'class'}, 'val_acc': .6, 'train_acc': .5}
        consume_upsert(msg)

if __name__ == "__main__":
        main()