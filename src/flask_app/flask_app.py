from flask import Flask, Markup, render_template
import sys
sys.path.append('/home/ubuntu/Watson')
from src.postgresql.postgres_helpers import get_counts, get_latest

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
    models = get_latest()
    model_labels = []
    model_accs = []
    for model in models:
        model_labels.append(model[0])
        model_accs.append(model[4] * 100)
    return render_template('main.html', title='Accuracies of Latest Trained Models', max=100, values=model_accs, labels=model_labels)

@app.route('/<model_name>')
def label_stats(model_name):
    class_counts = get_counts(model_name)
    pie_labels = []
    pie_values = []
    for label in class_counts:
        pie_labels.append(label[0])
        pie_values.append(label[1])
    models = get_latest()
    model_labels = []
    model_accs = []
    for model in models:
        model_labels.append(model[0])
        model_accs.append(model[4] * 100)
    return render_template('main.html', title='Class Composition of '+model_name, piemax=17000, barmax=100, set=zip(pie_values, pie_labels, colors), values=model_accs, labels=model_labels)    

if __name__ == "__main__":
    app.run(host='0.0.0.0')