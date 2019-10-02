import json
import psycopg2


def consume_initial(msg):
    conn = psycopg2.connect(database='sherlockdb', 
                            user='postgres', 
                            host='localhost', 
                            port='1324', 
                            password='default')

    curs = conn.cursor()
    sql = """UPDATE model_info SET classes = %s WHERE model_id=%s; 
            INSERT INTO model_info (%s, %s)
            SELECT %s, %s, %s
            WHERE NOT EXISTS (SELECT 1 FROM model_info WHERE model_id=%s);"""
    dir = msg['bucket_prefix'].split('/')
    model_name = dir[-1]
    
    curs.execute(sql, (model_name, msg['imageset_name']))
    model_id = curs.fetchone()[0]
    conn.commit()
    curs.close()
    conn.close()
    return model_id

def consume_update(model_info):
    conn = psycopg2.connect(database='sherlockdb',
                            user='postgres',
                            host='localhost',
                            port='1324',
                            password='default')


    
    curs = conn.cursor()
    classes = model_info['classes']
    sql = """UPDATE model_info SET classes = %s;"""
    curs.execute(sql, (json.dumps(classes),))
    conn.commit()
    curs.close()
    conn.close()