import json
import requests

def api_request(json_info, ip_address, port):
    info = json.load(json_info)
    url = 'http://' + ip_address + ":" + port + "/inceptionV3/" + info["job_type"]
    data = {
        'api_dev_key': 'b442c74e8e250dec65e401712f82ff9a',
        'api_option': 'paste',
        }
    info_headers = {
        'Cache-Control': 'no-cache',
        'content-type': 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    }
    info_files = {
        'train_bucket_name': info['bucket_name'],
        'train_bucket_prefix': info['bucket_prefix']
    }
    response = requests.post(url, headers = info_headers, files = info_files)

    return response

data = {
    'bucket_name': 'test-sherlock', 
    'bucket_prefix': 'models/test-model',
    'job_type': 'transfer'}
with open('test.json', 'w') as outfile:
    json.dump(data, outfile)
with open('test.json', 'r') as infile:
    print(api_request(infile, '127.0.0.1', '3031'))