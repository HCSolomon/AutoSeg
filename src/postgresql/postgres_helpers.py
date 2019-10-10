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
        sum_probs = 0
        prob_count = 0
        for item in labels:
                cls_count[item[0]['label']] = cls_count.get(item[0]['label'], 0) + 1
                sum_probs += item[0]['prob']
                prob_count += 1
        
        return cls_count, sum_probs/prob_count

def stat_update(model_name, imageset_name, cls_count):
        conn = psycopg2.connect(database='watsondb', 
                                user='postgres', 
                                host='localhost', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        for label in cls_count:
                sql_update = """INSERT INTO label_count(model_name, label, count, imageset_name)
                                VALUES(%s, %s, %s, %s)
                                ON CONFLICT (model_name, label, imageset_name) DO UPDATE
                                SET count = label_count.count + %s 
                                WHERE (label_count.model_name, label_count.label, label_count.imageset_name)= (%s, %s, %s);"""
                params = (model_name,
                        label,
                        cls_count[label],
                        imageset_name,
                        cls_count[label],
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

def main():
        msg = {'model_name': 'test-name', 'imageset_name': 'test-imageset', 'classes': {'0': 'class'}, 'val_acc': .6, 'train_acc': .5}
        consume_upsert(msg)

if __name__ == "__main__":
        main()