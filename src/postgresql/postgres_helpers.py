import json
import psycopg2
import uuid


def consume_upsert(msg):
        conn = psycopg2.connect(database='sherlockdb', 
                                user='postgres', 
                                host='ec2-34-220-127-34.us-west-2.compute.amazonaws.com', 
                                port='1324', 
                                password='default')

        curs = conn.cursor()
        sql = """UPDATE model_info SET classes = %s WHERE model_id=%s; 
                INSERT INTO model_info (model_name, model_id, imageset_name, classes)
                SELECT %s, %s, %s, %s
                WHERE NOT EXISTS (SELECT 1 FROM model_info WHERE model_id=%s);"""
        params = (json.dumps(msg['classes']), 
                msg['model_id'], 
                msg['model_name'],
                msg['model_id'],
                msg['imageset_name'],
                json.dumps(msg['classes']),
                msg['model_id']
                )
        curs.execute(sql, params)
        conn.commit()
        curs.close()
        conn.close()
        return msg['model_id']

def label_calcs(labels):
        cls_count = {}
        sum_probs = 0
        prob_count = 0
        for item in labels:
                cls_count[item[0]['label']] = cls_count.get(item[0]['label'], 0) + 1
                sum_probs += item[0]['prob']
                prob_count += 1
        
        return cls_count, sum_probs/prob_count

def stat_update(model_name, cls_count):
        conn = psycopg2.connect(database='sherlockdb', 
                                user='postgres', 
                                host='localhost', 
                                port='1324', 
                                password='default')
        curs = conn.cursor()
        sql_table = """CREATE TABLE IF NOT EXISTS %s(
                        label text,
                        count integer,
                        PRIMARY KEY label
                        );"""
        curs.execute(sql_table, (model_name,))
        conn.commit()
        for label in cls_count:
                sql_update = """UPDATE %s
                                SET count = count + %i
                                WHERE label = '%s';"""
                params = (model_name, cls_count[label], label)
                conn.execute(sql_update, params)
                conn.commit()
        curs.close()
        conn.close()
