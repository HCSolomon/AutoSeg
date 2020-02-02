import psycopg2


conn = psycopg2.connect(database='sherlockdb', user='postgres', host='ec2-34-220-127-34.us-west-2.compute.amazonaws.com', port='1324', password='default')
curs = conn.cursor()
sql = """INSERT INTO model_info (model_name, imageset_name) VALUES (%s,%s) RETURNING model_id;"""
curs.execute(sql, ('test-model', 'test-imageset'))
model_id = curs.fetchone()[0]
conn.commit()
curs.close()
conn.close()