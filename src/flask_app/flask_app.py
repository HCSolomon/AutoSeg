from flask import Flask, Markup, render_template
import sys
sys.path.append('/home/ubuntu/Watson')
from src.postgresql.postgres_helpers import get_counts

app = Flask(__name__)

labels = [
    'JAN', 'FEB', 'MAR', 'APR',
    'MAY', 'JUN', 'JUL', 'AUG',
    'SEP', 'OCT', 'NOV', 'DEC'
]

values = [
    967.67, 1190.89, 1079.75, 1349.19,
    2328.91, 2504.28, 2873.83, 4764.87,
    4349.29, 6458.30, 9907, 16297
]

colors = [
    "#F7464A", "#46BFBD", "#FDB45C", "#FEDCBA",
    "#ABCDEF", "#DDDDDD", "#ABCABC", "#4169E1",
    "#C71585", "#FF4500", "#FEDCBA", "#46BFBD"]

@app.route('/')
def homepage():
    class_counts = get_counts('base')
    pie_labels = []
    pie_values = []
    for label in class_counts:
        pie_labels.append(label[0])
        pie_values.append(label[1])
    return render_template('main.html', title='Class Composition of Current Dataset', max=17000, set=zip(pie_values, pie_labels, colors))    

if __name__ == "__main__":
    app.run(host='0.0.0.0')